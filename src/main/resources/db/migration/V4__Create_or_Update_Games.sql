CREATE TABLE IF NOT EXISTS games (
    id uuid PRIMARY KEY,
    host_id uuid REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    lobby_id uuid REFERENCES lobbies(id) ON DELETE CASCADE ON UPDATE CASCADE,
    date VARCHAR(255),
    status VARCHAR(255),
    winner VARCHAR(255) NULL
);
