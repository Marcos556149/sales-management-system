-- V17__simplify_sale_detail_stock_trigger_insert_only.sql
-- ============================================================
-- PURPOSE
-- ============================================================
-- Simplifies the stock control trigger on core.sale_detail to
-- match the current business rules of the application.
--
-- sale_detail rows are only created during sale registration
-- and are not expected to be updated or deleted through normal
-- system operations.
--
-- Therefore:
-- - The trigger now executes only BEFORE INSERT
-- - The function validates available stock
-- - The function decreases stock immediately before insert
-- - UPDATE and DELETE stock handling logic is removed
--
-- The trigger is intentionally preserved because it provides
-- database-level protection and row locking for concurrent
-- sales affecting the same product.
-- ============================================================


-- ============================================================
-- FUNCTION: fn_manage_product_stock
-- ============================================================
-- Controls stock changes caused by INSERT operations on
-- core.sale_detail.
--
-- Behavior:
-- - INSERT:
--     Locks the related product row
--     Validates available stock
--     Subtracts NEW.product_quantity from stock
--
-- Raises exception when available stock is insufficient.
--
-- FOR UPDATE is used to prevent concurrent overselling when
-- multiple transactions attempt to sell the same product.
-- ============================================================

CREATE OR REPLACE FUNCTION core.fn_manage_product_stock()
RETURNS TRIGGER AS
$$
DECLARE
    v_current_stock INTEGER;
BEGIN

    SELECT product_stock
    INTO v_current_stock
    FROM core.product
    WHERE product_code = NEW.product_code
    FOR UPDATE;

    IF v_current_stock < NEW.product_quantity THEN
        RAISE EXCEPTION 'Insufficient stock for product %', NEW.product_code;
    END IF;

    UPDATE core.product
    SET product_stock = product_stock - NEW.product_quantity
    WHERE product_code = NEW.product_code;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


-- ============================================================
-- TRIGGER: trg_sale_detail_manage_stock
-- ============================================================
-- Replaces the previous trigger definition.
--
-- Executes fn_manage_product_stock before each new row is
-- inserted into core.sale_detail.
--
-- BEFORE trigger is required because validation and stock
-- deduction must occur before persisting the sale detail.
-- ============================================================

DROP TRIGGER IF EXISTS trg_sale_detail_manage_stock
ON core.sale_detail;

CREATE TRIGGER trg_sale_detail_manage_stock
BEFORE INSERT
ON core.sale_detail
FOR EACH ROW
EXECUTE FUNCTION core.fn_manage_product_stock();