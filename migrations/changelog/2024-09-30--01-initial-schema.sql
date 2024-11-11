--liquibase formatted sql

--changeset svytoq:create_users_table
CREATE TABLE IF NOT EXISTS users
(
    user_id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS roles
(
    role_id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

INSERT INTO roles (role_id, name) values (1, 'USER');
INSERT INTO roles (role_id, name) values (2, 'BET_ADMIN');
INSERT INTO roles (role_id, name) values (3, 'USER_VERIFIER');
INSERT INTO roles (role_id, name) values (4, 'SERVICES_ADMIN');

CREATE TABLE IF NOT EXISTS users_roles
(
    user_id BIGINT REFERENCES users(user_id),
    role_id BIGINT REFERENCES roles(role_id),
    PRIMARY KEY (user_id, role_id)
);

--changeset svytoq:create_user_account_table
CREATE TABLE IF NOT EXISTS users_accounts
(
    user_id BIGINT REFERENCES users (user_id) ON DELETE CASCADE NOT NULL UNIQUE PRIMARY KEY,
    balance_amount DECIMAL(100,2) NOT NULL DEFAULT 0,
    email VARCHAR(255),
    phone_number VARCHAR(255),
    account_verified BOOLEAN NOT NULL DEFAULT FALSE,
    registration_date TIMESTAMP WITH TIME ZONE NOT NULL
);

--changeset svytoq:create_sport_table
CREATE TABLE IF NOT EXISTS sports
(
    sport_id BIGSERIAL PRIMARY KEY,
    sport_name VARCHAR(255) NOT NULL
);

--changeset svytoq:create_matches_table
CREATE TABLE IF NOT EXISTS matches
(
    match_id  BIGSERIAL PRIMARY KEY,
    match_name VARCHAR(255) NOT NULL,
    sport_id BIGINT NOT NULL REFERENCES sports (sport_id) ON DELETE CASCADE,
    ended BOOLEAN NOT NULL DEFAULT FALSE
);

--changeset svytoq:create_index_match_name
CREATE INDEX IF NOT EXISTS match_name_idx ON matches (match_name);

--changeset svytoq:create_type_of_bet_table
CREATE TABLE IF NOT EXISTS bets_groups
(
    group_id BIGSERIAL PRIMARY KEY,
    description VARCHAR(255) NOT NULL
);

--changeset svytoq:create_type_of_bet_match_table
CREATE TABLE IF NOT EXISTS available_bets
(
    available_bet_id BIGSERIAL PRIMARY KEY,
    ratio DECIMAL(100,2) NOT NULL,
    bets_closed BOOLEAN NOT NULL DEFAULT FALSE,
    match_id BIGINT NOT NULL REFERENCES matches (match_id) ON DELETE CASCADE,
    group_id BIGINT NOT NULL REFERENCES bets_groups (group_id) ON DELETE CASCADE
);


--changeset svytoq:create_bets_table
CREATE TABLE IF NOT EXISTS bets
(
    bet_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    amount DECIMAL(100,2) NOT NULL,
    ratio DECIMAL(100,2) NOT NULL,
    available_bet_id BIGINT NOT NULL REFERENCES available_bets (available_bet_id) ON DELETE CASCADE
);
