--liquibase formatted sql

--changeset re1kur:1
CREATE TABLE IF NOT EXISTS users
(
    id UUID PRIMARY KEY,
    username VARCHAR(256) NOT NULL UNIQUE,
    password VARCHAR(72) NOT NULL
)

--changeset re1kur:2
CREATE TABLE IF NOT EXISTS roles
(
    id SMALLSERIAL PRIMARY KEY,
    name VARCHAR(32) NOT NULL UNIQUE
);

--changeset re1kur:3
CREATE TABLE IF NOT EXISTS user_roles
(
    user_id UUID,
    role_id SMALLINT,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);