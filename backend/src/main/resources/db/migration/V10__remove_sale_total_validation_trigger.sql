-- ============================================================
-- ROLLBACK: Sale total_amount validation trigger
-- ============================================================
-- V10__remove_sale_total_validation_trigger.sql
-- Removes the trigger and function added in V8/V9 that blocked
-- manual total_amount changes on core.sale.
--
-- Sale total_amount control will remain handled by backend logic
-- and the existing sale_detail recalculation trigger.
-- ============================================================

DROP TRIGGER IF EXISTS trg_validate_sale_total_amount
ON core.sale;

DROP FUNCTION IF EXISTS core.fn_validate_sale_total_amount();