-- V20__update_sale_total_trigger_validation_fix_precision.sql
-- ============================================================
-- PURPOSE
-- ============================================================
-- Fixes precision handling in the sale total recalculation trigger
-- to prevent numeric overflow errors during intermediate calculations.
--
-- The previous implementation used NUMERIC(12,2) for the intermediate
-- variable, which could cause runtime overflow when PostgreSQL attempted
-- to cast values exceeding the allowed precision.
--
-- This version introduces a higher precision internal variable to ensure
-- safe computation before applying business constraints and persistence.
--
-- The business rule remains unchanged:
-- - A sale cannot exceed 9999999999.99
-- - Validation is enforced at database level
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
-- The calculation is performed using a higher precision numeric
-- type to avoid overflow during aggregation.
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
    v_total_amount NUMERIC(20,2);
    v_max_amount CONSTANT NUMERIC(12,2) := 9999999999.99;
BEGIN

    SELECT SUM(sale_price * product_quantity)
    INTO v_total_amount
    FROM core.sale_detail
    WHERE sale_id = NEW.sale_id;

    -- Normalize precision before validation
    v_total_amount := ROUND(v_total_amount, 2);

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