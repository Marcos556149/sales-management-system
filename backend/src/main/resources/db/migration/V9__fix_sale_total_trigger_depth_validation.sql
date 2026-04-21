-- V9__fix_sale_total_trigger_depth_validation.sql
-- ============================================================
-- FUNCTION: fn_validate_sale_total_amount
-- ============================================================
-- Enforces total_amount consistency in core.sale:
-- - INSERT: total_amount must start at 0.00
-- - UPDATE: manual changes to total_amount are not allowed
-- - Trigger-based updates are allowed
--
-- Fixes trigger depth detection so direct updates are blocked
-- while nested trigger updates remain allowed.
-- ============================================================

CREATE OR REPLACE FUNCTION core.fn_validate_sale_total_amount()
RETURNS TRIGGER AS
$$
BEGIN
    IF TG_OP = 'INSERT' THEN
        IF NEW.total_amount <> 0 THEN
            RAISE EXCEPTION
                'Sale total_amount must be 0.00 on insert';
        END IF;
    END IF;

    IF TG_OP = 'UPDATE' THEN
        IF NEW.total_amount <> OLD.total_amount
           AND pg_trigger_depth() = 1 THEN
            RAISE EXCEPTION
                'Manual update of sale total_amount is not allowed';
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;