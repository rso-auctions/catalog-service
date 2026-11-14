# catalog-service

[![CI main](https://github.com/rso-auctions/catalog-service/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/rso-auctions/catalog-service/actions/workflows/ci.yml?query=branch%3Amain)
[![CI dev](https://github.com/rso-auctions/catalog-service/actions/workflows/ci.yml/badge.svg?branch=dev)](https://github.com/rso-auctions/catalog-service/actions/workflows/ci.yml?query=branch%3Adev)

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
| `GET /catalog/api/v1/categories` | All categories of the tenant |
| `/catalog/q/health/live` | Liveness probe |
| `/catalog/q/health/ready` | Readiness probe |
| `/catalog/q/openapi` | OpenAPI document |
| `/catalog/q/swagger-ui` | Swagger UI |
| `/catalog/q/dev-ui` | Quarkus Dev UI (dev mode only) |

API requests (`/catalog/api/...`) must name the tenant in the `X-Tenant-Id` header:

```shell
curl -H 'X-Tenant-Id: avto' http://localhost:8081/catalog/api/v1/categories
```

## Multitenancy

Each tenant (auction house) has its own PostgreSQL schema, `tenant_<id>`, in the service's database.

- `TenantRequestFilter` reads `X-Tenant-Id` and rejects a missing (`TENANT_MISSING`) or unknown
  (`TENANT_UNKNOWN`) tenant with `400`.
- `SchemaTenantResolver` points Hibernate at the tenant's schema. Without a tenant the query fails; it never falls
  back to a shared schema.
- `TenantScope` sets the tenant for code that does not run in an HTTP request (NATS consumers, gRPC handlers):
  `tenantScope.call("avto", () -> categoryService.getAll())`.
- `TenantSchemaMigrator` creates every tenant's schema on startup and applies the Liquibase changelog to it, under
  a PostgreSQL advisory lock so several pods can start at once. Quarkus' own `migrate-at-start` only covers the
  default schema, so it is disabled. Raw JDBC must name the schema in table names (`tenant_avto.categories`).

**Trust model.** The service trusts `X-Tenant-Id`. In the cluster the API gateway must *overwrite* it from the
request host (Envoy Gateway `requestHeaderModifier.set`, not `add`), and only the gateway may reach the pods
(NetworkPolicy). A check against the `tenant` claim of the Keycloak token comes in a later step.

**Adding a tenant** means adding its id to `rso.tenants.ids` and a rolling restart; the schema is created on
startup. Until every pod has restarted, old pods answer `TENANT_UNKNOWN` for the new tenant.

## Tests and build

```shell
./mvnw verify
```

GitHub Actions ([`ci.yml`](.github/workflows/ci.yml)) runs the same `verify` on every push to `dev` and `main`
and on pull requests into `dev` and `main`; it can also be started by hand.

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
| `rso.tenants.ids` | Comma-separated tenant ids (`[a-z][a-z0-9_]{0,30}`), env `RSO_TENANTS_IDS` | none in prod, startup fails without it (`avto,tech` in dev/test) | runtime |
| `quarkus.hibernate-orm.multitenant` | Multitenancy strategy | `SCHEMA` | build time |
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
├── exceptions     Domain exceptions and their HTTP mappers
└── tenancy        Tenant resolution, schema per tenant, migrations per tenant
src/main/resources  Configuration; Liquibase migrations in db/changelog
src/main/docker     Dockerfile
src/test            Tests
```

## Branching strategy

- `main`: production; every merge deploys to the AKS cluster.
- `dev`: day-to-day development; merged into `main` at milestones.
