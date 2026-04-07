-- V3__create_core_tables.sql
-- Description: Create core tables for sales management system

-- Defines main business tables including products, sales and related entities
-- Applies business rules through constraints (PK, FK, CHECK) and default values to ensure data consistency

-- ============================================================
-- DATABASE NAMING CONVENTIONS
-- ============================================================
-- This project follows a consistent naming convention for database objects
-- to improve readability, maintainability, and debugging.

-- Constraints:
-- - Primary Key: pk_<table>
-- - Foreign Key: fk_<table>_<referenced_table>
-- - Unique:      uq_<table>_<columns>
-- - Check:       chk_<table>_<column>[_rule]

-- Notes:
-- - Avoid redundant naming (e.g., do not repeat table name in column if unnecessary)
-- - For multi-column constraints, column order in name matches definition
-- - Constraint names should be descriptive but concise
-- - The [_rule] part in CHECK constraints is optional and should be used

-- Examples:
-- - pk_product
-- - fk_sale_user
-- - uq_sale_detail_sale_id_product_code
-- - chk_product_price

-- ============================================================
-- TABLE DEFINITIONS
-- ============================================================

-- Product table: stores product catalog and stock information
CREATE TABLE core.product(
    product_code VARCHAR(100) NOT NULL,
    product_name VARCHAR(100) NOT NULL,
    product_price NUMERIC(12,2) NOT NULL,
    product_stock NUMERIC(12,2) NOT NULL,
    unit_of_measure VARCHAR(30) NOT NULL,
    product_status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',

    CONSTRAINT pk_product
        PRIMARY KEY(product_code),

    CONSTRAINT chk_product_price
        CHECK (product_price >= 0),

    CONSTRAINT chk_product_stock
        CHECK (product_stock >= 0),

    CONSTRAINT chk_product_stock_integer_if_unit
        CHECK (
            unit_of_measure <> 'UNITS'
            OR product_stock = FLOOR(product_stock)
        )
	CONSTRAINT chk_product_status
        CHECK (product_status IN ('ACTIVE', 'INACTIVE'))
);

-- User table: stores system users and roles
CREATE TABLE core.user (
    user_id BIGINT DEFAULT nextval('core.user_seq'),
    user_name VARCHAR(100) NOT NULL UNIQUE,
    user_role VARCHAR(20) NOT NULL,
    user_password VARCHAR(255) NOT NULL,
	language VARCHAR(20) NOT NULL DEFAULT 'EN',
    CONSTRAINT pk_user PRIMARY KEY(user_id),
    CONSTRAINT chk_user_role
		CHECK (user_role IN ('ADMIN', 'OPERATOR')),
	CONSTRAINT chk_user_language
        CHECK (language IN ('EN', 'ES'))
);

-- Sale table: stores sales transactions
CREATE TABLE core.sale(
	sale_id BIGINT DEFAULT nextval('core.sale_seq'),
	sale_date DATE NOT NULL DEFAULT CURRENT_DATE,
	sale_time TIME NOT NULL DEFAULT CURRENT_TIME,
	total_amount NUMERIC(12,2) NOT NULL,
	user_id BIGINT,
	CONSTRAINT pk_sale
		PRIMARY KEY(sale_id),
	CONSTRAINT fk_sale_user
		FOREIGN KEY(user_id)
		REFERENCES core.user(user_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT,
	CONSTRAINT chk_sale_total_amount
		CHECK (total_amount >= 0)
);

-- Sale detail table: represents line items within a sale
CREATE TABLE core.sale_detail(
	sale_detail_id BIGINT DEFAULT nextval('core.sale_detail_seq'),
	sale_price NUMERIC(12,2) NOT NULL,
	product_quantity NUMERIC(12,2) NOT NULL,
	sale_id BIGINT NOT NULL,
	product_code varchar(100) NOT NULL,
	CONSTRAINT pk_sale_detail
		PRIMARY KEY(sale_detail_id),
	CONSTRAINT fk_sale_detail_sale
		FOREIGN KEY(sale_id)
		REFERENCES core.sale(sale_id)
		ON UPDATE CASCADE
		ON DELETE CASCADE,
	CONSTRAINT fk_sale_detail_product
		FOREIGN KEY(product_code)
		REFERENCES core.product(product_code)
		ON UPDATE CASCADE
		ON DELETE RESTRICT,
	CONSTRAINT chk_sale_detail_product_quantity
		CHECK (product_quantity > 0),
	CONSTRAINT chk_sale_detail_sale_price
		CHECK (sale_price >= 0),
	CONSTRAINT uq_sale_detail_sale_id_product_code 
		UNIQUE(sale_id, product_code)
);

-- System configuration table: stores global configuration settings
CREATE TABLE core.system_configuration(
    system_configuration_id BIGINT NOT NULL,
    business_name VARCHAR(100) NOT NULL,
    CONSTRAINT pk_system_configuration
        PRIMARY KEY(system_configuration_id)
);