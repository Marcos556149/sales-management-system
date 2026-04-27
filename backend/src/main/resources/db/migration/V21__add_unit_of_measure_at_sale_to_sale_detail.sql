-- V21__add_unit_of_measure_at_sale_to_sale_detail.sql
-- Description: Add unit of measure snapshot to sale detail table

-- Adds a new column to store the unit of measure used at the time of sale.
-- This ensures historical accuracy of sale records even if the product
-- unit of measure changes in the future.

-- ============================================================
-- PURPOSE
-- ============================================================
-- Sale details previously depended on product.unit_of_measure,
-- which could change after a sale was registered.
--
-- This introduces a snapshot field to preserve the original unit
-- of measure used during the transaction.

-- ============================================================
-- COLUMN ADDITION
-- ============================================================

ALTER TABLE core.sale_detail
ADD COLUMN unit_of_measure_at_sale VARCHAR(30) NOT NULL;

-- ============================================================
-- CONSTRAINTS
-- ============================================================

ALTER TABLE core.sale_detail
ADD CONSTRAINT chk_sale_detail_unit_of_measure_at_sale
CHECK (
    unit_of_measure_at_sale IN ('UNITS', 'KILOGRAMS', 'LITERS')
);

-- ============================================================
-- BUSINESS RULE CONTEXT
-- ============================================================
-- The unit of measure stored here must represent the value at the
-- moment the sale is registered and must not change afterwards.