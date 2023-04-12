CREATE TABLE IF NOT EXISTS users (
                                     id SERIAL PRIMARY KEY,
                                     email VARCHAR(100) NOT NULL UNIQUE,
                                     hashed_password VARCHAR(100) NOT NULL,
                                     player_name VARCHAR(100) NOT NULL,
                                     is_admin BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS players (
                                       id SERIAL PRIMARY KEY,
                                       game_id INT NOT NULL,
                                       user_id INT NOT NULL,
                                       name VARCHAR(100) NOT NULL,
                                       status VARCHAR(20) NOT NULL,
                                       role VARCHAR(20) NOT NULL,
                                       FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE,
                                       FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_tokens (
                                           id SERIAL PRIMARY KEY,
                                           user_id INT NOT NULL,
                                           token VARCHAR(200) NOT NULL,
                                           expiration TIMESTAMP NOT NULL,
                                           FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS games (
                                     id SERIAL PRIMARY KEY,
                                     date TIMESTAMP NOT NULL,
                                     day_events TEXT,
                                     night_events TEXT,
                                     winner_role VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS game_players (
                                            id SERIAL PRIMARY KEY,
                                            game_id INT NOT NULL,
                                            player_id INT NOT NULL,
                                            FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE,
                                            FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE
);

