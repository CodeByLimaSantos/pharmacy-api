-- Add new optional customer fields while keeping migration idempotent for existing databases.
SET @email_exists := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'tb_customers'
      AND COLUMN_NAME = 'email'
);
SET @email_sql := IF(
    @email_exists = 0,
    'ALTER TABLE tb_customers ADD COLUMN email VARCHAR(255) NULL',
    'SELECT 1'
);
PREPARE stmt_email FROM @email_sql;
EXECUTE stmt_email;
DEALLOCATE PREPARE stmt_email;

SET @phone_exists := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'tb_customers'
      AND COLUMN_NAME = 'phone'
);
SET @phone_sql := IF(
    @phone_exists = 0,
    'ALTER TABLE tb_customers ADD COLUMN phone VARCHAR(20) NULL',
    'SELECT 1'
);
PREPARE stmt_phone FROM @phone_sql;
EXECUTE stmt_phone;
DEALLOCATE PREPARE stmt_phone;

SET @address_exists := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'tb_customers'
      AND COLUMN_NAME = 'address'
);
SET @address_sql := IF(
    @address_exists = 0,
    'ALTER TABLE tb_customers ADD COLUMN address VARCHAR(255) NULL',
    'SELECT 1'
);
PREPARE stmt_address FROM @address_sql;
EXECUTE stmt_address;
DEALLOCATE PREPARE stmt_address;

