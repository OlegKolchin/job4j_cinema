CREATE TABLE IF NOT EXISTS account (
    id SERIAL PRIMARY KEY,
    username VARCHAR NOT NULL,
    email VARCHAR NOT NULL UNIQUE,
    phone VARCHAR NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS ticket (
id SERIAL PRIMARY KEY,
session_id INT NOT NULL UNiQUE,
hall_row INT NOT NULL,
cell INT NOT NULL,
account_id INT NOT NULL references account(id)
);

alter table ticket add constraint const unique(hall_row, cell)
