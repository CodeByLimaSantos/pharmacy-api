CREATE TABLE tb_sale_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    price_at_sale DECIMAL(38,2) NOT NULL,

    quantity INT NOT NULL,

    product_id BIGINT NOT NULL,

    sale_id BIGINT NOT NULL,

    CONSTRAINT fk_sale_items_product
        FOREIGN KEY (product_id)
        REFERENCES tb_products(id),

    CONSTRAINT fk_sale_items_sale
        FOREIGN KEY (sale_id)
        REFERENCES tb_sales(id)
        ON DELETE CASCADE
);