# Logging en Java y Spring Boot

Los logs son registros de eventos que ocurren mientras una aplicación se está ejecutando. Sirven para entender qué está haciendo el sistema, detectar errores, reconstruir situaciones problemáticas y observar el comportamiento de la aplicación en distintos entornos.

En una aplicación real no alcanza con hacer `System.out.println(...)`. Necesitamos poder decidir:

- qué mensajes registrar
- con qué nivel de detalle
- en qué formato
- a qué destino enviarlos
- cuánto tiempo conservarlos
- cómo evitar que ocupen demasiado disco

Si registramos demasiado, consumimos CPU, disco y tiempo de I/O. Si registramos muy poco, después cuesta muchísimo diagnosticar problemas. El objetivo del logging no es "imprimir cosas", sino dejar trazas útiles para operar y mantener el sistema.

## Idea general: API, implementación y destino

En el ecosistema Java conviene separar tres conceptos:

| Concepto | Qué resuelve | Ejemplos |
| --- | --- | --- |
| API de logging | La interfaz que usa nuestro código | SLF4J |
| Implementación | La librería que realmente escribe el log | Logback, Log4j2, JUL |
| Destino | A dónde va el log | Consola, archivo, socket, sistema centralizado |

### SLF4J

SLF4J (*Simple Logging Facade for Java*) no es un motor de logging por sí mismo. Es una fachada: define una API común para que nuestro código no quede atado directamente a una implementación concreta.

En la práctica:

- la aplicación suele programarse contra `org.slf4j.Logger`
- por detrás puede estar Logback, Log4j2 u otra implementación
- esto desacopla el código de la tecnología concreta de logging

### Implementaciones comunes

#### JUL

`java.util.logging` o JUL viene con Java. Históricamente se usó bastante, pero en aplicaciones Spring Boot modernas no suele ser la opción preferida para código de aplicación.

#### Logback

Logback es la implementación por defecto en Spring Boot cuando usamos los starters estándar. Está muy integrada con SLF4J y es la opción más común en proyectos Spring Boot.

#### Log4j2

Log4j2 también es una implementación moderna y potente. Suele elegirse cuando se necesitan algunas capacidades específicas de performance, asincronía o integraciones particulares.

## Conceptos básicos de un framework de logging

Más allá de la implementación elegida, aparecen siempre ideas parecidas:

- `Logger`: objeto que usa la aplicación para emitir mensajes.
- `Appender`: destino del log. Por ejemplo consola o archivo.
- `Layout` o `Encoder`: formato del mensaje.
- `Level`: nivel de severidad o detalle.
- `Rolling policy`: política de rotación de archivos.

En JUL aparecen conceptos como `Handler` y `Formatter`. En Logback o Log4j2 hablamos más seguido de `Appender` y `Encoder/Layout`. La idea es parecida, pero no conviene mezclar la terminología como si fuera exactamente lo mismo.

## Niveles de logging

Los niveles más usados en Spring Boot y SLF4J/Logback son:

| Nivel | Cuándo usarlo | Observación |
| --- | --- | --- |
| `TRACE` | Detalle extremadamente fino | Puede generar muchísimo volumen |
| `DEBUG` | Información útil para desarrollo o diagnóstico | Normalmente no queda habilitado en producción |
| `INFO` | Eventos normales del sistema | Inicio, fin, acciones relevantes |
| `WARN` | Algo inesperado pero recuperable | El sistema sigue funcionando |
| `ERROR` | Fallo que afecta una operación | Requiere atención |

### Aclaración importante

Hay material viejo que enumera `FATAL` como nivel habitual. Eso puede verse en Log4j clásico, pero:

- en SLF4J no existe `FATAL`
- en Logback tampoco existe como nivel separado
- en Spring Boot actual normalmente se trabaja con `TRACE`, `DEBUG`, `INFO`, `WARN` y `ERROR`

