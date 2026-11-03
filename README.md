# NEXUS — The AI Brain

NEXUS is a Java 21 / Spring Boot API backed by PostgreSQL and Flyway. The API includes user, conversation, message, memory, AI-provider, context, tool-audit, and background-task endpoints.

## Run with Docker Compose

Copy `.env.example` to `.env`, set a strong `POSTGRES_PASSWORD`, then start the services:

```powershell
Copy-Item .env.example .env
# Set POSTGRES_PASSWORD in .env before starting
docker compose up --build -d
docker compose ps
```

PostgreSQL is published on host port **5434** and the API on **9095**. The API container connects to PostgreSQL at `postgres:5432`. Health is available at `http://localhost:9095/actuator/health`.

To stop the services without deleting database data:

```powershell
docker compose down
```

## Run locally

Start PostgreSQL, then run the API from its Maven module:

```powershell
cd services\api
mvn clean install
mvn spring-boot:run
```

The local API connects to `jdbc:postgresql://localhost:5434/nexus`. Set the database and optional AI/JWT settings in the process environment. Docker Compose loads them from `.env`.

## API areas

- User and legacy conversation APIs: `/api/users`, `/api/conversations`, and `/api/conversations/{conversationId}/messages`
- User-scoped conversation management: `/api/v1/users/{userId}/conversations`
- OpenAI chat: `/api/conversations/{conversationId}/chat`
- Multi-provider AI: `/api/v1/ai`
- User memory and context: `/api/v1/users/{userId}/memories` and `/api/v1/users/{userId}/context`
- Tools and background tasks: `/api/v1/tools` and `/api/v1/users/{userId}/tasks`

Conversation message roles are `system`, `user`, `assistant`, and `tool`. Paginated conversation endpoints limit page size to 100.

## Database and security

Flyway owns schema changes under `services/api/src/main/resources/db/migration`; add sequential migrations and do not edit migrations already applied to persistent databases. Hibernate uses `ddl-auto: validate`.

Security is disabled by default for local development. When enabling JWT validation, configure `NEXUS_JWT_ISSUER_URI` to a trusted issuer. Authentication alone does not map a JWT subject to the internal user ID or enforce user ownership on every endpoint, so do not expose the API publicly until that authorization mapping is implemented. Keep provider credentials and database passwords out of source control.
