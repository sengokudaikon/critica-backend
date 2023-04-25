CREATE TABLE IF NOT EXISTS games (
    id uuid PRIMARY KEY,
    lobby_id uuid REFERENCES lobbies(id) ON DELETE CASCADE,
    date VARCHAR(255),
    status VARCHAR(255),
    winner VARCHAR(255) NULL
);
