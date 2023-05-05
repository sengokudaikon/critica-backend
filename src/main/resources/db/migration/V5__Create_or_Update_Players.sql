CREATE TABLE IF NOT EXISTS players (
    id uuid PRIMARY KEY,
    user_id uuid REFERENCES users(id) ON DELETE SET NULL,
    lobby_id uuid NOT NULL REFERENCES lobbies(id) ON DELETE CASCADE ON UPDATE CASCADE,
    game_id uuid REFERENCES games(id) ON DELETE SET NULL,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    seat INTEGER DEFAULT 0,
    bonus_points INTEGER DEFAULT 0
    );
