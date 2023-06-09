CREATE TABLE IF NOT EXISTS user_ratings (
    id uuid PRIMARY KEY,
    user_id uuid NOT NULL references users(id) on delete cascade on update cascade,
    total_points int NOT NULL,
    bonus_points int NOT NULL,
    malus_points int NOT NULL,
    best_move_points int NOT NULL,
    created_at date NOT NULL DEFAULT NOW(),
    updated_at date NOT NULL DEFAULT NOW()
)