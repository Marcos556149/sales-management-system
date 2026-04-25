-- V15__add_business_address_to_system_configuration.sql
-- Description: Adds business_address column to system_configuration and initializes the default value

ALTER TABLE core.system_configuration
ADD COLUMN business_address VARCHAR(200);

UPDATE core.system_configuration
SET business_address = 'Business Address'
WHERE system_configuration_id = 1;

ALTER TABLE core.system_configuration
ALTER COLUMN business_address SET NOT NULL;