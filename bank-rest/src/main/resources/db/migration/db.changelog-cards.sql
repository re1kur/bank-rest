--liquibase formatted sql

--changeset re1kur:1
CREATE TABLE IF NOT EXISTS cards
(
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    number TEXT NOT NULL UNIQUE,
    number_hash char(64) NOT NULL UNIQUE,
    last4 CHAR(4) NOT NULL,
    expiration_date DATE NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'active' CHECK (status IN ('active', 'blocked', 'expired'))
);

--changeset re1kur:2
CREATE TABLE IF NOT EXISTS card_information
(
    card_id UUID PRIMARY KEY,
    brand VARCHAR(32) NOT NULL,
    issue_date DATE NOT NULL DEFAULT CURRENT_DATE,
    FOREIGN KEY (card_id) REFERENCES cards(id)
);

--changeset re1kur:3
CREATE TABLE IF NOT EXISTS card_balances
(
    card_id UUID PRIMARY KEY,
    value NUMERIC(12, 2) NOT NULL DEFAULT 0,
    blocked BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (card_id) REFERENCES cards(id)
);

--changeset re1kur:4
CREATE TABLE IF NOT EXISTS transactions
(
    id UUID PRIMARY KEY,
    sender_card_id UUID NOT NULL,
    receiver_card_id UUID NOT NULL,
    amount NUMERIC(12, 2) NOT NULL CHECK (amount > 0),
    status VARCHAR(16) NOT NULL DEFAULT 'processing' CHECK (status IN ('processing', 'failed', 'completed')),
    issue_timestamp TIMESTAMP NOT NULL DEFAULT now(),
    processed_timestamp TIMESTAMP,
    FOREIGN KEY (sender_card_id) REFERENCES cards(id),
    FOREIGN KEY (receiver_card_id) REFERENCES cards(id)
);

--changeset re1kur:5
CREATE INDEX IF NOT EXISTS card_status_idx ON cards(status);

--changeset re1kur:6
CREATE INDEX IF NOT EXISTS card_last4_idx on cards(last4);

--changeset re1kur:7
CREATE INDEX IF NOT EXISTS card_user_id_idx on cards(user_id);