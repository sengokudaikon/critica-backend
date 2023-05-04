CREATE TABLE IF NOT EXISTS user_role_statistics (
    id uuid PRIMARY KEY,
    user_rating_id uuid NOT NULL,
    role int not null default 0,
    gamesWon int not null default 0,
    gamesPlayed int not null default 0,
    bonusPoints int not null default 0,
    foreign key (user_rating_id) references user_ratings(id) on delete cascade on update cascade
)