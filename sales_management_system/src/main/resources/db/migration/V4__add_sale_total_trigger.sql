-- ============================================================
-- FUNCTION: fn_update_sale_total
-- ============================================================
-- Recalculates and updates the total_amount of a sale whenever
-- a related sale_detail row is inserted, updated, or deleted.
--
-- Uses NEW and OLD records to determine the affected sale:
-- - NEW is available on INSERT and UPDATE
-- - OLD is available on DELETE and UPDATE
--
-- COALESCE is used to:
-- - Select the correct sale_id (NEW or OLD depending on operation)
-- - Default total_amount to 0 when no detail rows exist
-- ============================================================

CREATE OR REPLACE FUNCTION core.fn_update_sale_total()
RETURNS TRIGGER AS
$$
BEGIN
    UPDATE core.sale
    SET total_amount = COALESCE((
        SELECT SUM(sale_price * product_quantity)
        FROM core.sale_detail
        WHERE sale_id = COALESCE(NEW.sale_id, OLD.sale_id)
    ), 0)
    WHERE sale_id = COALESCE(NEW.sale_id, OLD.sale_id);

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- ============================================================
-- TRIGGER: trg_sale_detail_update_total
-- ============================================================
-- Automatically executes the function fn_update_sale_total
-- after any INSERT, UPDATE, or DELETE operation on sale_detail.
--
-- FOR EACH ROW ensures the trigger runs once per affected row,
-- allowing access to NEW and OLD values.
-- ============================================================

CREATE TRIGGER core.trg_sale_detail_update_total
AFTER INSERT OR UPDATE OR DELETE
ON core.sale_detail
FOR EACH ROW
EXECUTE FUNCTION core.fn_update_sale_total();