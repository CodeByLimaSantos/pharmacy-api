CREATE TABLE tb_sales (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    payment_method ENUM(
        'BANK_TRANSFER',
        'BOLETO',
        'CASH',
        'CREDIT_CARD',
        'DEBIT_CARD',
        'PIX'
    ) NOT NULL,

    sale_date DATETIME(6) NOT NULL,

    total_amount DECIMAL(38,2) NOT NULL,

    customer_id BIGINT,

    CONSTRAINT fk_sales_customer
        FOREIGN KEY (customer_id)
        REFERENCES TB_customers(id)
        ON DELETE CASCADE
);