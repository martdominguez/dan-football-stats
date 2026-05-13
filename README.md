# dan-football-stats

Proyecto de ejemplo con tres microservicios:

- `core-registry-service`: catálogo de ligas, equipos y jugadores.
- `live-engine-service`: operación de partidos en vivo y publicación de eventos.
- `stats-service`: proyecciones de standings y goleadores.

## Ejecutar el proyecto

Levantar contenedores:

```bash
docker compose up -d --build
```

Bajar contenedores:

```bash
docker compose down -v
```

## Endpoints de prueba

Directo a cada servicio:

- [core-registry test](http://localhost:8081/api/test/context)
- [live-engine test](http://localhost:8082/api/test/context)
- [stats test](http://localhost:8083/api/test/context)

Vía API Gateway:

- [core-registry through gateway](http://localhost:8080/core-registry/api/test/context)
- [live-engine through gateway](http://localhost:8080/live-engine/api/test/context)
- [stats through gateway](http://localhost:8080/stats/api/test/context)

Eureka:

- [http://localhost:8761/](http://localhost:8761/)

Escalar un servicio:

```bash
docker compose scale stats-service=3
```

## Cambios incorporados

Se agregó un escenario de comunicación síncrona desde `stats-service` hacia `core-registry-service` para enriquecer las respuestas de estadísticas.

### 1. Enriquecimiento con Feign

`stats-service` ahora usa un cliente Feign contra `core-registry-service` para obtener datos canónicos de equipos y jugadores.

Esto se usa para enriquecer:

- `GET /stats/api/stats/standings`
- `GET /stats/api/stats/top-scorers`

En lugar de responder solo con la proyección local, `stats-service` intenta completar la salida con información del catálogo:

- `teamShortName` en standings;
- `position`, `shirtNumber` y `teamShortName` en top scorers;
- nombre canónico del jugador armado desde `firstName` + `lastName`.

### 2. Endpoint nuevo en Core Registry

Para soportar ese enriquecimiento, `core-registry-service` expone también:

- `GET /api/players/{playerId}`

Ese endpoint permite consultar un jugador puntual sin tener que leer la lista completa.

### 3. Circuit Breaker con Resilience4j

Sobre el cliente Feign se agregó un circuit breaker con Resilience4j.

Objetivo:

- evitar que `stats-service` quede atado a timeouts repetidos si `core-registry-service` está degradado;
- mantener disponibles los endpoints de estadísticas aunque falle el enriquecimiento remoto.

Cuando `core-registry-service` no responde o el circuito está abierto, `stats-service` hace fallback a sus datos locales proyectados:

- standings sigue devolviendo `teamName`, `leagueName` y métricas de puntos/goles;
- top scorers sigue devolviendo `playerName`, `teamName` y `goals`;
- los campos enriquecidos pueden salir en `null`.

## Flujo resultante

1. `live-engine-service` publica eventos en RabbitMQ.
2. `stats-service` consume esos eventos y mantiene sus proyecciones locales.
3. Cuando un cliente consulta standings o goleadores, `stats-service` responde desde su base propia.
4. Antes de devolver la respuesta, intenta enriquecerla consultando `core-registry-service` por Feign.
5. Si la llamada remota falla repetidamente, actúa el circuit breaker y se devuelve la respuesta degradada.

## Notas

- La proyección de stats sigue siendo el origen para standings y goleadores.
- El enriquecimiento remoto no reemplaza el flujo asíncrono con RabbitMQ; solo mejora la respuesta de lectura.
- El comportamiento degradado es intencional para privilegiar disponibilidad en `stats-service`.
