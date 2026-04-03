-- V6__insert_initial_system_configuration.sql
-- Description: Inserts the initial global configuration row in system_configuration table

INSERT INTO core.system_configuration (system_configuration_id, business_name)
VALUES (1, 'My Business');