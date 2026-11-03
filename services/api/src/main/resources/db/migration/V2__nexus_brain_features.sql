ALTER TABLE memories ADD COLUMN importance SMALLINT NOT NULL DEFAULT 3 CHECK (importance BETWEEN 1 AND 5);
ALTER TABLE memories ADD COLUMN tags JSONB NOT NULL DEFAULT '[]'::jsonb;
CREATE INDEX idx_memories_user_importance ON memories(user_id,importance DESC,updated_at DESC);
CREATE INDEX idx_memories_tags ON memories USING GIN(tags);
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
