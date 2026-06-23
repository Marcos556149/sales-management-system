-- Elimina todos los registros de la tabla de ventas (sale)
DELETE FROM core.sale;

-- Elimina todos los registros de la tabla de productos
DELETE FROM core.product;

-- Elimina todos los registros de la tabla de usuarios
DELETE FROM core.user;

-- Reinicia la secuencia de IDs de ventas para que vuelva a empezar en 1
ALTER SEQUENCE core.sale_seq RESTART WITH 1;

-- Reinicia la secuencia de IDs de detalles de ventas para que vuelva a empezar en 1
ALTER SEQUENCE core.sale_detail_seq RESTART WITH 1;

-- Reinicia la secuencia de IDs de usuarios para que vuelva a empezar en 1
ALTER SEQUENCE core.user_seq RESTART WITH 1;

-- Restablece la configuración predeterminada del negocio.
UPDATE core.system_configuration
SET
    business_name = 'My Business',
    business_address = 'Business Address'
WHERE system_configuration_id = 1;