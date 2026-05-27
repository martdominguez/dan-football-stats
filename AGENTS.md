# Repository Guidelines

## Project Structure & Module Organization
This repository is a Spring Boot microservices workspace. The parent `pom.xml` aggregates `core-registry-service`, `live-engine-service`, and `stats-service`. Each service keeps Java sources in `src/main/java/com/miniscore/...` and configuration in `src/main/resources/`. Flyway migrations live under `src/main/resources/db/migration/` in the PostgreSQL-backed services. `api-gateway/` and `discovery-server/` are standalone Spring Boot apps with their own `pom.xml`. Local API examples are stored in `bruno/`, and observability assets live in `monitoring/`.

## Build, Test, and Development Commands
Use Maven and Docker Compose from the repository root:

- `mvn test`: runs the reactor build for the parent modules.
- `mvn -pl stats-service test`: runs one module only; swap the module name as needed.
- `docker compose up -d --build`: starts the full local stack, including infrastructure.
- `docker compose down -v`: stops containers and removes named volumes.
- `docker compose scale stats-service=3`: scales a service for local resilience checks.

For service-specific work, the module wrappers are available, for example `./stats-service/mvnw test`.

## Coding Style & Naming Conventions
Use Java 21, 4-space indentation, and standard Spring naming. Classes use `PascalCase`; request/response DTOs end in `Request` or `Response`; controllers, services, repositories, and config classes follow the existing suffixes in each module. Keep package names under `com.miniscore.<domain>`. No formatter or linter plugin is configured in Maven, so match the surrounding style and keep imports organized.

## Testing Guidelines
`spring-boot-starter-test` is present across services, but no committed `src/test` suite exists yet. Add tests with every behavior change, placing them under `src/test/java` with names like `TeamServiceTest` or `StatsControllerTest`. Prefer focused service and controller tests over large end-to-end fixtures, and run `mvn test` before opening a PR.

## Commit & Pull Request Guidelines
Recent history favors short, imperative messages such as `actuator`, `update doc`, and `fix issues and add prometheus`. Keep commits small and descriptive, in English or Spanish, and start with the change itself. PRs should include a clear summary, affected modules, manual verification steps, and sample requests or screenshots when API behavior, dashboards, or gateway routing changes.
