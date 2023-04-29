CREATE TABLE IF NOT EXISTS user_tokens (
                                           id uuid PRIMARY KEY,
                                           user_id uuid REFERENCES users(id) ON DELETE CASCADE,
                                           token VARCHAR(255),
                                           expires_at BIGINT,
                                           created_at BIGINT
);
