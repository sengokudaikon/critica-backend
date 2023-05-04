CREATE TABLE IF NOT EXISTS user_ratings (
    id uuid PRIMARY KEY,
    user_id uuid NOT NULL,
    total_points int NOT NULL,
    bonus_points int NOT NULL,
    malus_points int NOT NULL,
    best_move_points int NOT NULL,
    foreign key (user_id) references users(id) on delete cascade on update cascade
)