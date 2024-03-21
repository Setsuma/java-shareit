INSERT INTO users (id, name, email)
VALUES      (1, 'owner', 'owner@yandex.ru');

INSERT INTO users (id, name, email)
VALUES      (2, 'requester', 'requester@yandex.ru');

INSERT INTO requests (id, description, requester_id, created_date)
VALUES (1, 'I need hummer', 2, LOCALTIMESTAMP);

INSERT INTO requests (id, description, requester_id, created_date)
VALUES (2, 'I need drill', 2, LOCALTIMESTAMP);

INSERT INTO items (id, name, description, is_available, owner_id, request_id)
VALUES      (1, 'item1 hammer', 'description1', true, 1, 1);

INSERT INTO items (id, name, description, is_available, owner_id, request_id)
VALUES      (2, 'item2', 'best HammeR', true, 1, 1);

INSERT INTO items (id, name, description, is_available, owner_id, request_id)
VALUES      (3, 'item2', 'drill', true, 1, 2);