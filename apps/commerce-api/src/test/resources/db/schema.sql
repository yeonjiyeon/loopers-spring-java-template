DROP TABLE IF EXISTS brand;
DROP TABLE IF EXISTS product;

CREATE TABLE brand (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500) NOT NULL,
    created_at DATETIME,
    updated_at DATETIME
);

CREATE TABLE product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    brand_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500) NOT NULL,
    price BIGINT NOT NULL,
    stock INT NOT NULL,
    like_count INT NOT NULL DEFAULT 0,
    version BIGINT DEFAULT 0,
    created_at DATETIME,
    updated_at DATETIME
);
