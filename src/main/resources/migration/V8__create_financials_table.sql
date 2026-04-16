CREATE TABLE tb_financials (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    amount DECIMAL(38,2) NOT NULL,

    created_at DATETIME(6) NOT NULL,

    description VARCHAR(255),

    due_date DATE,

    issue_date DATETIME(6),

    notes TEXT,

    payment_date DATE,

    payment_method ENUM(
        'BANK_TRANSFER',
        'BOLETO',
        'CASH',
        'CREDIT_CARD',
        'DEBIT_CARD',
        'PIX'
    ),

    status ENUM(
        'CANCELED',
        'OVERDUE',
        'PAID',
        'PARTIALLY_PAID',
        'PENDING'
    ) NOT NULL,

    type ENUM(
        'CONTA_A_PAGAR',
        'CONTA_A_RECEBER'
    ) NOT NULL,

    updated_at DATETIME(6),

    customer_id BIGINT,

    supplier_id BIGINT,

    CONSTRAINT fk_financials_customer
        FOREIGN KEY (customer_id)
        REFERENCES tb_customers(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_financials_supplier
        FOREIGN KEY (supplier_id)
        REFERENCES tb_suppliers(id)
        ON DELETE SET NULL
);
