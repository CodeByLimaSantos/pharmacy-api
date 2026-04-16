CREATE TABLE tb_inventory_lot (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    entry_date DATE NOT NULL,

    expiration_date DATE NOT NULL,

    lot_number VARCHAR(255) NOT NULL,

    quantity INT NOT NULL,

    product_id BIGINT NOT NULL,

    CONSTRAINT fk_inventory_lot_product
        FOREIGN KEY (product_id)
        REFERENCES tb_product(id)
        ON DELETE CASCADE
);
