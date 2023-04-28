CREATE TABLE IF NOT EXISTS users (
    id uuid PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    hashed_password VARCHAR(255) NOT NULL,
    player_name VARCHAR(255) NOT NULL,
    role INTEGER NOT NULL DEFAULT 0
    );
ALTER TABLE IF EXISTS users ADD CONSTRAINT role_check CHECK (role IN (0, 1, 2));