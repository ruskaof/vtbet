--liquibase formatted sql

--changeset svytoq:create_users_table
CREATE TABLE IF NOT EXISTS users
(
    user_id BIGINT PRIMARY KEY,
    registration_date TIMESTAMP
);

--changeset svytoq:create_currency_table
CREATE TABLE IF NOT EXISTS currency
(
    currency_id BIGINT PRIMARY KEY,
    currency_name VARCHAR(255)
);

--changeset svytoq:create_user_account_table
CREATE TABLE IF NOT EXISTS user_account
(
    user_id BIGINT PRIMARY KEY,
    balance_amount DECIMAL(100,2),
    currency_id BIGINT REFERENCES currency (currency_id) ON DELETE CASCADE,
    username VARCHAR(255),
    email VARCHAR(255),
    phone_number VARCHAR(255),
    account_verified BOOLEAN
);

--changeset svytoq:create_sport_table
CREATE TABLE IF NOT EXISTS sport
(
    sport_id BIGINT PRIMARY KEY,
    sport_name VARCHAR(255)
);

--changeset svytoq:create_matches_table
CREATE TABLE IF NOT EXISTS matches
(
    match_id  BIGINT PRIMARY KEY,
    match_name VARCHAR(255),
    sport_id BIGINT REFERENCES sport (sport_id) ON DELETE CASCADE
);

--changeset svytoq:create_bet_group_table
CREATE TABLE IF NOT EXISTS bet_group
(
    bet_group_id BIGINT PRIMARY KEY
);

--changeset svytoq:create_type_of_bet_table
CREATE TABLE IF NOT EXISTS type_of_bet
(
    type_of_bet_id BIGINT PRIMARY KEY,
    description VARCHAR(255),
    bet_group_id BIGINT REFERENCES bet_group (bet_group_id) ON DELETE CASCADE

);

--changeset svytoq:create_type_of_bet_match_table
CREATE TABLE IF NOT EXISTS type_of_bet_match
(
    type_of_bet_match_id  BIGINT PRIMARY KEY,
    ratio_now DECIMAL(100,2),
    match_id BIGINT REFERENCES matches (match_id) ON DELETE CASCADE,
    type_of_bet_id BIGINT REFERENCES type_of_bet (type_of_bet_id) ON DELETE CASCADE
);


--changeset svytoq:create_bets_table
CREATE TABLE IF NOT EXISTS type_of_bat_match
(
    bet_id BIGINT PRIMARY KEY,
    user_id BIGINT REFERENCES users (user_id) ON DELETE CASCADE,
    currency_id BIGINT REFERENCES currency (currency_id) ON DELETE CASCADE,
    amount DECIMAL(100,2),
    type_of_bet_match_id BIGINT REFERENCES type_of_bet_match (type_of_bet_match_id) ON DELETE CASCADE,
    ratio DECIMAL(100,2)
);