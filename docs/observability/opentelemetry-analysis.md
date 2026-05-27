# Analisis de OpenTelemetry en dan-football-stats

## Objetivo de este documento

Este documento releva el estado actual del proyecto antes de incorporar OpenTelemetry. La idea es dejar claro:

- como esta armada hoy la arquitectura;
- que observabilidad ya existe;
- que falta para tener trazas distribuidas;
- donde conviene integrar OpenTelemetry sin agregar complejidad innecesaria.

El criterio usado es educativo: se prioriza una solucion simple, local, repetible y facil de explicar a estudiantes.

## Estructura actual del proyecto

### Microservicios principales

El repositorio tiene tres microservicios de negocio:

1. `core-registry-service`
   - Catalogo de ligas, equipos y jugadores.
   - Usa PostgreSQL.
   - Expone endpoints REST para ligas, equipos, jugadores y roster.

2. `live-engine-service`
   - Maneja partidos en vivo y publica eventos.
   - Usa MongoDB.
   - Expone endpoints REST para crear partidos, iniciarlos, registrar goles y finalizar partidos.

3. `stats-service`
   - Mantiene proyecciones de standings y goleadores.
   - Usa PostgreSQL.
   - Consume eventos de RabbitMQ.
   - Expone endpoints REST de lectura.

### Componentes de infraestructura ya presentes

- `discovery-server`
  - Servidor Eureka para service discovery.

- `api-gateway`
  - Spring Cloud Gateway como punto de entrada unico.
  - Enruta por prefijos:
    - `/core-registry/**`
    - `/live-engine/**`
    - `/stats/**`

## Frameworks y versiones detectadas

### Base comun

- Java 21
- Spring Boot `4.0.6`
- Spring Cloud `2025.1.1`

### Dependencias relevantes por servicio

- `core-registry-service`
  - `spring-boot-starter-web`
  - `spring-boot-starter-data-jpa`
  - `spring-boot-starter-actuator`
  - `micrometer-registry-prometheus`
  - Flyway + PostgreSQL

- `live-engine-service`
  - `spring-boot-starter-web`
  - `spring-boot-starter-data-mongodb`
  - `spring-boot-starter-amqp`
  - `spring-boot-starter-actuator`
  - `micrometer-registry-prometheus`

- `stats-service`
  - `spring-boot-starter-web`
  - `spring-boot-starter-data-jpa`
  - `spring-cloud-starter-openfeign`
  - `spring-cloud-starter-circuitbreaker-resilience4j`
  - `spring-boot-starter-amqp`
  - `spring-boot-starter-actuator`
  - `micrometer-registry-prometheus`
  - Flyway + PostgreSQL

- `api-gateway`
  - `spring-cloud-starter-gateway-server-webflux`
  - `spring-cloud-starter-loadbalancer`
  - `spring-cloud-starter-netflix-eureka-client`
  - `spring-boot-starter-actuator`
  - `micrometer-registry-prometheus`

- `discovery-server`
  - `spring-cloud-starter-netflix-eureka-server`
  - `spring-boot-starter-actuator`
  - `micrometer-registry-prometheus`

## Como se comunican los servicios

### Comunicacion sincrona HTTP

- `api-gateway` enruta trafico hacia los tres microservicios.
- `stats-service` llama a `core-registry-service` via Feign:
  - `GET /api/teams/{teamId}`
  - `GET /api/players/{playerId}`

Este es hoy el mejor flujo para una demo simple de trazas distribuidas, porque un request puede pasar por:

1. cliente
2. `api-gateway`
3. `stats-service`
4. `core-registry-service`

### Comunicacion asincrona por mensajeria

- `live-engine-service` publica eventos en RabbitMQ:
  - `GoalScoredEvent`
  - `MatchEndedEvent`

- `stats-service` consume esos eventos con `@RabbitListener` y actualiza sus tablas de proyeccion.

Este flujo es muy valioso para mostrar observabilidad, pero como primera iteracion educativa conviene tratarlo como complemento y no como camino principal de la demo, porque suma mas moving parts.

### Service discovery

- Los servicios se registran en Eureka.
- El gateway usa URIs tipo `lb://...`, por ejemplo:
  - `lb://CORE-REGISTRY-SERVICE`
  - `lb://LIVE-ENGINE-SERVICE`
  - `lb://STATS-SERVICE`

## Observabilidad actual

### Logging

- No aparece una configuracion custom de Logback.
- Se usa el logging por defecto de Spring Boot.
- `stats-service` tiene logs manuales en el consumidor Rabbit:
  - `Received GoalScoredEvent`
  - `Received MatchEndedEvent`

Conclusion: hay logs basicos, pero no hay correlacion distribuida visible ni estrategia de agregacion centralizada.

### Metrics

Ya existe una base de metricas razonable:

- Todos los componentes Spring Boot tienen:
  - `spring-boot-starter-actuator`
  - `micrometer-registry-prometheus`

- En `application.yml` de cada app se ve:
  - exposicion amplia de endpoints de actuator;
  - export Prometheus habilitado.

- Prometheus ya esta configurado para scrapear:
  - `discovery-server`
  - `core-registry-service`
  - `live-engine-service`
  - `stats-service`
  - `api-gateway`

### Dashboards

Ya existe una carpeta `monitoring/grafana/` con:

- provision de datasource Prometheus;
- provision de dashboards;
- dashboard `spring-boot-observability.json`.