O sea: si aparece `FATAL` en apuntes antiguos, hay que entenderlo como una noción histórica. En aplicaciones modernas con Spring Boot 3.x lo normal es usar `ERROR`.

## Cómo elegir el nivel correcto

### `TRACE`

Se usa para trazas extremadamente detalladas. Ejemplos:

- estado de variables en pasos internos complejos
- iteraciones de algoritmos
- flujo fino dentro de infraestructura o librerías

No conviene dejarlo habilitado en producción salvo casos puntuales, porque genera mucho volumen.

### `DEBUG`

Se usa para información útil al depurar:

- valores intermedios
- decisiones de negocio tomadas por el código
- parámetros relevantes
- resultados parciales

Es muy útil en desarrollo y testing. En producción se habilita de forma puntual cuando hace falta investigar algo.

### `INFO`

Sirve para eventos normales e importantes del sistema:

- arranque de la aplicación
- levantada de componentes importantes
- ejecución de procesos de negocio relevantes
- cierre correcto

Es el nivel que suele quedar visible por defecto en muchos entornos.

### `WARN`

Se usa cuando pasó algo raro, pero la aplicación pudo seguir:

- uso de un valor por defecto porque faltó configuración
- reintento de una operación
- timeout recuperado
- dato inesperado pero tolerable

### `ERROR`

Se usa cuando una operación falló y eso merece atención:

- excepción no recuperable para ese caso de uso
- error al acceder a un recurso externo
- inconsistencia que impide completar una operación

## Jerarquía de loggers

Los loggers suelen organizarse jerárquicamente según el nombre del paquete o de la clase. Por ejemplo:

- `ar.edu.universidad.app`
- `ar.edu.universidad.app.service`
- `ar.edu.universidad.app.service.UsuarioService`

Eso permite configurar distintos niveles para distintas partes de la aplicación.

Ejemplo:

- `logging.level.root=INFO`
- `logging.level.ar.edu.universidad.app.service=DEBUG`

En ese caso:

- toda la aplicación loguea al menos en `INFO`
- la capa `service` además muestra mensajes `DEBUG`

### Sobre "varias clases escribiendo al mismo archivo"

En frameworks modernos como Logback no hace falta crear un archivo por clase ni un `FileHandler` separado por cada una. Lo normal es:

- cada clase obtiene su propio `Logger`
- todos esos loggers terminan escribiendo en los appenders configurados
- el archivo compartido se maneja desde la configuración central

La idea de que cada clase abra el mismo archivo por su cuenta es una mala práctica. La configuración del destino se centraliza.

## Logging en Spring Boot

Spring Boot usa internamente la abstracción `spring-jcl`, pero en el código de aplicación lo normal es trabajar con SLF4J.

Con los starters estándar:

- la implementación por defecto es Logback
- Boot trae una configuración base razonable
- por defecto los logs van a consola

### Qué conviene usar en el código

```java
package ar.edu.universidad.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    public void crearUsuario(String email) {
        logger.info("Creando usuario con email={}", email);
    }
}
```

### Alternativa común con Lombok

Si el proyecto usa Lombok, también es habitual:

```java
package ar.edu.universidad.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PedidoService {

    public void confirmarPedido(Long idPedido) {
        log.info("Confirmando pedido {}", idPedido);
    }
}
```

## Ejemplo práctico de uso correcto

```java
package ar.edu.universidad.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TransferenciaService {

    private static final Logger logger = LoggerFactory.getLogger(TransferenciaService.class);

    public void transferir(Long cuentaOrigen, Long cuentaDestino, double monto) {
        logger.info(
                "Iniciando transferencia. origen={}, destino={}, monto={}",
                cuentaOrigen,
                cuentaDestino,
                monto
        );

        if (monto <= 0) {
            logger.warn("Se intento transferir un monto no valido: {}", monto);
            throw new IllegalArgumentException("El monto debe ser mayor a cero");
        }

        try {
            logger.debug("Validando saldo y reglas de negocio");

            // Lógica ficticia del ejemplo
            boolean transferenciaExitosa = true;

            if (transferenciaExitosa) {
                logger.info(
                        "Transferencia realizada correctamente. origen={}, destino={}, monto={}",
                        cuentaOrigen,
                        cuentaDestino,
                        monto
                );
            }
        } catch (RuntimeException e) {
            logger.error(
                    "Fallo la transferencia. origen={}, destino={}, monto={}",
                    cuentaOrigen,
                    cuentaDestino,
                    monto,
                    e
            );
            throw e;
        }
    }
}
```

