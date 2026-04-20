-- V12__add_minimum_stock_to_product.sql
-- Description: Add minimum stock threshold to product table

-- Adds a minimum stock field to define the minimum allowed inventory level
-- Used for alerts and stock control validation at business logic level

-- ============================================================
-- ALTER TABLE: PRODUCT
-- ============================================================

ALTER TABLE core.product
ADD COLUMN minimum_stock NUMERIC(12,2) NOT NULL;

-- ============================================================
-- CONSTRAINTS
-- ============================================================

-- Ensures minimum stock cannot be negative
ALTER TABLE core.product
ADD CONSTRAINT chk_product_minimum_stock
CHECK (minimum_stock >= 0);