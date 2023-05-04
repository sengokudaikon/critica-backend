CREATE TABLE IF NOT EXISTS user_settings (
    id uuid PRIMARY KEY,
    user_id uuid NOT NULL,
    email_verified bool NOT NULL,
    public_visibility bool NOT NULL,
    push_notifications bool NOT NULL,
    language varchar(10) NOT NULL,
    promotion_status bool,
    created_at BIGINT NOT NULL,
    updated_at BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
);