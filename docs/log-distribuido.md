# Logging en microservicios

En microservicios el logging cambia un poco de escala:

- ya no alcanza con mirar un único archivo local
- importa muchísimo la correlación entre servicios
- conviene emitir logs estructurados
- los logs suelen centralizarse en herramientas externas

En ese contexto suele recomendarse:

- incluir `service.name`
- incluir `traceId` o `correlationId`
- no depender solamente de archivos locales
- mantener formatos consistentes entre servicios

## Logging, métricas y tracing: no son lo mismo

Cuando se empieza a trabajar con observabilidad distribuida conviene separar tres tipos de señales:

| Señal | Qué responde mejor | Ejemplo |
| --- | --- | --- |
| Logs | Qué pasó en detalle | "Falló el llamado al catálogo con timeout" |
| Métricas | Cuánto, cada cuánto, con qué tendencia | latencia P95, tasa de errores, uso de heap |
| Trazas | Cómo viajó una operación entre servicios | gateway -> stats -> core-registry |

Las tres se complementan:

- una métrica puede mostrar que aumentó la latencia
- una trace puede mostrar en qué servicio se fue el tiempo
- un log puede dar el detalle del error o la decisión tomada

## Qué es el tracing distribuido

El *distributed tracing* o trazabilidad distribuida permite reconstruir el recorrido de una operación a través de varios procesos o servicios.

Ejemplo en este proyecto:

1. entra un request a `api-gateway`
2. el gateway lo enruta a `stats-service`
3. `stats-service` consulta a `core-registry-service`
4. cada paso deja spans relacionados dentro de una misma trace

Sin tracing distribuido veríamos logs y métricas sueltas. Con tracing distribuido podemos entender:

- qué servicio recibió primero la operación
- qué llamados internos hizo
- cuánto tardó cada tramo
- dónde apareció un error

## Trace, span y propagación de contexto

### Trace

Una `trace` representa la operación completa de punta a punta.

Ejemplo:

- un `GET /stats/api/stats/demo/trace?teamId=1&playerId=1`

Toda esa operación debería quedar identificada por un `traceId`.

### Span

Un `span` es una unidad de trabajo dentro de una trace.

Ejemplos de spans posibles:

- request entrante al gateway
- request entrante a `stats-service`
- llamada saliente de Feign desde `stats-service`
- request entrante a `core-registry-service`

### Propagación de contexto

La propagación de contexto es el mecanismo que permite que el segundo servicio entienda que la operación ya venía trazada desde el primero.

En la práctica eso suele viajar en headers HTTP.

Si no hay propagación:

- cada servicio crea su propia trace aislada
- no se puede reconstruir el recorrido completo

Si sí hay propagación:

- todos los spans quedan conectados
- el `traceId` es el mismo a lo largo del flujo

## OpenTelemetry

OpenTelemetry es el estándar moderno más usado para instrumentar observabilidad en sistemas distribuidos.

No es una única herramienta. Es un ecosistema que define:

- APIs
- SDKs
- formato de datos
- convenciones
- protocolos de exportación

### Qué problema resuelve

Evita que la aplicación quede acoplada a una sola plataforma de observabilidad.

La idea es:

- la app emite telemetría con un estándar
- después esa telemetría puede ir a distintas herramientas

### Qué telemetría puede manejar

OpenTelemetry puede trabajar con:

- traces
- métricas
- logs

En este proyecto lo usamos principalmente para tracing distribuido.

## OTLP

OTLP significa *OpenTelemetry Protocol*.

Es el protocolo con el que una aplicación o un agente exporta telemetría hacia otro componente, por ejemplo un collector.

En este proyecto usamos OTLP para enviar trazas desde las apps Spring Boot al OpenTelemetry Collector.

## OpenTelemetry Collector

El OpenTelemetry Collector es un proceso intermedio entre las aplicaciones y los backends finales.

### Objetivo

Centralizar la recepción y el reenvío de telemetría.

