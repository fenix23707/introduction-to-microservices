CREATE TABLE IF NOT EXISTS storage (
    id           BIGSERIAL PRIMARY KEY,
    storage_type VARCHAR(50)  NOT NULL,
    bucket       VARCHAR(255) NOT NULL,
    path         VARCHAR(1024) NOT NULL,
    created_at   TIMESTAMPTZ   NOT NULL DEFAULT now()
);
