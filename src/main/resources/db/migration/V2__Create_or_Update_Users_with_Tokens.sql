CREATE TABLE IF NOT EXISTS users (
    id uuid PRIMARY KEY,
    uid VARCHAR(255) UNIQUE NOT NULL,
    club_id uuid references clubs(id) ON DELETE CASCADE ON UPDATE CASCADE,
    email VARCHAR(255) UNIQUE NOT NULL,
    player_name VARCHAR(255),
    role INTEGER NOT NULL DEFAULT 0,
    created_at date NOT NULL DEFAULT NOW(),
    updated_at date NOT NULL DEFAULT NOW()
    );
ALTER TABLE IF EXISTS users ADD CONSTRAINT role_check CHECK (role IN (0, 1, 2));