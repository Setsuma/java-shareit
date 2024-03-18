INSERT INTO users (id, name, email)
VALUES      (1, 'owner', 'owner@yandex.ru');

INSERT INTO users (id, name, email)
VALUES      (2, 'booker', 'booker@yandex.ru');

INSERT INTO items (id, name, description, is_available, owner_id, request_id)
VALUES      (1, 'item1', 'description1', true, 1, null);

INSERT INTO items (id, name, description, is_available, owner_id, request_id)
VALUES      (2, 'item2', 'description2', true, 2, null);

INSERT INTO booking (id, start_date, end_date, item_id, booker_id, status)
VALUES      (1, LOCALTIMESTAMP - 3, LOCALTIMESTAMP - 2, 2, 1, 'REJECTED');

INSERT INTO booking (id, start_date, end_date, item_id, booker_id, status)
VALUES      (2, LOCALTIMESTAMP - 3, LOCALTIMESTAMP - 2, 1, 2, 'APPROVED');

INSERT INTO booking (id, start_date, end_date, item_id, booker_id, status)
VALUES      (3, LOCALTIMESTAMP - 1, LOCALTIMESTAMP + 1, 1, 2, 'APPROVED');

INSERT INTO booking (id, start_date, end_date, item_id, booker_id, status)
VALUES      (4, LOCALTIMESTAMP + 2, LOCALTIMESTAMP + 3, 1, 2, 'WAITING');

INSERT INTO booking (id, start_date, end_date, item_id, booker_id, status)
VALUES      (5, LOCALTIMESTAMP + 4, LOCALTIMESTAMP + 5, 1, 2, 'APPROVED');