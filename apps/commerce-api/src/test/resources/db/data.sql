INSERT INTO brand (name, description, created_at, updated_at) VALUES
('Nike', 'Sports Brand', NOW(), NOW()),
('Adidas', 'Sports Brand', NOW(), NOW()),
('Apple', 'Tech Brand', NOW(), NOW()),
('Samsung', 'Tech Brand', NOW(), NOW()),
('Sony', 'Electronics', NOW(), NOW()),
('LG', 'Electronics', NOW(), NOW()),
('Chanel', 'Luxury', NOW(), NOW()),
('Gucci', 'Luxury', NOW(), NOW()),
('Zara', 'Clothing', NOW(), NOW()),
('Uniqlo', 'Clothing', NOW(), NOW());

DROP PROCEDURE IF EXISTS loop_insert_products;

DELIMITER $$
CREATE PROCEDURE loop_insert_products()
BEGIN
    DECLARE i INT DEFAULT 1;

    SET autocommit = 0;

    WHILE i <= 100000 DO
        INSERT INTO product (
            brand_id,
            name,
            description,
            price_amount,
            stock,
            like_count,
            version,
            created_at,
            updated_at
        ) VALUES (
            FLOOR(1 + RAND() * 10),
            CONCAT('Product Name ', i),
            CONCAT('Description for product ', i),
            FLOOR(1000 + RAND() * 99000),
            FLOOR(RAND() * 100),
            FLOOR(RAND() * 5000),
            0,
            NOW(),
            NOW()
        );

        SET i = i + 1;

        IF i % 1000 = 0 THEN
            COMMIT;
        END IF;
    END WHILE;

    COMMIT;
    SET autocommit = 1;
END$$
DELIMITER ;

CALL loop_insert_products();