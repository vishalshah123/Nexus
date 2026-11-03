CREATE TABLE memory_entries (
 id UUID PRIMARY KEY, user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
 content TEXT NOT NULL, category VARCHAR(80) NOT NULL DEFAULT 'GENERAL',
 source VARCHAR(120), importance SMALLINT NOT NULL DEFAULT 3 CHECK (importance BETWEEN 1 AND 5),
 tags JSONB NOT NULL DEFAULT '[]'::jsonb, created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_memory_entries_user_updated ON memory_entries(user_id,updated_at DESC);
CREATE INDEX idx_memory_entries_tags ON memory_entries USING GIN(tags);
CREATE TABLE nexus_tool_audit (
 id UUID PRIMARY KEY, user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
 tool_name VARCHAR(120) NOT NULL, arguments JSONB NOT NULL DEFAULT '{}'::jsonb,
 status VARCHAR(30) NOT NULL, approved_by VARCHAR(255),
 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE background_tasks (
 id UUID PRIMARY KEY, user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
 task_type VARCHAR(80) NOT NULL, payload JSONB NOT NULL DEFAULT '{}'::jsonb,
 status VARCHAR(20) NOT NULL DEFAULT 'QUEUED' CHECK(status IN ('QUEUED','RUNNING','COMPLETED','FAILED','CANCELLED')),
 result JSONB, error_message TEXT, created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 started_at TIMESTAMPTZ, completed_at TIMESTAMPTZ
);
CREATE INDEX idx_background_tasks_user_created ON background_tasks(user_id,created_at DESC);
CREATE INDEX idx_background_tasks_queue ON background_tasks(status,created_at) WHERE status='QUEUED';
