# NEXUS — The AI Brain

NEXUS is a Java 21 / Spring Boot API backed by PostgreSQL and Flyway. It includes user, conversation, message, memory, AI-provider, context, tool-audit, and background-task APIs.

## Run with Docker Compose

Copy `.env.example` to `.env`, set a strong local `POSTGRES_PASSWORD` (and `SPRING_DATASOURCE_PASSWORD` to the same value), then start the stack:

```powershell
Copy-Item .env.example .env
# Edit .env and set a strong local password
docker compose up --build -d
docker compose ps
```

PostgreSQL is published on host port **5434** and the API on **9095**. The API container connects to PostgreSQL at `postgres:5432`.

- NEXUS Console: http://localhost:9095/
- API health: http://localhost:9095/actuator/health

The console is served by Spring Boot from `src/main/resources/static`; no separate frontend server is required. It currently supports user creation/selection, conversations, persisted OpenAI chat, long-term memory CRUD, allowlisted demo tools, and queue inspection/creation.

To stop the services without deleting database data:

```powershell
docker compose down
```

## Run locally

Start PostgreSQL, then run the API from its Maven module:

```powershell
cd services\api
.\mvnw.cmd spring-boot:run
```

The local API connects to `jdbc:postgresql://localhost:5434/nexus`. Configure database credentials and optional AI/JWT settings using process environment variables.

## API areas

- Users: `/api/users`
- Legacy conversations/messages: `/api/conversations` and `/api/conversations/{conversationId}/messages`
- Persisted OpenAI chat: `/api/conversations/{conversationId}/chat`
- Multi-provider AI: `/api/v1/ai`
- Long-term memory: `/api/v1/users/{userId}/memories`
- Context: `/api/v1/users/{userId}/context`
- Tools and audit: `/api/v1/tools`
- Background task queue: `/api/v1/users/{userId}/tasks`

## Database and security

Flyway owns schema changes under `services/api/src/main/resources/db/migration`; add sequential migrations and never edit migrations already applied to persistent databases. Hibernate uses `ddl-auto: validate`.

Security is disabled by default for local development. When enabling JWT validation, configure `NEXUS_JWT_ISSUER_URI` to a trusted issuer. Authentication alone does not map a JWT subject to the internal user ID or enforce ownership on every endpoint. Do not expose this API or console publicly until authentication, authorization, and ownership checks are implemented. Keep provider credentials and database passwords out of source control.

## Current implementation notes

- Background tasks can be enqueued and inspected, but a worker/consumer is not implemented yet.
- The tool registry currently exposes only the safe demo tools `nexus.time` and `nexus.echo`.
- The console's chat panel uses the configured OpenAI chat endpoint; set `OPENAI_API_KEY` to enable it.
- Run Maven tests and a Docker Compose smoke test locally before using the system beyond development.
