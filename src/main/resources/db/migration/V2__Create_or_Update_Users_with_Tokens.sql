CREATE TABLE IF NOT EXISTS users (
    id uuid PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    hashed_password VARCHAR(255) NOT NULL,
    player_name VARCHAR(255) NOT NULL,
    is_admin BOOLEAN DEFAULT false
    );