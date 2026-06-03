# Modelo Lógico de Base de Datos

## Índice

### General
- [Descripción](#descripción)
- [Normalización](#normalización)

### Entidades
- [product](#product)
- [user](#user)
- [sale](#sale)
- [sale_detail](#sale_detail)
- [system_configuration](#system_configuration)

---

## Descripción

Este documento describe la estructura lógica de la base de datos del sistema de gestión de ventas, incluyendo tablas, claves primarias (PK) y claves foráneas (FK).

---

## Entidades

### product

Representa los productos disponibles en el sistema.

- product_code (PK)
- product_name
- product_price
- product_status
- product_stock
- unit_of_measure
- minimum_stock

---

### user

Representa los usuarios del sistema.

- user_id (PK)
- user_name
- user_role
- user_password
- user_status

---

### sale

Representa las transacciones de venta.

- sale_id (PK)
- sale_date
- sale_time
- total_amount
- user_id (FK → user.user_id)

---

### sale_detail

Representa los detalles de cada venta, vinculando productos con ventas.

- sale_detail_id (PK)
- sale_price
- product_name_at_sale
- product_quantity
- unit_of_measure_at_sale
- sale_id (FK → sale.sale_id)
- product_code (FK → product.product_code)

sale_price representa el precio unitario del producto al momento de la venta.

---

### system_configuration

Representa la configuración general del sistema.

- system_configuration_id (PK)
- business_name
- business_address

Nota: Esta tabla se inicializa con un único registro que contiene la configuración predeterminada del sistema (system_configuration_id=1, business_name="My Business", business_address="Business Address").

---

## Normalización

El modelo de base de datos ha sido normalizado hasta la Cuarta Forma Normal (4FN).

Este diseño garantiza la integridad de los datos y previene anomalías de inserción, actualización y eliminación.