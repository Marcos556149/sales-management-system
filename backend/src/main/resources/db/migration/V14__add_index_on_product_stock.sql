-- V14__add_index_on_product_stock.sql
-- Description: Add index on product stock column to optimize stock-based filtering queries

-- Improves performance for queries that filter products by stock level,
-- such as out-of-stock, low-stock, and stock availability searches.

-- ============================================================
-- PRODUCT INDEXES
-- ============================================================

CREATE INDEX idx_product_stock
ON core.product(product_stock);