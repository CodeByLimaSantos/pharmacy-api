CREATE TABLE tb_products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    controlled BIT(1) NOT NULL,

    description VARCHAR(255),

    name VARCHAR(255) NOT NULL,

    price_cost DECIMAL(38,2) NOT NULL,

    price_sale DECIMAL(38,2) NOT NULL,

    product_category_type ENUM(
        'DERMOCOSMETICOS',
        'GENERICOS',
        'HIGIENE_PESSOAL',
        'INFANTIL',
        'MEDICAMENTOS',
        'PERFUMARIA',
        'SAUDE_SEXUAL',
        'SIMILARES',
        'SUPLEMENTOS'
    ) NOT NULL,

    registerms VARCHAR(13),

    tarja VARCHAR(255),

    supplier_id BIGINT,

    CONSTRAINT fk_products_supplier
        FOREIGN KEY (supplier_id)
        REFERENCES tb_suppliers(id)
);