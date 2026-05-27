# Plan de implementacion de OpenTelemetry

## Objetivo

Agregar una stack minima y funcional de observabilidad distribuida para uso local y educativo, manteniendo el proyecto simple.

El resultado buscado es:

- metricas en Prometheus;
- trazas en Tempo;
- logs en Loki;
- visualizacion en Grafana;
- export OTLP via OpenTelemetry Collector;
- una demo clara que muestre un request atravesando multiples servicios.

## Alcance de la primera iteracion

### Incluye

- tracing en los 3 microservicios de negocio;
- tracing en `api-gateway` para que la demo sea mas clara;
- export OTLP al Collector;
- Tempo como backend de trazas;
- Loki como backend de logs;
- Promtail como agente simple de recoleccion de logs;
- datasource de Tempo en Grafana;
- datasource de Loki en Grafana;
- guia paso a paso para levantar y probar todo;
- demo HTTP reproducible;
- si queda simple, observacion de RabbitMQ.

### No incluye

- Kubernetes;
- alertas avanzadas;
- tuning de performance;
- muestreo complejo;
- agregacion de logs obligatoria;
- despliegue productivo.

## Dependencias requeridas

En cada aplicacion que vaya a exportar trazas:

- `org.springframework.boot:spring-boot-starter-opentelemetry`

Esto aprovecha el soporte oficial de Spring Boot 4 para OpenTelemetry con OTLP.

## Configuracion requerida en las apps

Agregar propiedades simples y explicitas:

- `management.tracing.sampling.probability=1.0`
  - para entornos educativos conviene samplear todo.

- `management.opentelemetry.tracing.export.otlp.endpoint=http://otel-collector:4318/v1/traces`
  - uso de OTLP HTTP por simplicidad.

- `management.opentelemetry.resource-attributes.service.name=...`
  - si hace falta reforzar el nombre del servicio.
  - igual Spring ya parte de `spring.application.name`.

En `stats-service`:

- habilitar observacion de Feign si hace falta mediante dependencia/config simple.

En `live-engine-service` y `stats-service`:

- habilitar observacion de Spring AMQP si la configuracion queda directa y legible.

## Archivos de configuracion a agregar

### OpenTelemetry Collector

Crear:

- `monitoring/otel-collector/config.yml`

Contenido esperado:

- receptor OTLP por HTTP y/o gRPC;
- exporter hacia Tempo;
- pipeline de traces.

Mantenerlo intencionalmente chico y comentado.

### Tempo

Crear:

- `monitoring/tempo/tempo.yml`

Configuracion minima:

- receiver OTLP;
- storage local;
- puertos basicos para uso en Docker Compose.

### Grafana

Agregar datasource de Tempo:

- `monitoring/grafana/provisioning/datasources/tempo.yml`

Agregar datasource de Loki:

- `monitoring/grafana/provisioning/datasources/loki.yml`

Opcional:

- algun dashboard o link de exploracion si no complica.

### Loki y agente de logs

Crear:

- `monitoring/loki/loki-config.yml`
- `monitoring/promtail/promtail-config.yml`

Configuracion minima:

- Loki como backend local de logs;
- Promtail leyendo logs de Docker;
- labels simples por servicio o contenedor para que sea facil filtrar en Grafana.

## Cambios en Docker Compose

Agregar servicios:

1. `otel-collector`
   - imagen oficial del collector;
   - monta `monitoring/otel-collector/config.yml`.

2. `tempo`
   - imagen oficial de Grafana Tempo;
   - monta `monitoring/tempo/tempo.yml`.

3. `loki`
   - imagen oficial de Grafana Loki;
   - monta `monitoring/loki/loki-config.yml`.

4. `promtail`
   - imagen oficial de Grafana Promtail;
   - monta `monitoring/promtail/promtail-config.yml`;
   - lee logs de contenedores y los envia a Loki.

5. actualizar dependencias de los servicios instrumentados
   - no para arrancar, pero si para que el flujo de trazas este disponible cuando el collector este arriba.

## Tareas paso a paso

