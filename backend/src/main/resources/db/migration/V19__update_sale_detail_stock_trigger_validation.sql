-- V19__update_sale_detail_stock_trigger_validation.sql
-- ============================================================
-- PURPOSE
-- ============================================================
-- Enhances the stock control trigger on core.sale_detail to
-- align database-level validation with application business rules.
--
-- This ensures consistent stock validation behavior between
-- Java service layer and PostgreSQL triggers.
--
-- The trigger validates available stock before inserting a
-- sale_detail and prevents overselling at database level.
-- ============================================================


-- ============================================================
-- FUNCTION: fn_manage_product_stock
-- ============================================================
-- Controls stock changes caused by INSERT operations on
-- core.sale_detail.
--
-- Behavior:
-- - Locks the related product row (FOR UPDATE)
-- - Validates available stock before deduction
-- - Decreases stock immediately if validation passes
--
-- Raises exception when available stock is insufficient,
-- using the same message format as the Java service layer.
-- ============================================================

CREATE OR REPLACE FUNCTION core.fn_manage_product_stock()
RETURNS TRIGGER AS
$$
DECLARE
    v_current_stock NUMERIC(12,2);
    v_product_label TEXT;
BEGIN

    SELECT product_stock,
           product_code || ' - ' || product_name
    INTO v_current_stock,
         v_product_label
    FROM core.product
    WHERE product_code = NEW.product_code
    FOR UPDATE;

    -- Validate available stock
    IF v_current_stock < NEW.product_quantity THEN
        RAISE EXCEPTION 'Insufficient stock for product %', v_product_label;
    END IF;

    -- Decrease stock
    UPDATE core.product
    SET product_stock = product_stock - NEW.product_quantity
    WHERE product_code = NEW.product_code;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


-- ============================================================
-- TRIGGER: trg_sale_detail_manage_stock
-- ============================================================
-- Executes fn_manage_product_stock before each INSERT on
-- core.sale_detail.
--
-- Ensures stock validation and deduction happen atomically
-- before persisting sale details.
-- ============================================================

DROP TRIGGER IF EXISTS trg_sale_detail_manage_stock
ON core.sale_detail;

CREATE TRIGGER trg_sale_detail_manage_stock
BEFORE INSERT
ON core.sale_detail
FOR EACH ROW
EXECUTE FUNCTION core.fn_manage_product_stock();