### Ventajas

- simplifica la configuración de las apps
- evita que cada servicio conozca todos los backends finales
- permite cambiar destinos sin tocar tanto el código
- sirve para explicar una arquitectura de observabilidad más realista

### En este proyecto

Las apps Spring Boot envían trazas por OTLP al Collector.

Después el Collector las reenvía a Tempo.

## Loki

Loki es un sistema para almacenar y consultar logs.

Fue creado por Grafana Labs y está pensado para centralización de logs con una idea parecida a Prometheus:

- indexar principalmente etiquetas
- dejar el contenido del log como texto
- hacer consultas eficientes por labels y tiempo

### Qué problema resuelve

En microservicios ya no alcanza con mirar la consola de un solo proceso. Loki permite:

- juntar logs de varios contenedores
- consultarlos desde un mismo lugar
- filtrarlos por servicio o etiqueta
- relacionarlos temporalmente con métricas y traces

## Promtail

Promtail es un agente que lee logs y los empuja a Loki.

### Qué hace

- descubre fuentes de logs
- les agrega labels
- envía cada línea a Loki

### En este proyecto

Promtail se conecta al socket de Docker y descubre los contenedores del stack.

De ahí toma:

- nombre del contenedor
- stream (`stdout` o `stderr`)

y lo manda a Loki con labels simples como:

- `container`
- `service`
- `stream`

### Aclaración importante

Promtail está deprecado dentro del ecosistema de Grafana, pero para un proyecto educativo local sigue siendo una opción simple de entender. Más adelante podría reemplazarse por Grafana Alloy.

## Tempo

Tempo es el backend donde se almacenan las traces.

En este proyecto su objetivo es muy concreto:

- recibir spans reenviados por el Collector
- permitir consultarlos desde Grafana

Tempo no centraliza logs. Para eso usamos Loki.

## Prometheus

Prometheus es el backend de métricas.

En este proyecto ya estaba integrado para scrapear:

- `discovery-server`
- `core-registry-service`
- `live-engine-service`
- `stats-service`
- `api-gateway`

Prometheus tampoco reemplaza a Loki ni a Tempo:

- Prometheus guarda métricas
- Loki guarda logs
- Tempo guarda trazas

## Grafana

Grafana es la interfaz unificada para consultar las distintas señales.

En esta arquitectura permite:

- ver métricas desde Prometheus
- ver traces desde Tempo
- ver logs desde Loki

Esa unificación es muy útil para explicar observabilidad porque el estudiante puede abrir una sola interfaz y navegar entre distintos tipos de datos.

## Arquitectura general de observabilidad en este proyecto

```text
Spring Boot services
  -> OTLP HTTP
  -> OpenTelemetry Collector
  -> Tempo
  -> Grafana

Spring Boot services
  -> /actuator/prometheus
  -> Prometheus
  -> Grafana

Docker container logs
  -> Promtail
  -> Loki
  -> Grafana
```

## Cómo se configura en Docker

En este proyecto la forma más simple de mostrar la stack es con Docker Compose.

### Objetivo

Levantar toda la observabilidad local sin instalaciones manuales por fuera del repo.

### Componentes agregados

- `otel-collector`
- `tempo`
- `loki`
- `promtail`
- `prometheus`
- `grafana`

### Collector

Archivo:

- `monitoring/otel-collector/config.yml`

Idea general:

- recibe OTLP por HTTP y gRPC
- procesa en batch
- exporta traces a Tempo

Ejemplo simplificado:

```yaml
receivers:
  otlp:
    protocols:
      grpc:
      http:

processors:
  batch:

exporters:
  otlp/tempo:
    endpoint: tempo:4317
    tls:
      insecure: true

service:
  pipelines:
    traces:
      receivers: [otlp]
      processors: [batch]
      exporters: [otlp/tempo]
```

### Tempo

Archivo:

- `monitoring/tempo/tempo.yml`

Idea general:

