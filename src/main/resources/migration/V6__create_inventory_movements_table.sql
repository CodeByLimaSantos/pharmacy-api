CREATE TABLE tb_inventory_movements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    movement_date DATETIME(6) NOT NULL,

    movement_type ENUM(
        'ADJUSTMENT_IN',
        'ADJUSTMENT_OUT',
        'DISPOSAL',
        'ENTRY',
        'SALE_EXIT'
    ) NOT NULL,

    quantity INT NOT NULL,

    reason VARCHAR(255),

    inventory_lot_id BIGINT NOT NULL,

    CONSTRAINT fk_inventory_movements_lot
        FOREIGN KEY (inventory_lot_id)
        REFERENCES tb_inventory_lots(id)
        ON DELETE CASCADE
);