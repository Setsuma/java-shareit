INSERT INTO users (id, name, email)
VALUES      (1, 'owner', 'owner@yandex.ru');

INSERT INTO users (id, name, email)
VALUES      (2, 'requester', 'requester@yandex.ru');

INSERT INTO requests (id, description, requester_id, created_date)
VALUES (1, 'I need hummer', 1, LOCALTIMESTAMP);

INSERT INTO requests (id, description, requester_id, created_date)
VALUES (2, 'I need drill', 1, LOCALTIMESTAMP);

INSERT INTO requests (id, description, requester_id, created_date)
VALUES (3, 'I need pen', 2, LOCALTIMESTAMP);