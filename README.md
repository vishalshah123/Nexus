# NEXUS — The AI Brain

Java 21 / Spring Boot API with PostgreSQL and Flyway persistence. The main line currently includes user, conversation, message, memory and OpenAI chat APIs.

## Runtime configuration
# NEXUS — The AI Brain

Java 21 / Spring Boot API with PostgreSQL and Flyway. This branch adds a paginated, user-scoped conversation-management API on top of the current main backend.
Copy `.env.example` to `.env` and replace the database password with a strong local value. The `.env` file is ignored by Git and must never be committed.

Main already includes user, conversation, message, OpenAI chat and basic memory APIs.
- Docker Compose publishes PostgreSQL on host port **5434** and the API on **9095**.
- The API container connects to PostgreSQL at `postgres:5432`.
- Running the API from IntelliJ uses `jdbc:postgresql://localhost:5434/nexus`.
- OpenAI settings use `OPENAI_API_KEY`, `OPENAI_MODEL` and `OPENAI_BASE_URL`.

## Conversation management API added in this branch
Base path: `/api/v1/users/{userId}/conversations`
## Run with Docker

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/v1/users/{userId}/conversations` | Create conversation |
| GET | `/api/v1/users/{userId}/conversations?page=0&size=20` | List conversations |
| GET | `/api/v1/users/{userId}/conversations/{conversationId}` | Get conversation |
| PATCH | `/api/v1/users/{userId}/conversations/{conversationId}` | Rename conversation |
| DELETE | `/api/v1/users/{userId}/conversations/{conversationId}` | Delete conversation and messages |
| POST | `/api/v1/users/{userId}/conversations/{conversationId}/messages` | Append a message |
| GET | `/api/v1/users/{userId}/conversations/{conversationId}/messages?page=0&size=50` | List messages |

Message roles: `system`, `user`, `assistant`, `tool`. Page size is limited to 100.

## Run
Copy `.env.example` to `.env`, set a strong `POSTGRES_PASSWORD`, then run `docker compose up --build -d`. PostgreSQL host port is **5434**; API port is **9095**. For local execution use `mvnw.cmd spring-boot:run` in `services/api`.
```powershell
Copy-Item .env.example .env
# Set POSTGRES_PASSWORD in .env before starting
docker compose up --build -d
docker compose ps
```

## Security
A UUID in a URL is not authentication. Do not expose these endpoints publicly until a trusted JWT identity is mapped to an internal user and ownership is enforced using the authenticated principal. Flyway owns schema changes; never edit applied migrations.
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
