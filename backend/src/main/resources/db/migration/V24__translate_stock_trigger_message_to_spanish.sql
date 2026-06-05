-- V24__translate_stock_trigger_message_to_spanish.sql
-- ============================================================
-- PURPOSE
-- ============================================================
-- Translates stock validation error messages raised by
-- fn_manage_product_stock() to Spanish.
--
-- No business logic changes are introduced.
-- Only the exception text returned to the application
-- is modified.
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
        RAISE EXCEPTION 'Stock insuficiente para el producto %', v_product_label;
    END IF;

    -- Decrease stock
    UPDATE core.product
    SET product_stock = product_stock - NEW.product_quantity
    WHERE product_code = NEW.product_code;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;