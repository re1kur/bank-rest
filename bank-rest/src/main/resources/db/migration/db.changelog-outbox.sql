--liquibase formatted sql

--changeset re1kur:1
CREATE TABLE IF NOT EXISTS outbox_events
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payload TEXT    NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    type VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'NEW'
);