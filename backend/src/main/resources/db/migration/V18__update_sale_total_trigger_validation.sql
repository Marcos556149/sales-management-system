-- V18__update_sale_total_trigger_validation.sql
-- ============================================================
-- PURPOSE
-- ============================================================
-- Enhances the sale total recalculation trigger to enforce
-- a strict business rule on maximum allowed sale amount.
--
-- This ensures that no sale can exceed the database-defined
-- monetary limit (NUMERIC(12,2) => 9999999999.99).
--
-- The validation is performed at database level to guarantee
-- consistency regardless of application logic.
-- ============================================================


-- ============================================================
-- FUNCTION: fn_update_sale_total
-- ============================================================
-- Recalculates and updates the total_amount of a sale after a
-- new related sale_detail row is inserted.
--
-- The total is computed as:
-- SUM(sale_price * product_quantity)
--
-- Before persisting the value into core.sale.total_amount,
-- the result is validated against the maximum allowed amount.
--
-- If the limit is exceeded, the function raises an exception
-- and the transaction is aborted.
-- ============================================================

CREATE OR REPLACE FUNCTION core.fn_update_sale_total()
RETURNS TRIGGER AS
$$
DECLARE
    v_total_amount NUMERIC(12,2);
    v_max_amount CONSTANT NUMERIC(12,2) := 9999999999.99;
BEGIN

    SELECT SUM(sale_price * product_quantity)
    INTO v_total_amount
    FROM core.sale_detail
    WHERE sale_id = NEW.sale_id;

    -- Validate maximum allowed sale amount
    IF v_total_amount > v_max_amount THEN
        RAISE EXCEPTION 'Sale total exceeds maximum allowed amount (9999999999.99)';
    END IF;

    UPDATE core.sale
    SET total_amount = v_total_amount
    WHERE sale_id = NEW.sale_id;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;


-- ============================================================
-- TRIGGER: trg_sale_detail_update_total
-- ============================================================
-- Executes fn_update_sale_total after each INSERT on
-- core.sale_detail.
--
-- Ensures that the total_amount of the sale is always
-- recalculated and validated immediately after each new
-- sale detail is added.
-- ============================================================

DROP TRIGGER IF EXISTS trg_sale_detail_update_total
ON core.sale_detail;

CREATE TRIGGER trg_sale_detail_update_total
AFTER INSERT
ON core.sale_detail
FOR EACH ROW
EXECUTE FUNCTION core.fn_update_sale_total();