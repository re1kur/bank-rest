--liquibase formatted sql

--changeset re1kur:1
INSERT INTO roles(name)
VALUES ('USER'), ('ADMIN');

--changeset re1kur:2
INSERT INTO users(username, password) VALUES ('admin', '$2a$12$t/VoGJ6wpISG.F6IN8Mmzu.E.G3AOxsnmETu3mDYBvw7YNtcAmtta');

--changeset re1kur:3
INSERT INTO user_roles(user_id, role_id)
VALUES
    ((SELECT id FROM users WHERE username = 'admin'), (SELECT id FROM roles WHERE name = 'ADMIN')),
    ((SELECT id FROM users WHERE username = 'admin'), (SELECT id FROM roles WHERE name = 'USER'));