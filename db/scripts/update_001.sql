CREATE TABLE IF NOT EXISTS account (
    id SERIAL PRIMARY KEY,
    username VARCHAR NOT NULL,
    email VARCHAR NOT NULL UNIQUE,
    phone VARCHAR NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS ticket (
id SERIAL PRIMARY KEY,
session_id INT NOT NULL,
ticket_row INT NOT NULL,
cell INT NOT NULL,
account_id INT NOT NULL,
foreign key(account_id) references account(id)
);