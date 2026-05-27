# Guia educativa de OpenTelemetry

## Para que sirve esta guia

Esta guia explica, en un lenguaje simple y orientado a clase, como se usa OpenTelemetry dentro de este proyecto.

La idea no es mostrar una arquitectura de produccion, sino un laboratorio local donde se puedan ver:

- metricas;
- trazas distribuidas;
- logs centralizados;
- propagacion de contexto entre servicios.

## Que es OpenTelemetry

OpenTelemetry es un conjunto de estandares, APIs y herramientas para observar sistemas distribuidos.

En la practica, OpenTelemetry ayuda a que una aplicacion pueda emitir informacion sobre lo que esta pasando mientras corre, por ejemplo:

- cuanto tarda una request;
- por que servicio paso;
- si hubo errores;
- como se encadena una llamada con otra.

## Que es una trace

Una `trace` es la historia completa de una operacion distribuida.

Por ejemplo, si un cliente hace un request al gateway, ese request entra a `stats-service` y desde ahi sale otra llamada a `core-registry-service`, todo ese recorrido puede quedar agrupado dentro de una sola trace.

La trace permite responder preguntas como:

- por donde paso este request;
- en que servicio se fue mas tiempo;
- donde fallo;
- que parte fue llamada desde cual otra.

## Que es un span

Un `span` es una unidad de trabajo dentro de una trace.

Pensalo asi:

- la trace es la pelicula completa;
- cada span es una escena de esa pelicula.

Ejemplos de spans en este proyecto:

- el span del request que entra al gateway;
- el span del request que atiende `stats-service`;
- el span de la llamada HTTP saliente desde `stats-service` a `core-registry-service`;
- el span del request que atiende `core-registry-service`.

Cada span suele tener:

- nombre;
- hora de inicio;
- duracion;
- estado;
- atributos o tags.

## Que significa propagacion de contexto

La propagacion de contexto es el mecanismo que permite que varios servicios entiendan que estan participando de la misma operacion.

Sin propagacion:

- cada servicio ve su request como algo aislado;
- no se puede reconstruir bien el recorrido completo.

Con propagacion:

- el gateway recibe una request;
- manda headers de tracing al siguiente servicio;
- el siguiente servicio continua la misma trace en vez de crear una nueva;
- al final en Grafana/Tempo se ve el arbol completo.

En este proyecto, la propagacion principal se da en llamadas HTTP entre:

- `api-gateway`
- `stats-service`
- `core-registry-service`

Tambien puede verse en RabbitMQ cuando la observacion de mensajeria esta habilitada.

## Que hace el OpenTelemetry Collector

El OpenTelemetry Collector es un intermediario entre las aplicaciones y los backends de observabilidad.

En este proyecto cumple un rol muy importante porque simplifica la explicacion:

1. las apps envian trazas por OTLP;
2. el Collector las recibe;
3. el Collector las reenvia a Tempo.

Esto sirve para enseñar que las aplicaciones no siempre mandan datos directo al backend final.

## Que hace cada herramienta del stack

### Grafana

Grafana es la interfaz visual.

Se usa para:

- ver metricas desde Prometheus;
- explorar trazas desde Tempo;
- explorar logs desde Loki.

Es la puerta de entrada para estudiantes porque desde una sola UI pueden recorrer todo.

### Tempo

Tempo es el backend de trazas.

Guarda las traces y permite consultarlas desde Grafana.

En este proyecto se usa para visualizar el recorrido distribuido de una request.

### Prometheus

Prometheus es el backend de metricas.

Hace scrape de `/actuator/prometheus` en los servicios y guarda series temporales como:

- cantidad de requests;
- latencia;
- errores;
- uso de heap;
- CPU;
- threads.

### Loki

Loki es el backend donde se guardan los logs.

En este proyecto se usa para centralizar los logs de los contenedores y poder consultarlos desde Grafana.

### Promtail

Promtail es el agente que lee logs y los envia a Loki.

En este proyecto se usa porque es la forma mas simple de mostrar centralizacion de logs en un entorno Docker local.

Nota importante:

- Promtail esta deprecado en el ecosistema de Grafana;
- igual, para un proyecto educativo local sigue siendo una opcion simple de entender;
- mas adelante se podria migrar a Alloy si el curso quiere mostrar la opcion mas nueva.

## Como usa este proyecto cada herramienta

### Spring Boot Actuator + Micrometer

Cada servicio Spring Boot ya expone metricas mediante Actuator y Micrometer.

Eso se mantiene.

### OpenTelemetry en los servicios

Se agrego soporte de OpenTelemetry en:

- `core-registry-service`
- `live-engine-service`
- `stats-service`
- `api-gateway`

Cada app exporta trazas por OTLP al Collector.

### OpenTelemetry Collector

Se agrego un Collector local en Docker Compose para recibir trazas OTLP y reenviarlas a Tempo.

### Tempo

Se agrego Tempo como almacenamiento de trazas para consulta desde Grafana.

### Grafana

Grafana ya tenia datasource de Prometheus.

Ahora tambien tiene datasource de Tempo.
Ahora tambien tiene datasource de Loki.

### Loki y Promtail

Promtail lee logs de los contenedores Docker y los envia a Loki.

Eso permite que los estudiantes puedan:

- buscar logs por contenedor;
- ver logs del mismo rango temporal que una trace;
- comparar rapidamente logs, trazas y metricas.

### RabbitMQ

`live-engine-service` publica eventos y `stats-service` los consume.

En esta iteracion se habilito observacion del listener para que el flujo asincrono tambien tenga mejor visibilidad, pero la demo principal sigue siendo HTTP porque es mas facil de explicar.

