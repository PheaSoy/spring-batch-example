DROP TABLE payment_order IF EXISTS;

CREATE TABLE payment_order  (
    payment_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    order_id VARCHAR(20),
    status INT,
    price DECIMAL(20)
);