# NEXUS — The AI Brain

Java 21 / Spring Boot API with PostgreSQL and Flyway. This branch extends the main backend with multi-provider AI, enriched long-term memory, context retrieval, controlled tools, background-task records and an OAuth2 resource-server foundation.

## Capabilities
- Main APIs: user, conversation, message, OpenAI chat and basic memory.
- **Multi-provider AI:** common provider interface with OpenAI, Gemini and Anthropic Claude adapters.
- **Long-term memory:** create, search, update and delete memories with category, importance and tags.
- **Context retrieval:** bounded recent messages plus high-importance memories.
- **Tool registry:** only `nexus.time` and `nexus.echo`; execution is audited. No arbitrary shell, SQL, filesystem, trading or messaging tools.
- **Background tasks:** durable enqueue/list/get/cancel API. No worker is included yet; queued tasks remain QUEUED.
- **JWT foundation:** optional OAuth2 resource-server validation. Enable with `NEXUS_SECURITY_ENABLED=true` and set `NEXUS_JWT_ISSUER_URI`.

## Configuration
Database host port **5434** (Compose container port 5432); API port **9095**.
- OpenAI: `OPENAI_API_KEY`, `OPENAI_MODEL`, `OPENAI_BASE_URL`. Provider adapter also accepts `NEXUS_OPENAI_API_KEY` as an override.
- Gemini: `NEXUS_GEMINI_API_KEY`
- Claude: `NEXUS_ANTHROPIC_API_KEY`
- JWT: `NEXUS_SECURITY_ENABLED`, `NEXUS_JWT_ISSUER_URI`

Copy `.env.example` to `.env` and set local secrets. Never commit `.env`.

## Additional endpoints
| Capability | Endpoint |
|---|---|
| Providers | `GET /api/v1/ai/providers` |
| AI completion | `POST /api/v1/ai/chat` |
| Memory CRUD/search | `/api/v1/users/{userId}/memories` |
| Context | `GET /api/v1/users/{userId}/context` |
| Tool catalog | `GET /api/v1/tools` |
| Tool execution | `POST /api/v1/tools/execute` |
| Background tasks | `/api/v1/users/{userId}/tasks` |

## Migration ordering
Main has `V1__initial_schema.sql` and `V2__add_memory_source_and_indexes.sql`. This branch adds `V3__nexus_brain_features.sql`. Never edit migrations already applied to a persistent database.

## Production readiness
JWT validation exists, but routes still accept user IDs in paths/headers; map JWT subject to internal user and enforce ownership before public exposure. Provider calls need production timeout/retry/rate-limit/cost controls. Background queue records need a worker. No high-impact tools are enabled.
