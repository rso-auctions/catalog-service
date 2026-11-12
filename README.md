# catalog-service

Microservice of the [RSO Auctions](https://github.com/rso-auctions) platform. It owns items, categories,
photos and listing moderation. Other services reach it via REST and GraphQL (frontend), gRPC (auction-service)
and NATS JetStream events.

> Status: walking skeleton. Health checks and OpenAPI only; domain features come next.

## Tech stack

- Java 25, Quarkus 3.40 (LTS)
- Maven (wrapper included)

## Requirements

- JDK 25
- Docker (needed from the next steps on, for PostgreSQL via Quarkus Dev Services)

## Running locally

```shell
./mvnw quarkus:dev
```

The service starts on <http://localhost:8081/catalog>. Useful endpoints:

| Endpoint | Purpose |
| --- | --- |
| `/catalog/q/health/live` | Liveness probe |
| `/catalog/q/health/ready` | Readiness probe |
| `/catalog/q/openapi` | OpenAPI document |
| `/catalog/q/swagger-ui` | Swagger UI |
| `/catalog/q/dev-ui` | Quarkus Dev UI (dev mode only) |

## Tests and build

```shell
./mvnw verify
```

Container image (JVM):

```shell
./mvnw package
docker build -f src/main/docker/Dockerfile.jvm -t catalog-service .
```

## Configuration

Configuration lives in `src/main/resources/application.properties`. Every property can be overridden without
rebuilding through an environment variable (Quarkus naming: `quarkus.http.port` → `QUARKUS_HTTP_PORT`).

| Property | Purpose | Default |
| --- | --- | --- |
| `quarkus.http.port` | HTTP port | `8080` (`8081` in dev mode) |
| `quarkus.http.root-path` | Path prefix of all endpoints, used by the gateway | `/catalog` |

## Project structure

```
src/main/java/si/fri/rso/auctions/catalog
├── api            REST and GraphQL resources (inbound adapters)
├── service        Business logic (interfaces + impl)
├── persistence    Entities and repositories
├── mappers        Entity ↔ DTO mapping
├── integrations   Clients for external systems (Azure AI Vision, Blob Storage)
├── events         NATS JetStream producers and consumers
└── exceptions     Domain exceptions and their HTTP mappers
src/main/resources  Configuration and database migrations
src/main/docker     Dockerfile
src/test            Tests
```

## Branching strategy

- `main`: production; every merge deploys to the AKS cluster.
- `dev`: day-to-day development; merged into `main` at milestones.