### Docker y monitoreo

`docker-compose.yml` ya levanta:

- PostgreSQL para core
- PostgreSQL para stats
- MongoDB
- RabbitMQ
- Eureka
- API Gateway
- Prometheus
- Grafana

### Observabilidad ya existente

Si. Ya existe una observabilidad parcial:

- metricas con Actuator + Micrometer + Prometheus;
- visualizacion con Grafana;
- dashboard inicial;
- endpoints de prueba.

### Observabilidad que falta

No aparece configuracion actual de:

- OpenTelemetry;
- OTLP exporter;
- OpenTelemetry Collector;
- Tempo;
- trazas distribuidas end-to-end;
- propagacion de contexto configurada de forma explicita;
- agregacion centralizada de logs.

## Donde conviene integrar OpenTelemetry

## Decision principal

La integracion mas simple y pedagogica es:

1. mantener las metricas existentes por Prometheus;
2. agregar trazas distribuidas con OpenTelemetry usando soporte nativo de Spring Boot;
3. exportar esas trazas via OTLP hacia un OpenTelemetry Collector;
4. reenviar desde el Collector a Tempo;
5. visualizar todo desde Grafana.

## Puntos concretos de integracion

### 1. Microservicios de negocio

Hay que agregar tracing al menos en:

- `core-registry-service`
- `live-engine-service`
- `stats-service`

Cada uno ya tiene `spring.application.name`, asi que ya existe una base clara para el `service.name`.

### 2. API Gateway

Aunque no es uno de los tres microservicios de negocio, conviene instrumentarlo tambien porque:

- es el punto de entrada real para la demo;
- agrega un span muy facil de entender para estudiantes;
- permite ver la cadena completa cliente -> gateway -> servicio interno.

### 3. RabbitMQ

Se puede habilitar observacion de Spring AMQP para que:

- `RabbitTemplate` cree spans al publicar;
- `@RabbitListener` cree spans al consumir.

Esto sirve para mostrar que un evento tambien puede quedar trazado. Igual, para mantener el proyecto simple, la demo principal puede seguir siendo la ruta HTTP `gateway -> stats -> core-registry`.

## Que telemetria conviene recolectar

### Trazas

Se deberian recolectar si o si.

Motivo:

- es el eje del aprendizaje de OpenTelemetry;
- muestra como viaja una request entre servicios;
- permite explicar spans, parents, children y propagacion de contexto.

Fuentes esperadas de spans:

- requests HTTP entrantes;
- llamadas Feign salientes;
- requests manejadas por el gateway;
- opcionalmente publicacion y consumo de mensajes RabbitMQ.

### Metricas

Ya se recolectan y conviene conservarlas.

Motivo:

- ya estan integradas;
- completan la historia de observabilidad junto con las trazas;
- son faciles de relacionar con latencia, errores y throughput.

### Logs

Para esta iteracion conviene incluir logs agregados de una manera simple para poder mostrar el tercer pilar clasico de observabilidad.

Decision:

- agregar Loki;
- usar una recoleccion de logs local y facil de explicar;
- evitar pipelines complejos o parsing demasiado avanzado.

Justificacion:

- el proyecto es educativo y local;
- mostrar trazas, metricas y logs juntos en Grafana tiene mucho valor didactico;
- Loki se puede sumar sin cambiar demasiado el codigo de las aplicaciones si se usa un agente simple de recoleccion.

## Stack propuesta

### Componentes

- OpenTelemetry Collector
  - recibe OTLP desde las apps;
  - centraliza la salida;
  - simplifica la configuracion de los servicios.

- Tempo
  - backend de trazas.

- Prometheus
  - se mantiene como backend de metricas.

- Grafana
  - UI unificada para explorar metricas y trazas.

- Loki
  - backend de logs.
  - conviene incluirlo porque el usuario quiere mostrar como funciona.

- Promtail o equivalente simple
  - agente para leer logs de contenedores y enviarlos a Loki.

## Por que esta combinacion

- Es comun en entornos reales, pero sigue siendo simple para laboratorio local.
- Grafana permite mostrar todo desde una sola interfaz.
- El Collector permite explicar el patron "aplicaciones -> collector -> backend".
- No obliga a meter Kubernetes, sidecars ni configuraciones avanzadas.

## Enfoque recomendado para esta implementacion

### Camino principal de demo

Usar un flujo HTTP sincrono porque es el mas facil de entender:

1. request a `api-gateway`
2. el gateway reenvia a `stats-service`
3. `stats-service` consulta a `core-registry-service` por Feign
4. la traza completa aparece en Tempo/Grafana

### Camino complementario

Dejar documentado que tambien existe el flujo asincrono:

1. `live-engine-service` publica evento
2. `stats-service` consume evento
3. se actualiza el read model

Si la instrumentacion AMQP queda simple, se puede incluir como bonus didactico.

## Resumen de decisiones

- Ya hay metricas y dashboards basicos: no hace falta rehacer eso.
- Falta trazabilidad distribuida: ese es el hueco principal.
- La opcion mas simple es soporte nativo de Spring Boot + OTLP + Collector + Tempo.
- La demo principal deberia ser HTTP via gateway y Feign.
- Loki pasa a formar parte del stack educativo.
- La forma mas simple de mostrarlo es recolectar logs de contenedores y consultarlos en Grafana.
