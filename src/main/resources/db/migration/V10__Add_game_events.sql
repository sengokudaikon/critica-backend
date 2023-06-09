CREATE TABLE IF NOT EXISTS day_events (
                            id UUID PRIMARY KEY,
                            game_id UUID REFERENCES games(id) ON DELETE CASCADE ON UPDATE CASCADE,
                            day INT NOT NULL,
                            stage VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS day_candidates (
                                id UUID PRIMARY KEY,
                                day_id UUID REFERENCES day_events(id) ON DELETE CASCADE ON UPDATE CASCADE,
                                player_id UUID REFERENCES players(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS day_event_votes (
                                 id UUID PRIMARY KEY,
                                 day_id UUID REFERENCES day_events(id) ON DELETE CASCADE ON UPDATE CASCADE,
                                 player_id UUID REFERENCES players(id) ON DELETE CASCADE ON UPDATE CASCADE,
                                 candidate_id UUID REFERENCES players(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS night_events (
                              id UUID PRIMARY KEY,
                              night INT NOT NULL,
                              game_id UUID REFERENCES games(id) ON DELETE CASCADE ON UPDATE CASCADE,
                              mafia_shot UUID REFERENCES players(id) ON DELETE CASCADE ON UPDATE CASCADE,
                              detective_check UUID REFERENCES players(id) ON DELETE CASCADE ON UPDATE CASCADE,
                              don_check UUID REFERENCES players(id) ON DELETE CASCADE ON UPDATE CASCADE
);
