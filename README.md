# catalog-service

Microservice of the [RSO Auctions](https://github.com/rso-auctions) platform. It owns items, categories,
photos and listing moderation. Other services reach it via REST and GraphQL (frontend), gRPC (auction-service)
and NATS JetStream events.

> Status: walking skeleton. Categories are read from PostgreSQL; other domain features come next.

## Tech stack

- Java 25, Quarkus 3.40 (LTS)
- PostgreSQL, Liquibase migrations, Hibernate ORM with Panache, MapStruct
- Maven (wrapper included)

## Requirements

- JDK 25
- Docker: in dev and test mode Quarkus Dev Services starts PostgreSQL automatically

## Running locally

```shell
./mvnw quarkus:dev
```

The service starts on <http://localhost:8081/catalog>. Useful endpoints:

| Endpoint | Purpose |
| --- | --- |
| `GET /catalog/api/v1/categories` | All categories |
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

Configuration lives in `src/main/resources/application.properties`. Runtime properties can be overridden
without rebuilding through an environment variable (Quarkus naming: `quarkus.http.port` → `QUARKUS_HTTP_PORT`).
Build-time properties are fixed when the image is built; changing them needs a new build.

| Property | Purpose | Default | Changeable at |
| --- | --- | --- | --- |
| `quarkus.http.port` | HTTP port | `8080` (`8081` in dev mode) | runtime |
| `quarkus.datasource.jdbc.url` | PostgreSQL JDBC URL | none (Dev Services in dev/test) | runtime |
| `quarkus.datasource.username` | Database user | none (Dev Services in dev/test) | runtime |
| `quarkus.datasource.password` | Database password, from a Kubernetes Secret in the cluster | none (Dev Services in dev/test) | runtime |
| `quarkus.liquibase.migrate-at-start` | Apply database migrations on startup | `true` | runtime |
| `quarkus.http.root-path` | Path prefix of all endpoints, used by the gateway | `/catalog` | build time |
| `quarkus.datasource.db-kind` | Database type | `postgresql` | build time |

## Project structure

```
src/main/java/si/fri/rso/auctions/catalog
├── api            REST and GraphQL resources (inbound adapters), OpenAPI specs in api/specs
├── lib/dto        API data transfer objects
├── service        Business logic (interfaces + impl)
├── persistence    Entities and repositories
├── mappers        Entity ↔ DTO mapping
├── integrations   Clients for external systems (Azure AI Vision, Blob Storage)
├── events         NATS JetStream producers and consumers
└── exceptions     Domain exceptions and their HTTP mappers
src/main/resources  Configuration; Liquibase migrations in db/changelog
src/main/docker     Dockerfile
src/test            Tests
```

## Branching strategy

- `main`: production; every merge deploys to the AKS cluster.
- `dev`: day-to-day development; merged into `main` at milestones.
