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

--changeset re1kur:4
INSERT INTO cards (id, user_id, number, number_hash, last4, expiration_date, status)
VALUES
    (gen_random_uuid(), (SELECT id FROM users WHERE username = 'admin'),
     '4111111111111111', 'hash1111', '1111', '2027-12-31', 'active'),
     (gen_random_uuid(), (SELECT id FROM users WHERE username = 'admin'),
  '4111111111111112', md5('4111111111111112'), '1112', '2027-12-31', 'active'),
    (gen_random_uuid(), (SELECT id FROM users WHERE username = 'admin'),
     '5500000000000004', 'hash0004', '0004', '2026-05-31', 'blocked'),
    (gen_random_uuid(), (SELECT id FROM users WHERE username = 'admin'),
     '340000000000009', 'hash0009', '0009', '2023-01-31', 'expired')
     ;

--changeset re1kur:5
INSERT INTO card_information (card_id, brand, issue_date)
VALUES
    ((SELECT id FROM cards WHERE last4 = '1111'), 'VISA', '2023-01-01'),
    ((SELECT id FROM cards WHERE last4 = '1112'), 'VISA', '2023-01-02'),
    ((SELECT id FROM cards WHERE last4 = '0004'), 'MASTERCARD', '2022-06-01'),
    ((SELECT id FROM cards WHERE last4 = '0009'), 'AMEX', '2020-01-01');

--changeset re1kur:6
INSERT INTO card_balances (card_id, value, blocked)
VALUES
    ((SELECT id FROM cards WHERE last4 = '1111'), 1500.50, FALSE),
    ((SELECT id FROM cards WHERE last4 = '1112'), 120.50, FALSE),
    ((SELECT id FROM cards WHERE last4 = '0004'), 0, TRUE),
    ((SELECT id FROM cards WHERE last4 = '0009'), 320.75, FALSE);
