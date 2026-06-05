-- V25__translate_sale_total_trigger_message_to_spanish.sql
-- ============================================================
-- PURPOSE
-- ============================================================
-- Translates sale total validation error messages raised by
-- fn_update_sale_total() to Spanish.
--
-- No business logic changes are introduced.
-- Only the exception text returned to the application
-- is modified.
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
        RAISE EXCEPTION 'El importe total de la venta supera el máximo permitido (9999999999.99)';
    END IF;

    UPDATE core.sale
    SET total_amount = v_total_amount
    WHERE sale_id = NEW.sale_id;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;