- expone receiver OTLP
- guarda traces en storage local
- queda listo para ser consultado por Grafana

### Loki

Archivo:

- `monitoring/loki/loki-config.yml`

Idea general:

- expone API HTTP en `3100`
- guarda datos localmente
- funciona como backend centralizado de logs

### Promtail

Archivo:

- `monitoring/promtail/promtail-config.yml`

Idea general:

- descubre contenedores Docker usando el socket local
- obtiene metadatos del contenedor
- envía logs a Loki

Ejemplo simplificado:

```yaml
clients:
  - url: http://loki:3100/loki/api/v1/push

scrape_configs:
  - job_name: docker-containers
    docker_sd_configs:
      - host: unix:///var/run/docker.sock
```

### Grafana

Grafana queda con datasources provisionados desde archivos:

- `monitoring/grafana/provisioning/datasources/prometheus.yml`
- `monitoring/grafana/provisioning/datasources/tempo.yml`
- `monitoring/grafana/provisioning/datasources/loki.yml`

Eso evita tener que crear datasources a mano cada vez que se levanta el entorno.

## Cómo se integra con Spring Boot

### 1. Dependencia de OpenTelemetry

En los servicios Spring Boot se agregó:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-opentelemetry</artifactId>
</dependency>
```

Con eso Spring Boot puede instrumentar trazas de forma nativa usando Micrometer Tracing y OpenTelemetry.

### 2. Exportación OTLP

En `application.yml` se configuró algo como esto:

```yaml
management:
  tracing:
    sampling:
      probability: 1.0
  opentelemetry:
    tracing:
      export:
        otlp:
          endpoint: ${OTEL_EXPORTER_OTLP_TRACES_ENDPOINT:http://localhost:4318/v1/traces}
```

#### Qué significa

- `sampling.probability: 1.0`
  - en local sampleamos el 100% de las requests para no perdernos ninguna trace de la demo

- `endpoint`
  - la app manda las trazas al Collector usando una variable de entorno

### 3. Nombre del servicio

También se configuró:

```yaml
management:
  opentelemetry:
    resource-attributes:
      service.name: ${spring.application.name}
```

Esto ayuda a que cada span quede asociado a un nombre de servicio claro.

### 4. Correlación en logs

Además se configuró el patrón de logging para incluir:

- nombre del servicio
- `traceId`
- `spanId`

Ejemplo:

```yaml
logging:
  include-application-name: false
  pattern:
    correlation: "[${spring.application.name:},%X{traceId:-},%X{spanId:-}] "
```

El objetivo es que cuando un estudiante mire Loki pueda cruzar una línea de log con una trace de Tempo.

### 5. Métricas con Actuator y Prometheus

Los servicios ya tenían:

- `spring-boot-starter-actuator`
- `micrometer-registry-prometheus`

Eso permite exponer `/actuator/prometheus` y mantener la parte de métricas sin cambios grandes.

## Cómo se conecta todo en el `docker-compose.yml`

La idea general es:

- cada servicio de Spring Boot tiene la variable `OTEL_EXPORTER_OTLP_TRACES_ENDPOINT`
- el valor apunta a `http://otel-collector:4318/v1/traces`
- Prometheus scrapea métricas por HTTP
- Promtail lee logs de Docker
- Grafana consulta Prometheus, Tempo y Loki

Ejemplo conceptual para un servicio:

```yaml
environment:
  OTEL_EXPORTER_OTLP_TRACES_ENDPOINT: http://otel-collector:4318/v1/traces
```

## Integración concreta en este repo

### Servicios con tracing

Se integró tracing al menos en:

- `core-registry-service`
- `live-engine-service`
- `stats-service`
- `api-gateway`

### Flujo principal de demo

El flujo más fácil de mostrar es:

1. request a `api-gateway`
2. gateway enruta a `stats-service`
3. `stats-service` llama a `core-registry-service`
4. la trace aparece en Tempo
5. los logs de esos contenedores aparecen en Loki

### Endpoint de demo

Se agregó un endpoint pensado para esto:

- `GET /stats/api/stats/demo/trace?teamId=1&playerId=1`

Ese endpoint devuelve además el `traceId`, para facilitar la búsqueda en Grafana.

## Cómo buscar la misma operación en logs y traces

### En Tempo

1. abrir Grafana
2. ir a `Explore`
3. elegir el datasource `Tempo`
4. pegar el `traceId`

### En Loki

Después se puede:

1. cambiar al datasource `Loki`
2. buscar por servicio o por contenido del `traceId`

Ejemplos de consultas:

```text
{service=~".*stats-service.*"}
```

```text
{container=~".*api-gateway.*"}
```

Si una línea de log contiene el mismo `traceId`, entonces se puede relacionar visualmente con la trace distribuida.

## Por qué esta arquitectura sirve para enseñar

Porque deja ver de forma separada y a la vez conectada:

- logs
- métricas
- traces

Además:

- no depende de Kubernetes
- se levanta con Docker Compose
- usa herramientas comunes del ecosistema
- permite entender roles distintos sin mezclar conceptos

## Qué no hace falta sobrecomplicar en un proyecto educativo

Para una demo local no hace falta:

- muestreo sofisticado
- pipelines complejos de parsing
- múltiples collectors
- storage distribuido
- alerting avanzado
- formatos extremadamente optimizados

Primero conviene entender bien:

- qué es un log
- qué es una métrica
- qué es una trace
- cómo se relacionan
- qué hace cada herramienta del stack

## Performance y costo del logging

El logging tiene costo. Algunas recomendaciones:

- no habilitar `TRACE` o `DEBUG` globalmente en producción sin motivo
- usar placeholders `{}` en vez de concatenar strings
- evitar loguear objetos enormes en cada request
- no serializar JSON a mano sólo para loguearlo si no hace falta
- prestar atención a appenders síncronos muy pesados

### Ejemplo

Mal:

```java
logger.debug("Resultado del proceso: " + objetoPesado.toString());
```

Bien:

```java
logger.debug("Resultado del proceso: {}", objetoPesado);
```

La segunda opción evita trabajo innecesario cuando el nivel `DEBUG` no está habilitado.

## Seguridad y datos sensibles

Loguear demasiado también puede ser un problema de seguridad.

Nunca conviene registrar en texto plano:

- contraseñas
- tokens
- claves privadas
- números completos de tarjeta
- datos personales sensibles si no son realmente necesarios

### Mal

```java
logger.info("Login usuario={} password={}", usuario, password);
```

### Mejor

```java
logger.info("Intento de login para usuario={}", usuario);
```

Si se necesita auditar, conviene registrar el evento y no el secreto.

## Prácticas modernas de configuración

### Cambiar niveles por entorno

Es común tener:

- `INFO` en producción
- `DEBUG` en desarrollo
- override puntual por paquete cuando aparece un problema

Ejemplo:

```properties
spring.profiles.active=dev
```

`application-dev.properties`

```properties
logging.level.root=DEBUG
logging.level.org.springframework.web=INFO
```

`application-prod.properties`

```properties
logging.level.root=INFO
logging.level.org.springframework.web=WARN
```

### Configurar grupos de loggers

Spring Boot también permite agrupar loggers:

```properties
logging.group.web=org.springframework.web,org.springframework.http
logging.level.web=DEBUG
```

Esto es práctico cuando queremos ajustar varios paquetes relacionados juntos.

## Errores conceptuales frecuentes en apuntes viejos

### 1. "Spring Boot usa Commons Logging y listo"

Eso queda corto. Más preciso sería:

- Spring usa internamente `spring-jcl`
- el código de aplicación normalmente usa SLF4J
- con starters estándar, la implementación por defecto es Logback

### 2. "Cada clase necesita su propio FileHandler"

No en aplicaciones Spring Boot modernas. La escritura a archivos se centraliza en la configuración del framework.

### 3. "FATAL es un nivel estándar en Spring Boot"

No. En la práctica moderna con SLF4J + Logback se trabaja sin `FATAL`.

### 4. "Con `logging.file.path` pongo un nombre de archivo"

Esto era fuente de confusión incluso en versiones viejas. En Spring Boot actual conviene usar:

- `logging.file.name` para indicar archivo completo
- `logging.file.path` para indicar sólo directorio

Ejemplo correcto:

```properties
logging.file.name=logs/dan-ms-productos.log
```

## Ejemplo de aspecto (`@Aspect`) mejorado

Si queremos mantener la idea del ejemplo original con AOP:

```java
package ar.edu.universidad.demo.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Before("execution(* ar.edu.universidad.demo.service..*(..))")
    public void hacerAntes(JoinPoint joinPoint) {
        logger.debug(
                "Se va a ejecutar el metodo {} de la clase {}",
                joinPoint.getSignature().getName(),
                joinPoint.getSignature().getDeclaringTypeName()
        );
    }
}
```

Observación:

- antes el ejemplo emitía `trace`, `debug`, `info`, `warn` y `error` todos juntos para el mismo evento
- eso no tiene mucho sentido en una aplicación real
- lo correcto es elegir el nivel según la importancia del mensaje

## Buenas ideas para escribir mensajes de log

Un buen mensaje de log debería ayudar a responder preguntas como:

- qué operación estaba ocurriendo
- sobre qué entidad o identificador
- con qué resultado
- si falló, por qué falló

Conviene incluir contexto útil:

- `idPedido`
- `idUsuario`
- `email`
- `monto`
- `estado`
- `traceId`

Sin irse al extremo de loguear todo.

## Resumen

El logging es una herramienta central para desarrollar, operar y diagnosticar aplicaciones. En Spring Boot moderno lo más común es programar contra SLF4J y usar Logback como implementación por defecto. La clave no está solamente en "escribir logs", sino en registrar mensajes útiles, con niveles correctos, formato consistente, rotación de archivos, cuidado de performance y protección de datos sensibles.

En proyectos simples puede alcanzar con la configuración básica de `application.properties` o `application.yml`. En proyectos más reales suelen aparecer necesidades como `logback-spring.xml`, JSON logging, correlation IDs y centralización para microservicios.

## Buenas prácticas recomendadas

- Usar SLF4J en el código de aplicación.
- Elegir el nivel de log según la severidad real del evento.
- Usar `INFO` para eventos normales importantes y `DEBUG` para diagnóstico.
- Registrar excepciones pasando el `Throwable`, no sólo el mensaje.
- Usar placeholders `{}` en vez de concatenar strings.
- Centralizar la configuración de appenders y archivos.
- Rotar archivos de log para no llenar el disco.
- Incluir contexto útil como identificadores de negocio o correlation IDs.
- Mantener formatos consistentes entre servicios.
- Evitar registrar secretos o datos sensibles.
- En microservicios, preferir logging estructurado y centralizado.

## Errores comunes

- Usar `System.out.println(...)` como mecanismo principal de logging.
- Loguear el mismo evento en todos los niveles a la vez.
- Dejar `DEBUG` o `TRACE` global en producción sin necesidad.
- Escribir mensajes vagos como `Paso algo`.
- Ocultar la excepción original y loguear sólo `e.getMessage()`.
- Duplicar logs del mismo error en varias capas sin criterio.
- Loguear contraseñas, tokens o datos sensibles.
- Configurar archivos sin rotación.
- Mezclar conceptos de JUL, Log4j2 y Logback como si fueran idénticos.

## Bibliografía y referencias recomendadas

- Documentación oficial de Spring Boot sobre logging.
- Documentación oficial de SLF4J.
- Documentación oficial de Logback.
- Documentación oficial de Log4j2.
- Guías de observabilidad y logging estructurado para aplicaciones distribuidas.
- Material de OpenTelemetry para trazabilidad y correlación en microservicios.
