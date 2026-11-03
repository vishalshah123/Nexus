# NEXUS Complete Backend Bundle

Java 21 / Spring Boot API source bundle for the existing NEXUS repository.

## Features
- User create and lookup
- Conversation create/list
- Message create/list, ordered by sequence number
- OpenAI Chat Completions integration through `POST /api/conversations/{conversationId}/chat`
- Conversation history is sent to OpenAI; user and assistant messages are persisted
- User memories create/list/delete
- Bean validation and consistent API error responses
- PostgreSQL + Flyway migration
- Postman collection
- Docker Compose example and health endpoint

## Install into existing repository

1. Back up `E:\\Nexus\\services\\api` before replacing anything.
2. Copy this bundle's `services/api/src/main/java/com/nexus/api` into:
   `E:\\Nexus\\services\\api\\src\\main\\java\\com\\nexus\\api`
3. Copy `services/api/pom.xml`, `services/api/Dockerfile`, and `services/api/src/main/resources/application.yaml` into the corresponding existing locations.
4. Do NOT delete the existing `V1__initial_schema.sql` or database volume. This bundle includes the existing V1 schema as a reference plus `V2__add_memory_source_and_indexes.sql`. If your existing V1 has already run, Flyway will only run V2.
5. If your existing V1 differs from the reference, review V2 before starting.
6. Add `OPENAI_API_KEY` to your local environment. Never commit the key.

## Environment variables

- `SPRING_DATASOURCE_URL`: defaults to `jdbc:postgresql://localhost:5434/nexus` for running from IntelliJ on Windows.
- `SPRING_DATASOURCE_USERNAME`: defaults to `nexus`.
- `SPRING_DATASOURCE_PASSWORD`: use the actual password configured for the PostgreSQL `nexus` role.
- `OPENAI_API_KEY`: required only for the AI chat endpoint.
- `OPENAI_MODEL`: defaults to `gpt-4.1-mini`.

When running through Docker Compose, API connects to `jdbc:postgresql://postgres:5432/nexus`. The Compose example publishes PostgreSQL on host port 5434 and API on 9095.

## Build and run

From `E:\\Nexus\\services\\api`:
```powershell
.\\mvnw.cmd -DskipTests compile
```

From `E:\\Nexus`:
```powershell
docker compose up -d --build
```

Check:
`http://localhost:9095/actuator/health`

## Postman

Import `postman/NEXUS-Complete-API.postman_collection.json`. Run requests in order. Create User and Create Conversation requests save returned IDs to collection variables. AI chat requires `OPENAI_API_KEY` in the API process/container.

## Important design notes
- This is a development-ready baseline, not a production-secure multi-tenant deployment. Authentication/authorization is not yet implemented. Do not expose it publicly.
- The initial message sequence allocation uses `MAX(sequence_number) + 1`; concurrent writes to one conversation can race. Use a database-side atomic allocator/locking strategy before production.
- OpenAI requests are made server-side. The API key is never returned to clients.
- No real AI response is generated if `OPENAI_API_KEY` is missing; the chat endpoint returns a configuration error.
