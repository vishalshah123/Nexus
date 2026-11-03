# Nexus
Nexus: The AI Brain

## Current API foundation

The API is a Java 21 / Spring Boot service backed by PostgreSQL. Database tables are managed through Flyway migrations.

## Conversation management API

The initial conversation API supports:
- Create a conversation for an existing user.
- List a user's conversations, ordered by most recently updated.
- Fetch, rename, and delete a conversation.
- Append messages with ordered, per-conversation sequence numbers.
- Retrieve conversation messages with pagination.

Base path: `/api/v1/users/{userId}/conversations`

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/v1/users/{userId}/conversations` | Create conversation |
| GET | `/api/v1/users/{userId}/conversations?page=0&size=20` | List conversations |
| GET | `/api/v1/users/{userId}/conversations/{conversationId}` | Get conversation |
| PATCH | `/api/v1/users/{userId}/conversations/{conversationId}` | Rename conversation |
| DELETE | `/api/v1/users/{userId}/conversations/{conversationId}` | Delete conversation and its messages |
| POST | `/api/v1/users/{userId}/conversations/{conversationId}/messages` | Append a message |
| GET | `/api/v1/users/{userId}/conversations/{conversationId}/messages?page=0&size=50` | List messages |

Example create request:

```json
{
  "title": "Nexus planning",
  "provider": "openai"
}
```

Example append-message request:

```json
{
  "role": "user",
  "content": "Help me plan the next release."
}
```

Supported message roles: `system`, `user`, `assistant`, `tool`. Pagination size is limited to 100 records.

### Important security limitation

These endpoints currently use a user UUID in the URL to scope database queries. This is **not authentication or authorization**. Do not expose this API to the public internet or treat a supplied UUID as proof of identity. Before production use, integrate the NEXUS identity/authentication layer and derive the user ID from the authenticated principal, then add authorization tests.

The API expects the user row to have been provisioned by the identity/user-management layer; this feature intentionally does not add an unauthenticated user-registration endpoint.

## Running locally

Start PostgreSQL and configure the datasource, then run the API:

```bash
cd services/api
./mvnw spring-boot:run
```

On Windows use `mvnw.cmd spring-boot:run`.

Run the test suite with:

```bash
./mvnw test
```

On Windows use `mvnw.cmd test`.
