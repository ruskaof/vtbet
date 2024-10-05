--liquibase formatted sql

--changeset svytoq:create_users_table
CREATE TABLE IF NOT EXISTS users
(
    id BIGSERIAL PRIMARY KEY,
    registration_date TIMESTAMP WITH TIME ZONE NOT NULL
);

--changeset svytoq:create_currency_table
CREATE TABLE IF NOT EXISTS currency
(
    currency_id BIGINT PRIMARY KEY,
    currency_name NOT NULL VARCHAR(255)
);

--changeset svytoq:create_user_account_table
CREATE TABLE IF NOT EXISTS user_account
(
    user_id BIGINT REFERENCES users (id) NOT NULL PRIMARY KEY,
    balance_amount DECIMAL(100,2) NOT NULL DEFAULT 0,
    currency_id BIGINT REFERENCES currency (currency_id) NOT NULL,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone_number VARCHAR(255),
    account_verified BOOLEAN NOT NULL DEFAULT FALSE
);

--changeset svytoq:create_sport_table
CREATE TABLE IF NOT EXISTS sport
(
    sport_id BIGSERIAL PRIMARY KEY,
    sport_name VARCHAR(255) NOT NULL
);

--changeset svytoq:create_matches_table
CREATE TABLE IF NOT EXISTS matches
(
    match_id  BIGSERIAL PRIMARY KEY,
    match_name VARCHAR(255) NOT NULL,
    sport_id BIGINT NOT NULL REFERENCES sport (sport_id)
);

--changeset svytoq:create_bet_group_table
CREATE TABLE IF NOT EXISTS bet_group
(
    bet_group_id BIGSERIAL PRIMARY KEY
);

--changeset svytoq:create_type_of_bet_table
CREATE TABLE IF NOT EXISTS type_of_bet
(
    type_of_bet_id BIGSERIAL PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    bet_group_id BIGINT NOT NULL REFERENCES bet_group (bet_group_id)
);

--changeset svytoq:create_type_of_bet_match_table
CREATE TABLE IF NOT EXISTS type_of_bet_match
(
    type_of_bet_match_id BIGINT PRIMARY KEY,
    ratio_now DECIMAL(100,2) NOT NULL,
    match_id BIGINT NOT NULL REFERENCES matches (match_id),
    type_of_bet_id BIGINT NOT NULL REFERENCES type_of_bet (type_of_bet_id)
);


--changeset svytoq:create_bets_table
CREATE TABLE IF NOT EXISTS type_of_bet_match
(
    bet_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users (user_id),
    currency_id BIGINT NOT NULL REFERENCES currency (currency_id),
    amount DECIMAL(100,2) NOT NULL,
    type_of_bet_match_id BIGINT NOT NULL REFERENCES type_of_bet_match (type_of_bet_match_id),
    ratio DECIMAL(100,2) NOT NULL
);
