CREATE TABLE IF NOT EXISTS lobbies (
    id uuid PRIMARY KEY,
    date date NOT NULL default now()
);