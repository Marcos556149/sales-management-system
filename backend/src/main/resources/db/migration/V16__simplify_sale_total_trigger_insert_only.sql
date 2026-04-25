-- V16__simplify_sale_total_trigger_insert_only.sql
-- ============================================================
-- PURPOSE
-- ============================================================
-- Simplifies the sale total recalculation trigger logic to
-- reflect the current business rules of the system.
--
-- In this application, sale_detail records are only created
-- during sale registration and are not expected to be updated
-- or deleted through normal operations.
--
-- Therefore:
-- - The trigger now executes only AFTER INSERT
-- - The function now uses only NEW.sale_id
-- - Automatic deletion of empty sales is removed
--
-- This results in cleaner, more maintainable, and domain-aligned
-- database behavior.
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
-- Uses only NEW.sale_id because INSERT is the only supported
-- trigger event.
-- ============================================================

CREATE OR REPLACE FUNCTION core.fn_update_sale_total()
RETURNS TRIGGER AS
$$
BEGIN
    UPDATE core.sale
    SET total_amount = (
        SELECT SUM(sale_price * product_quantity)
        FROM core.sale_detail
        WHERE sale_id = NEW.sale_id
    )
    WHERE sale_id = NEW.sale_id;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;


-- ============================================================
-- TRIGGER: trg_sale_detail_update_total
-- ============================================================
-- Replaces the previous trigger definition.
--
-- Automatically executes fn_update_sale_total after each new
-- sale_detail row is inserted.
--
-- FOR EACH ROW ensures recalculation occurs for every inserted
-- detail row belonging to a sale.
-- ============================================================

DROP TRIGGER IF EXISTS trg_sale_detail_update_total
ON core.sale_detail;

CREATE TRIGGER trg_sale_detail_update_total
AFTER INSERT
ON core.sale_detail
FOR EACH ROW
EXECUTE FUNCTION core.fn_update_sale_total();