CREATE INDEX IF NOT EXISTS user_tokens_user_id_token_idx ON user_tokens ("userId", token);
