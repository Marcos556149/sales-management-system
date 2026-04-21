-- V7__update_sale_trigger_delete_empty_sales.sql
-- ============================================================
-- FUNCTION: fn_update_sale_total
-- ============================================================
-- Recalculates and updates the total_amount of a sale whenever
-- a related sale_detail row is inserted, updated, or deleted.
--
-- If no sale_detail rows remain for the affected sale,
-- the sale record is automatically deleted.
--
-- Uses NEW and OLD records to determine the affected sale:
-- - NEW is available on INSERT and UPDATE
-- - OLD is available on DELETE and UPDATE
-- ============================================================

CREATE OR REPLACE FUNCTION core.fn_update_sale_total()
RETURNS TRIGGER AS
$$
DECLARE
    v_sale_id BIGINT;
BEGIN
    v_sale_id := COALESCE(NEW.sale_id, OLD.sale_id);

    IF EXISTS (
        SELECT 1
        FROM core.sale_detail
        WHERE sale_id = v_sale_id
    ) THEN
        UPDATE core.sale
        SET total_amount = (
            SELECT SUM(sale_price * product_quantity)
            FROM core.sale_detail
            WHERE sale_id = v_sale_id
        )
        WHERE sale_id = v_sale_id;
    ELSE
        DELETE FROM core.sale
        WHERE sale_id = v_sale_id;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;