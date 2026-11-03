# NEXUS — The AI Brain

Java 21 / Spring Boot API with PostgreSQL and Flyway. This branch adds a paginated, user-scoped conversation-management API on top of the current main backend.

Main already includes user, conversation, message, OpenAI chat and basic memory APIs.

## Conversation management API added in this branch
Base path: `/api/v1/users/{userId}/conversations`

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

## Security
A UUID in a URL is not authentication. Do not expose these endpoints publicly until a trusted JWT identity is mapped to an internal user and ownership is enforced using the authenticated principal. Flyway owns schema changes; never edit applied migrations.
