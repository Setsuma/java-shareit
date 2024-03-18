INSERT INTO users (id, name, email)
VALUES      (1, 'owner', 'owner@yandex.ru');

INSERT INTO items (id, name, description, is_available, owner_id, request_id)
VALUES      (1, 'item1 hammer', 'description1', true, 1, null);

INSERT INTO items (id, name, description, is_available, owner_id, request_id)
VALUES      (2, 'item2', 'best HammeR', true, 1, null);

INSERT INTO comments (id, text, item_id, author_id, created_date)
VALUES (1, 'comment 1', 1, 1, LOCALTIMESTAMP);

INSERT INTO comments (id, text, item_id, author_id, created_date)
VALUES (2, 'comment 2', 1, 1, LOCALTIMESTAMP);

INSERT INTO comments (id, text, item_id, author_id, created_date)
VALUES (3, 'comment 3', 2, 1, LOCALTIMESTAMP);