1. Crear documentacion inicial
   - `docs/observability/opentelemetry-analysis.md`
   - `docs/observability/opentelemetry-implementation-plan.md`

2. Agregar stack de trazas local
   - Collector
   - Tempo
   - Loki
   - Promtail
   - datasource de Tempo en Grafana
   - datasource de Loki en Grafana

3. Instrumentar aplicaciones
   - agregar dependencia OpenTelemetry;
   - agregar configuracion OTLP;
   - mantener nombres de servicio claros.

4. Verificar propagacion HTTP
   - `api-gateway` -> `stats-service` -> `core-registry-service`

5. Evaluar observacion AMQP
   - si se habilita con pocos cambios, dejarla activa;
   - si empieza a requerir mucha configuracion, dejarla documentada como mejora futura.

6. Verificar metricas
   - conservar `/actuator/prometheus`;
   - confirmar que Prometheus siga scrapeando.

7. Verificar logs
   - confirmar que Promtail este enviando logs a Loki;
   - confirmar que Grafana pueda consultarlos por contenedor o servicio.

8. Preparar demo reproducible
   - documentar endpoints;
   - documentar pasos en Grafana/Tempo.

9. Escribir guia educativa en español
   - conceptos base;
   - uso de cada componente;
   - pasos de prueba.

## Demo principal elegida

La demo principal va a ser esta:

1. llamar al gateway:
   - `GET /stats/api/stats/top-scorers`
   - o `GET /stats/api/stats/standings`

2. el gateway enruta a `stats-service`

3. `stats-service` consulta a `core-registry-service` por Feign para enriquecer la respuesta

4. en Tempo/Grafana se ve la traza con spans de:
   - gateway;
   - controlador/servicio de stats;
   - llamada Feign;
   - controlador/servicio de core-registry.

Ademas, en Loki/Grafana se deberian poder ver los logs emitidos por esos contenedores en el mismo rango temporal.

## Flujo complementario opcional

Si AMQP queda observada con bajo costo:

1. crear o iniciar un partido en `live-engine-service`;
2. registrar un gol o finalizarlo;
3. `live-engine-service` publica evento;
4. `stats-service` consume evento;
5. luego consultar `stats-service` para ver el efecto.

Esto suma mucho valor pedagogico, pero no debe bloquear el minimo viable.

## Como correr la solucion

### Levantar todo

```bash
docker compose up -d --build
```

### Verificar infraestructura

- Eureka: `http://localhost:8761`
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000`

### Verificar metricas

Probar por ejemplo:

- `http://localhost:8080/stats/actuator/prometheus`

### Verificar trazas

1. generar requests de demo;
2. entrar a Grafana;
3. usar el datasource Tempo en Explore;
4. buscar por servicio o por rango de tiempo reciente.

### Verificar logs

1. entrar a Grafana;
2. usar el datasource Loki en `Explore`;
3. filtrar por contenedor o servicio;
4. ubicar los logs generados por la request de demo.

## Criterios de aceptacion

- Las apps arrancan por Docker Compose.
- Prometheus sigue scrapeando metricas.
- Tempo recibe trazas via Collector.
- Grafana tiene datasource de Prometheus y Tempo.
- Loki recibe logs via Promtail.
- Grafana tiene datasource de Loki.
- Un request a traves de multiples servicios genera una traza visible.
- Los logs de los contenedores se pueden consultar en Grafana.
- La documentacion explica el enfoque y como probarlo.

## Decisiones de simplicidad

- OTLP HTTP en vez de configuraciones mas avanzadas.
- sampleo al 100% en local para facilitar el aprendizaje.
- Loki con configuracion minima y sin parsing sofisticado.
- sin custom spans manuales salvo que hagan falta para explicar mejor un punto.
- sin refactors grandes del dominio ni de la arquitectura existente.

## Posibles mejoras futuras

- correlacion mas fuerte de logs con trace id;
- Loki + Promtail;
- spans manuales en secciones de negocio puntuales;
- demo asincrona mas guiada para RabbitMQ;
- dashboards mas especificos por flujo de negocio.
