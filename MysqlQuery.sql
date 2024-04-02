CREATE DATABASE point;
use point;


CREATE TABLE products (
    id INT PRIMARY KEY,
    name VARCHAR(255),
    price DECIMAL(10, 2)
);

INSERT INTO products VALUES(102, "rice", 150);

SELECT * FROM products;




CREATE TABLE RecentBills (
    id INT AUTO_INCREMENT PRIMARY KEY,
    bill_identifier VARCHAR(255),  -- Adding the bill identifier column
    product_id INT,
    quantity INT,
    price DECIMAL(10, 2),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

SELECT * FROM RecentBills;

DROP TABLE RecentBills;