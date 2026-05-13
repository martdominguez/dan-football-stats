CREATE TABLE team_standings (
    id BIGSERIAL PRIMARY KEY,
    team_id UUID NOT NULL UNIQUE,
    team_name VARCHAR(120) NOT NULL,
    league_name VARCHAR(120) NOT NULL,
    played INTEGER NOT NULL,
    won INTEGER NOT NULL,
    drawn INTEGER NOT NULL,
    lost INTEGER NOT NULL,
    goals_for INTEGER NOT NULL,
    goals_against INTEGER NOT NULL,
    points INTEGER NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE player_scorers (
    id BIGSERIAL PRIMARY KEY,
    player_id UUID NOT NULL UNIQUE,
    player_name VARCHAR(120) NOT NULL,
    team_id UUID NOT NULL,
    team_name VARCHAR(120) NOT NULL,
    goals INTEGER NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);
