CREATE TABLE IF NOT EXISTS user_tokens (
                                           id SERIAL PRIMARY KEY,
                                           user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
                                           token VARCHAR(200),
                                           expires_at BIGINT,
                                           created_at BIGINT
);
