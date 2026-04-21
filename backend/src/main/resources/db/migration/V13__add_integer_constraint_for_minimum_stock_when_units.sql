-- V13__add_integer_constraint_for_minimum_stock_when_units.sql
-- Description: Enforce integer minimum stock values for products measured in units

-- Ensures that products using UNITS as unit of measure
-- store minimum_stock values without decimal places,
-- keeping consistency with stock quantity rules.

-- ============================================================
-- CONSTRAINTS
-- ============================================================

ALTER TABLE core.product
ADD CONSTRAINT chk_product_minimum_stock_integer_if_unit
CHECK (
    unit_of_measure <> 'UNITS'
    OR minimum_stock = FLOOR(minimum_stock)
);