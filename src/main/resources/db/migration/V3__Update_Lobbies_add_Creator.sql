ALTER TABLE lobbies
    ADD COLUMN creator uuid REFERENCES users(id) ON DELETE CASCADE;