### Salida posible

```text
2026-05-20 19:30:10.421  INFO 12345 --- [nio-8080-exec-1] a.e.u.demo.service.TransferenciaService : Iniciando transferencia. origen=10, destino=25, monto=1000.0
2026-05-20 19:30:10.425 DEBUG 12345 --- [nio-8080-exec-1] a.e.u.demo.service.TransferenciaService : Validando saldo y reglas de negocio
2026-05-20 19:30:10.432  INFO 12345 --- [nio-8080-exec-1] a.e.u.demo.service.TransferenciaService : Transferencia realizada correctamente. origen=10, destino=25, monto=1000.0
```

## Buen ejemplo vs mal ejemplo

### Mal

```java
logger.info("Entrando al metodo");
logger.info("valor=" + valor);
logger.error("Paso algo");
```

Problemas:

- el mensaje no aporta contexto
- se concatena string aunque el nivel tal vez esté deshabilitado
- `Paso algo` no dice qué pasó
- no se registra la excepción

### Bien

```java
logger.debug("Procesando archivo {} para el usuario {}", nombreArchivo, idUsuario);
logger.error("Error al procesar el archivo {} para el usuario {}", nombreArchivo, idUsuario, ex);
```

Ventajas:

- el mensaje tiene contexto
- se usa parametrización con `{}` en vez de concatenación
- se registra la excepción completa

## Configuración básica en Spring Boot

### `application.properties`

```properties
server.port=9010

# Nivel global por defecto
logging.level.root=INFO

# Nivel para nuestro paquete
logging.level.ar.edu.universidad.demo=DEBUG

# Archivo de salida
logging.file.name=logs/demo-app.log

# Patrón de consola
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%thread] %logger{36} - %msg%n

# Patrón de archivo
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%thread] %logger{36} - %msg%n
```

### `application.yml`

```yaml
server:
  port: 9010

logging:
  level:
    root: INFO
    ar.edu.universidad.demo: DEBUG
  file:
    name: logs/demo-app.log
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%thread] %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%thread] %logger{36} - %msg%n"
```

## Qué viene por defecto en Spring Boot

Spring Boot trae una configuración inicial bastante usable:

- logs a consola
- niveles razonables para Spring y la app
- patrón de salida legible
- posibilidad de cambiar niveles por paquete desde propiedades

### Qué suele quedar corto

Para proyectos medianos o productivos, normalmente hace falta agregar:

- archivos con rotación
- formato JSON
- correlation IDs
- separación por ambiente
- appenders diferentes según destino
- reglas más finas por paquete o por clase

## Configuración avanzada con `logback-spring.xml`

Cuando la configuración por propiedades no alcanza, se puede agregar `src/main/resources/logback-spring.xml`.

### Ejemplo completo con consola y archivo rotativo

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <property name="LOG_PATH" value="logs" />
    <property name="APP_NAME" value="demo-app" />

    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%thread] %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="ROLLING_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_PATH}/${APP_NAME}.log</file>

        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%thread] %logger{36} - %msg%n</pattern>
        </encoder>

        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_PATH}/archived/${APP_NAME}.%d{yyyy-MM-dd}.%i.log.gz</fileNamePattern>
            <maxFileSize>10MB</maxFileSize>
            <maxHistory>30</maxHistory>
            <totalSizeCap>1GB</totalSizeCap>
        </rollingPolicy>
    </appender>

    <root level="INFO">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="ROLLING_FILE" />
    </root>

    <logger name="ar.edu.universidad.demo" level="DEBUG" />

