# NEXUS — The AI Brain

Java 21 / Spring Boot API with PostgreSQL and Flyway persistence. The main line currently includes user, conversation, message, memory and OpenAI chat APIs.

## Runtime configuration

Copy `.env.example` to `.env` and replace the database password with a strong local value. The `.env` file is ignored by Git and must never be committed.

- Docker Compose publishes PostgreSQL on host port **5434** and the API on **9095**.
- The API container connects to PostgreSQL at `postgres:5432`.
- Running the API from IntelliJ uses `jdbc:postgresql://localhost:5434/nexus`.
- OpenAI settings use `OPENAI_API_KEY`, `OPENAI_MODEL` and `OPENAI_BASE_URL`.

## Run with Docker

```powershell
Copy-Item .env.example .env
# Set POSTGRES_PASSWORD in .env before starting
docker compose up --build -d
docker compose ps
```

Health: http://localhost:9095/actuator/health

Stop without deleting data:

```powershell
docker compose down
```

## Run locally

Start PostgreSQL, then:

```powershell
cd services/api
mvnw.cmd spring-boot:run
```

## Database

Flyway owns schema changes under `services/api/src/main/resources/db/migration`. Add sequential migrations; never edit a migration already applied to a persistent database. Hibernate uses `ddl-auto: validate`.

## Security and production notes

The current API is a development foundation, not a production-secure multi-tenant service. Do not expose it publicly until JWT authentication and per-user authorization are enforced on every endpoint. Keep provider credentials and database passwords out of source control. Use a local untracked `.env` or a secrets manager.
