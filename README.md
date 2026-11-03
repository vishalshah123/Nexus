# NEXUS — The AI Brain

NEXUS is a local-first AI assistant platform. The current repository contains the initial API service and PostgreSQL persistence foundation.

## Repository layout

```text
.
├── compose.yml
├── .env.example
└── services/
    └── api/
        ├── Dockerfile
        ├── pom.xml
        └── src/
            ├── main/
            │   ├── java/com/nexus/api/
            │   └── resources/
            │       └── db/migration/
            └── test/
```

## Prerequisites

- Docker Engine / Docker Desktop with Docker Compose
- Java 21 and Maven (only when running the API outside Docker)

## Run with Docker Compose

1. Copy the example environment file:

   ```bash
   cp .env.example .env
   ```

   On Windows PowerShell:

   ```powershell
   Copy-Item .env.example .env
   ```

2. Replace the placeholder values for `POSTGRES_PASSWORD` and `SPRING_DATASOURCE_PASSWORD` in `.env` with the same strong local password.

3. Build and start the services:

   ```bash
   docker compose up --build -d
   ```

4. Check service status and API health:

   ```bash
   docker compose ps
   ```

   Open [http://localhost:9095/actuator/health](http://localhost:9095/actuator/health).

5. Stop the services:

   ```bash
   docker compose down
   ```

   Add `-v` only when you intentionally want to delete the local PostgreSQL data volume.

## Run the API locally

Start PostgreSQL first (for example, with `docker compose up -d postgres`), then run:

```bash
cd services/api
./mvnw spring-boot:run
```

On Windows use `mvnw.cmd spring-boot:run`.

The host-run API uses `SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/nexus` by default. Set `POSTGRES_PASSWORD` or `SPRING_DATASOURCE_PASSWORD` in the environment before starting it.

## Database migrations

Flyway migrations live in `services/api/src/main/resources/db/migration`. Add new schema changes as sequential migration files (for example, `V2__add_memory_metadata.sql`). Do not edit a migration that has already been applied to a shared or persistent database.

Hibernate is configured with `ddl-auto=validate`; Flyway owns schema changes.

## Current implementation scope

The API currently provides the Spring Boot application bootstrap, PostgreSQL/Flyway configuration, and the initial users, conversations, messages, and memories schema. Domain APIs, authentication/authorization, AI-provider integration, and automated integration coverage are not implemented yet.

## Configuration and secrets

- Never commit `.env` or real credentials.
- Keep local secrets in your untracked `.env` file or inject them through the deployment environment.
- The values in `.env.example` are placeholders, not production credentials.