</configuration>
```

### Aclaración sobre contenido desactualizado

En ejemplos viejos puede aparecer:

- `SizeAndTimeBasedFNATP`
- configuraciones heredadas de Logback antiguas

Eso hoy está desactualizado para la mayoría de los casos. La alternativa moderna y más clara es usar `SizeAndTimeBasedRollingPolicy`.

## Log4j2 en Spring Boot

Aunque el default sea Logback, Spring Boot permite usar Log4j2.

La idea general es:

1. excluir `spring-boot-starter-logging`
2. agregar `spring-boot-starter-log4j2`

Ejemplo con Maven:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-logging</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-log4j2</artifactId>
</dependency>
```

No hace falta hacerlo por defecto. En la mayoría de los proyectos Spring Boot, Logback alcanza y sobra.

## Logging estructurado

En sistemas modernos muchas veces no alcanza con logs "lindos para leer". También interesa que sean fáciles de procesar automáticamente.

Eso lleva al logging estructurado: en vez de una línea informal, se emiten campos bien definidos.

Ejemplo conceptual:

```text
timestamp=2026-05-20T19:30:10 level=INFO service=pagos traceId=abc123 usuario=42 mensaje="Transferencia realizada"
```

Ventajas:

- búsqueda más simple en herramientas centralizadas
- filtros por campos
- dashboards y alertas más precisos
- mejor trazabilidad en microservicios

### JSON logging

Una forma habitual de logging estructurado es emitir JSON.

Ejemplo de salida:

```json
{
  "timestamp": "2026-05-20T19:30:10.432-03:00",
  "level": "INFO",
  "logger": "ar.edu.universidad.demo.service.TransferenciaService",
  "thread": "http-nio-8080-exec-1",
  "message": "Transferencia realizada correctamente",
  "cuentaOrigen": 10,
  "cuentaDestino": 25,
  "monto": 1000.0,
  "traceId": "abc123"
}
```

En Spring Boot se puede implementar de distintas maneras:

- usando un encoder JSON de Logback
- usando Logstash Logback Encoder
- usando configuraciones específicas de la plataforma de observabilidad

### Ejemplo de Logback con JSON

Este ejemplo requiere una dependencia adicional, por ejemplo `net.logstash.logback:logstash-logback-encoder`.

```xml
<configuration>

    <appender name="JSON_CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LogstashEncoder" />
    </appender>

    <root level="INFO">
        <appender-ref ref="JSON_CONSOLE" />
    </root>

</configuration>
```

## MDC y correlation IDs

### Qué problema resuelven

En aplicaciones con múltiples requests concurrentes, o peor todavía en microservicios, enseguida aparece una pregunta:

> ¿Cómo sé qué logs pertenecen a la misma operación?

Para eso se usan identificadores de correlación, muchas veces llamados `correlationId` o `traceId`.

El MDC (*Mapped Diagnostic Context*) permite adjuntar datos contextuales al hilo de ejecución actual para que cada línea de log los incluya.

### Ejemplo simple con filtro HTTP

```java
package ar.edu.universidad.demo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String HEADER_NAME = "X-Correlation-Id";
    public static final String MDC_KEY = "correlationId";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String correlationId = request.getHeader(HEADER_NAME);

        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        MDC.put(MDC_KEY, correlationId);
        response.setHeader(HEADER_NAME, correlationId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_KEY);
        }
    }
}
```

### Patrón para mostrar el correlation ID

```properties
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%thread] [%X{correlationId}] %logger{36} - %msg%n
```

### Salida posible

```text
2026-05-20 19:35:10.101 INFO  [http-nio-8080-exec-2] [9d0b7f4d-0d4d-42a8-beb4-2d7c4a9a4e25] a.e.u.demo.controller.UsuarioController - GET /usuarios/10
```

## Logging en microservicios

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
