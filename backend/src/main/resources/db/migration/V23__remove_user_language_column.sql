-- V23__remove_user_language_column.sql
-- Description: Remove language attribute from user table

-- This migration removes the language column from the core.user table,
-- as the system no longer supports multi-language configuration.
-- It also removes the associated CHECK constraint to maintain schema consistency.

-- ============================================================
-- DATABASE NAMING CONVENTIONS
-- ============================================================
-- This project follows a consistent naming convention for database objects
-- to improve readability, maintainability, and debugging.

-- Constraints:
-- - Primary Key: pk_<table>
-- - Foreign Key: fk_<table>_<referenced_table>
-- - Unique:      uq_<table>_<columns>
-- - Check:       chk_<table>_<column>[_rule]

-- ============================================================
-- MIGRATION STEPS
-- ============================================================

-- Step 1: Drop CHECK constraint related to language
ALTER TABLE core.user
DROP CONSTRAINT IF EXISTS chk_user_language;

-- Step 2: Remove language column
ALTER TABLE core.user
DROP COLUMN IF EXISTS language;