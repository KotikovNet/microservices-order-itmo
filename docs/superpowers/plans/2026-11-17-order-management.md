# Order Management Microservices Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans. Steps use checkbox (`- [ ]`) syntax for
> tracking.

**Goal:** Build a runnable Java 21 / Spring Boot 3 Maven multi-module order management system with three REST services
and isolated PostgreSQL databases.

**Architecture:** User, Order, and Notification are independent applications with separate persistence. Order
synchronously checks a user, persists an order, then records a notification over HTTP using bounded-timeout `RestClient`
calls.

**Tech Stack:** Java 21, Spring Boot 3.x, Spring Web, Spring Data JPA, Jakarta Validation, PostgreSQL, Maven, Docker
Compose.

**Spec:** `docs/superpowers/specs/2026-11-17-order-management-design.md`

## Global Constraints

- Use Java 21, Spring Boot 3.x, Spring Web, Spring Data JPA, PostgreSQL and Maven multi-module.
- Use HTTP REST and `RestClient` exclusively for service-to-service communication.
- Each service owns one database; no shared Java DTO module and no cross-service database access.
- Do not create test source sets/classes/directories or add JUnit, Mockito, WireMock or similar dependencies.
- Implement validation, safe consistent API errors, env-based configuration and Docker health checks.
- No Kubernetes, brokers, Spring Cloud, or other infrastructure beyond Docker Compose and PostgreSQL.

## Review Focus

- A duplicate email must produce a conflict response despite concurrent requests (enforced by a database unique
  constraint).
- A missing user must prevent an order insert; a user-service outage must map to a safe 503.
- A notification outage after order persistence must not imply the order was rolled back; return a safe 503 and make the
  behavior documented.
- Invalid payloads and malformed IDs must not leak framework exceptions or stack traces.
- Compose health checks and service/database dependencies must work with fresh volumes and a clean startup.

---

### Task 1: Maven parent and User Service

**Files:** root `pom.xml`; `user-service/pom.xml`; `user-service/src/main/java/...` application, entity, repository,
request/response DTOs, controller, service, exceptions/advice, config;
`user-service/src/main/resources/application.yml`; `user-service/Dockerfile`.

- [ ] Create the Maven aggregator and Java 21 Spring Boot parent configuration with exactly three modules and no test
  dependencies.
- [ ] Implement User CRUD, existence checks, email uniqueness, input constraints, DTO mapping, and safe 404/409/400/500
  error responses.
- [ ] Configure user DB settings from environment and local schema update; add a multi-stage Dockerfile.
- [ ] Check: `mvn -pl user-service -am -DskipTests package` succeeds.

### Task 2: Notification Service

**Files:** `notification-service/pom.xml`; service application/entity/repository/DTO/controller/service/error/config
classes; resources `application.yml`; `notification-service/Dockerfile`.

- [ ] Implement notification creation, listing, lookup by ID and user, persist status SENT, and log the simulated send.
- [ ] Validate DTOs and return consistent safe errors; configure its independent DB and image build.
- [ ] Check: `mvn -pl notification-service -am -DskipTests package` succeeds.

### Task 3: Order Service and HTTP workflow

**Files:** `order-service/pom.xml`; service application/entity/repository/DTO/controller/service/HTTP
client/config/error classes; resources `application.yml`; `order-service/Dockerfile`.

- [ ] Implement order CRUD and user-order listing, positive amount validation and supported statuses.
- [ ] Add `RestClient` configuration with connect/read timeouts and env-configured user/notification base URLs.
- [ ] On create, check user first; map absent user to 404 and dependency failure to 503 without persisting. Persist
  valid order, then request notification; if that call fails, return safe 503 while preserving the order.
- [ ] Check: `mvn -pl order-service -am -DskipTests package` succeeds.

### Task 4: Compose system and operational configuration

**Files:** root `docker-compose.yml`, `.env.example`, `.gitignore` (if needed), all service Dockerfiles/application
configs.

- [ ] Define three PostgreSQL containers with separate credentials/databases, persistent volumes and `pg_isready` health
  checks.
- [ ] Define three application containers, exposed ports, shared network, service health checks, env configuration, and
  `depends_on: condition: service_healthy` for databases.
- [ ] Validate Compose syntax with `docker compose config` and build with `docker compose build` when Docker is
  available.

### Task 5: Documentation and end-to-end operational check

**Files:** root `README.md`.

- [ ] Document architecture, module layout, environment setup, startup, ports, API payloads, curl examples for each API
  family, and complete order workflow.
- [ ] If Docker is available, start via `docker compose up --build -d`, check service health, exercise user creation,
  valid and missing-user order creation, notification listing, and CRUD endpoints with HTTP requests, then report
  results. Do not claim checks that could not run.
- [ ] Stop the compose stack after checks while preserving its volumes, and report exact commands/results and any
  environment limitation.
