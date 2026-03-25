# Modelo Lógico de Base de Datos

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

---

### user
Representa los usuarios del sistema.

- user_id (PK)
- user_name
- user_role
- user_password

---

### sale
Representa las ventas realizadas.

- sale_id (PK)
- sale_date
- sale_time
- total_amount
- user_id (FK → user.user_id)

---

### sale_detail
Representa el detalle de cada venta, asociando productos con ventas.

- sale_detail_id (PK)
- sale_price
- product_quantity
- sale_id (FK → sale.sale_id)
- product_code (FK → product.product_code)

## Normalización

El modelo de base de datos ha sido normalizado hasta la Cuarta Forma Normal (4FN).
Este diseño garantiza la integridad de los datos y evita anomalías de inserción, actualización y eliminación.