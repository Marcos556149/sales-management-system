-- ============================================================
-- MIGRATION: V11__add_sale_detail_stock_control_trigger.sql
-- ============================================================
-- Adds a BEFORE trigger on core.sale_detail to:
-- - Validate available stock before insert/update
-- - Automatically decrease stock on insert/update
-- - Restore stock on delete
-- ============================================================
-- FUNCTION: fn_manage_product_stock
-- ============================================================
-- Controls stock changes caused by INSERT, UPDATE, or DELETE
-- operations on core.sale_detail.
--
-- Behavior:
-- - INSERT:
--     Validates stock and subtracts NEW.product_quantity
--
-- - UPDATE:
--     Restores OLD quantity to previous product stock,
--     then validates and subtracts NEW quantity.
--     Supports quantity changes or product replacement.
--
-- - DELETE:
--     Restores OLD quantity to product stock
--
-- Raises exception when available stock is insufficient.
-- ============================================================

CREATE OR REPLACE FUNCTION core.fn_manage_product_stock()
RETURNS TRIGGER AS
$$
DECLARE
    v_current_stock INTEGER;
BEGIN

    -- ========================================================
    -- INSERT
    -- ========================================================
    IF TG_OP = 'INSERT' THEN

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
    END IF;


    -- ========================================================
    -- UPDATE
    -- ========================================================
    IF TG_OP = 'UPDATE' THEN

        -- Restore previous stock
        UPDATE core.product
        SET product_stock = product_stock + OLD.product_quantity
        WHERE product_code = OLD.product_code;

        -- Lock current/new product row
        SELECT product_stock
        INTO v_current_stock
        FROM core.product
        WHERE product_code = NEW.product_code
        FOR UPDATE;

        IF v_current_stock < NEW.product_quantity THEN
            RAISE EXCEPTION 'Insufficient stock for product %', NEW.product_code;
        END IF;

        -- Apply new discount
        UPDATE core.product
        SET product_stock = product_stock - NEW.product_quantity
        WHERE product_code = NEW.product_code;

        RETURN NEW;
    END IF;


    -- ========================================================
    -- DELETE
    -- ========================================================
    IF TG_OP = 'DELETE' THEN

        UPDATE core.product
        SET product_stock = product_stock + OLD.product_quantity
        WHERE product_code = OLD.product_code;

        RETURN OLD;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;



-- ============================================================
-- TRIGGER: trg_sale_detail_manage_stock
-- ============================================================
-- Executes fn_manage_product_stock before any INSERT, UPDATE,
-- or DELETE on core.sale_detail.
--
-- BEFORE trigger is required because validation must happen
-- before persisting the sale_detail row.
-- ============================================================

CREATE TRIGGER trg_sale_detail_manage_stock
BEFORE INSERT OR UPDATE OR DELETE
ON core.sale_detail
FOR EACH ROW
EXECUTE FUNCTION core.fn_manage_product_stock();