-- V22__add_product_name_at_sale_to_sale_detail.sql
-- Description: Add product name snapshot to sale detail table

-- Adds a new column to store the product name at the time of sale.
-- This ensures historical accuracy of sale records even if the product
-- name changes in the future.

-- ============================================================
-- PURPOSE
-- ============================================================
-- Sale details previously depended on product.product_name,
-- which could change after a sale was registered.
--
-- This introduces a snapshot field to preserve the original product
-- name used during the transaction.

-- ============================================================
-- COLUMN ADDITION
-- ============================================================

ALTER TABLE core.sale_detail
ADD COLUMN product_name_at_sale VARCHAR(100) NOT NULL;

-- ============================================================
-- BUSINESS RULE CONTEXT
-- ============================================================
-- The product name stored here must represent the value at the
-- moment the sale is registered and must not change afterwards.