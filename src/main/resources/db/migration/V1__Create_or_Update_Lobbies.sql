CREATE TABLE IF NOT EXISTS lobbies (
    id uuid PRIMARY KEY,
    club_id uuid references clubs(id) ON DELETE CASCADE ON UPDATE CASCADE,
    creator uuid REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    date date NOT NULL default now()
);