-- V5__add_indexes.sql
-- Description: Add indexes to improve query performance for core tables

-- This migration creates indexes based on the most frequent query patterns
-- defined in the functional requirements (product search, sales filtering, and joins).

-- ============================================================
-- PRODUCT INDEXES
-- ============================================================

-- Index to optimize product search and sorting by name
-- Used in queries that filter or order products by product_name
CREATE INDEX idx_product_name 
ON core.product(product_name);

-- Index to optimize filtering by product status
-- Used when filtering products by their availability
CREATE INDEX idx_product_status 
ON core.product(product_status);

-- ============================================================
-- SALE INDEXES
-- ============================================================

-- Index to optimize joins between sale and user tables
-- Improves performance when retrieving sales along with user information
CREATE INDEX idx_sale_user_id 
ON core.sale(user_id);

-- Composite index to optimize filtering and sorting of sales
-- Matches the query pattern: WHERE sale_date = ? ORDER BY sale_time
CREATE INDEX idx_sale_date_time 
ON core.sale(sale_date, sale_time);

-- ============================================================
-- SALE DETAIL INDEXES
-- ============================================================

-- Index to optimize queries by sale_id
-- Critical for trigger performance (core.trg_sale_detail_update_total) when recalculating total_amount
CREATE INDEX idx_sale_detail_sale_id 
ON core.sale_detail(sale_id);

-- Index to optimize queries by product_code
-- Useful for statistics and aggregations by product
CREATE INDEX idx_sale_detail_product_code 
ON core.sale_detail(product_code);