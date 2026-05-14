CREATE TABLE leagues (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL UNIQUE,
    country VARCHAR(120) NOT NULL
);

CREATE TABLE teams (
    team_id BIGSERIAL PRIMARY KEY,
    league_id BIGINT NOT NULL REFERENCES leagues(id),
    name VARCHAR(120) NOT NULL,
    short_name VARCHAR(20) NOT NULL
);

CREATE TABLE players (
    player_id BIGSERIAL PRIMARY KEY,
    team_id BIGINT NOT NULL REFERENCES teams(team_id),
    first_name VARCHAR(80) NOT NULL,
    last_name VARCHAR(80) NOT NULL,
    position VARCHAR(40) NOT NULL,
    shirt_number INTEGER NOT NULL
);
