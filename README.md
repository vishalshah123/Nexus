# NEXUS — The AI Brain

NEXUS is a Java 21 / Spring Boot API foundation for persistent conversations, long-term memory, AI-provider orchestration, context retrieval, controlled tools and background work.

## Capabilities added in this branch

- **AI provider abstraction:** one `AiProvider` contract with OpenAI, Gemini and Anthropic Claude adapters. API keys are environment variables; no credentials are committed.
- **Long-term memory API:** create, search, update and delete user memories with category, source, importance and tags.
- **Context retrieval API:** assemble a bounded set of recent conversation messages and high-importance memories for an AI request.
- **Tool registry:** exposes only `nexus.time` and `nexus.echo` (read-only/safe demo tools) and writes an execution audit record. Arbitrary shell, SQL, filesystem, trading and messaging tools are deliberately not implemented.
- **Background task queue API:** persist, list, inspect and cancel queued task records. **A worker/dispatcher is not included yet**, so tasks remain QUEUED until a worker is implemented and deployed.
- **OAuth2 resource-server security foundation:** optional JWT validation using a configured issuer. Enable with `NEXUS_SECURITY_ENABLED=true` and configure `NEXUS_JWT_ISSUER_URI`.

## Configuration

Database:
- `SPRING_DATASOURCE_URL` (local default: `jdbc:postgresql://localhost:5433/nexus`)
- `SPRING_DATASOURCE_USERNAME`
- `POSTGRES_PASSWORD`

AI provider credentials:
- `NEXUS_OPENAI_API_KEY`
- `NEXUS_GEMINI_API_KEY`
- `NEXUS_ANTHROPIC_API_KEY`

Optional provider base URLs:
- `NEXUS_OPENAI_BASE_URL`
- `NEXUS_GEMINI_BASE_URL`
- `NEXUS_ANTHROPIC_BASE_URL`

## API surface

| Capability | Endpoint |
|---|---|
| Provider list | `GET /api/v1/ai/providers` |
| AI completion | `POST /api/v1/ai/chat` |
| Memory CRUD/search | `/api/v1/users/{userId}/memories` |
| Context assembly | `GET /api/v1/users/{userId}/context?messageLimit=10&memoryLimit=10` |
| Tool catalog | `GET /api/v1/tools` |
| Safe tool execution | `POST /api/v1/tools/execute` |
| Background task queue | `/api/v1/users/{userId}/tasks` |

Memory create example:
```json
{
  "content": "Prefers concise technical explanations",
  "category": "PREFERENCE",
  "source": "conversation",
  "importance": 4,
  "tags": ["response-style", "profile"]
}
```

AI chat example:
```json
{
  "provider": "openai",
  "model": "gpt-4.1-mini",
  "messages": [{"role": "user", "content": "Summarize this context"}],
  "systemPrompt": "You are NEXUS.",
  "temperature": 0.4,
  "maxTokens": 1200
}
```

## Security and production readiness

The security filter is disabled by default for local development. Before exposing NEXUS, enable JWT validation and configure a trusted issuer. The current APIs still accept `userId` in the URL, and the demo tool endpoint accepts `X-Nexus-User-Id`; these are **not identity proofs**. Next security work must map the validated JWT subject to an internal user and enforce ownership on every endpoint. Do not expose this branch publicly until that is complete.

Provider calls are synchronous. Add outbound timeouts, retry budgets, rate limiting, token/cost accounting and secret-safe logging before production. Background task records are durable but not yet processed by a worker. No arbitrary code or high-impact tool execution is enabled.
