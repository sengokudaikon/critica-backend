ALTER TABLE users ADD IF NOT EXISTS token_id INTEGER REFERENCES user_tokens(id) ON DELETE CASCADE