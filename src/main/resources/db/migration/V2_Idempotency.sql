CREATE TABLE IF NOT EXISTS idempotency_keys (
    key_text TEXT PRIMARY KEY,
    ride_id BIGINT NULL,
    status TEXT NOT NULL, -- "IN_PROGRESS" or "COMPLETED"
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Index on ride_id for fast lookup (optional)
CREATE INDEX IF NOT EXISTS idx_idempotency_ride_id ON idempotency_keys(ride_id);