## Arquitectura de observabilidad en este repo

```text
Cliente
  -> API Gateway
  -> stats-service
  -> core-registry-service

Servicios Spring Boot
  -> OTLP HTTP
  -> OpenTelemetry Collector
  -> Tempo
  -> Grafana

Servicios Spring Boot
  -> /actuator/prometheus
  -> Prometheus
  -> Grafana

Logs de contenedores Docker
  -> Promtail
  -> Loki
  -> Grafana
```

## Como levantar el entorno

Desde la raiz del repo:

```bash
docker compose up -d --build
```

## URLs utiles

- Eureka: [http://localhost:8761](http://localhost:8761)
- Prometheus: [http://localhost:9090](http://localhost:9090)
- Grafana: [http://localhost:3000](http://localhost:3000)

Credenciales por defecto de Grafana:

- usuario: `admin`
- password: `admin`

## Como probar metricas

Ejemplo:

- [http://localhost:8080/stats/actuator/prometheus](http://localhost:8080/stats/actuator/prometheus)

Tambien se pueden usar los dashboards ya provisionados en Grafana para ver:

- tasa de requests;
- latencia;
- errores;
- heap;
- CPU;
- threads.

## Como probar logs

1. generar algunas requests a los servicios;
2. abrir Grafana;
3. ir a `Explore`;
4. elegir datasource `Loki`;
5. probar una consulta simple como:

```text
{service=~".*stats-service.*"}
```

o bien:

```text
{container=~".*api-gateway.*"}
```

Como los logs salen por consola y Docker los captura, Loki termina mostrando los logs centralizados sin necesidad de escribir archivos dentro de las apps.

## Demo principal de trazas

## Opcion recomendada

Usar el endpoint de demo expuesto por `stats-service` a traves del gateway:

```bash
curl "http://localhost:8080/stats/api/stats/demo/trace?teamId=1&playerId=1"
```

Ese request hace este recorrido:

1. entra por `api-gateway`;
2. llega a `stats-service`;
3. `stats-service` llama a `core-registry-service`;
4. la respuesta devuelve tambien el `traceId`.

### Ejemplo de respuesta esperada

La respuesta deberia incluir algo parecido a:

```json
{
  "serviceName": "stats-service",
  "traceId": "9d7b0f7d6d8b5f8d2a8b4d4a8f1e2c3b",
  "spanId": "7f2f1b2c3d4e5f6a",
  "teamId": 1,
  "teamName": "Campus Jaguars",
  "teamShortName": "JAG",
  "playerId": 1,
  "playerName": "Luna Diaz",
  "note": "Esta respuesta existe para generar una traza simple stats-service -> core-registry-service."
}
```

Los valores exactos de `traceId` y `spanId` cambian en cada llamada.

## Como encontrar la trace en Grafana

### Metodo mas simple

1. abrir Grafana en [http://localhost:3000](http://localhost:3000)
2. ir a `Explore`
3. elegir el datasource `Tempo`
4. pegar el `traceId` devuelto por el endpoint
5. abrir la trace

### Que deberias ver

Como minimo deberian aparecer spans asociados a:

- `api-gateway`
- `stats-service`
- llamada cliente HTTP o Feign desde `stats-service`
- `core-registry-service`

## Como cruzar la trace con logs

Como configuramos correlacion de logs, las lineas de log incluyen:

- nombre del servicio;
- `traceId`;
- `spanId`.

Entonces, despues de encontrar una trace en Tempo, podes:

1. copiar el `traceId`;
2. ir a `Explore` con datasource `Loki`;
3. buscar ese identificador dentro de los logs recientes.

Eso ayuda mucho a mostrar en clase como se complementan trazas y logs.

## Flujo complementario con eventos

Ademas del flujo HTTP, el proyecto tiene un flujo asincrono:

1. `live-engine-service` registra acciones del partido;
2. publica eventos en RabbitMQ;
3. `stats-service` consume esos eventos;
4. actualiza standings y goleadores.

Eso sirve para mostrar que observabilidad no es solo REST, aunque para empezar es mejor mirar primero la demo del gateway.

## Por que se eligio esta implementacion

- porque usa herramientas conocidas y faciles de levantar localmente;
- porque mantiene separadas las responsabilidades;
- porque permite explicar muy bien que hace cada pieza;
- porque evita meter complejidad de produccion que para una clase inicial no suma tanto.

## Decisiones importantes

### Sampleo al 100%

Se configuro sampleo completo en local.

Motivo:

- en un proyecto educativo queremos ver todas las requests de la demo;
- en produccion esto normalmente se ajustaria para reducir costo y volumen.

### OTLP por HTTP

Se uso OTLP HTTP porque es simple de entender y configurar en Docker Compose.

### Loki simple en vez de pipeline complejo

Se eligio Loki con Promtail y logs de contenedores Docker.

Motivo:

- es suficiente para mostrar centralizacion de logs;
- evita cambiar demasiado el codigo de las apps;
- mantiene la configuracion entendible para estudiantes.

## Que pueden observar los estudiantes

Con esta implementacion los estudiantes pueden practicar:

- como identificar el recorrido completo de una request;
- como leer spans padre e hijos;
- como distinguir metricas de trazas;
- como ver latencia por servicio;
- como entender la diferencia entre flujo HTTP y flujo asincrono.

## Siguiente ejercicio sugerido

Una buena extension despues de esta base es:

1. generar eventos desde `live-engine-service`;
2. verificar el impacto en `stats-service`;
3. comparar metricas y trazas;
4. profundizar la correlacion de logs con `traceId`;
5. migrar de Promtail a Alloy si quieren mostrar una herramienta mas nueva.
