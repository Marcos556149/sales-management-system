# Logical Database Model

## Index

### General
- [Description](#description)
- [Normalization](#normalization)

### Entities
- [product](#product)
- [user](#user)
- [sale](#sale)
- [sale_detail](#sale_detail)
- [system_configuration](#system_configuration)

---

## Description
This document describes the logical structure of the database for the sales management system, including tables, primary keys (PK), and foreign keys (FK).

---

## Entities

### product
Represents the products available in the system.

- product_code (PK)
- product_name
- product_price
- product_status
- product_stock
- unit_of_measure

---

### user
Represents the system users.

- user_id (PK)
- user_name
- user_role
- user_password
- language

---

### sale
Represents the sales transactions.

- sale_id (PK)
- sale_date
- sale_time
- total_amount
- user_id (FK → user.user_id)

---

### sale_detail
Represents the details of each sale, linking products to sales.

- sale_detail_id (PK)
- sale_price
- product_quantity
- sale_id (FK → sale.sale_id)
- product_code (FK → product.product_code)

### system_configuration
Represents the global configuration of the system.

- system_configuration_id (PK)
- business_name

Note: This table is initialized with a single row containing the system's default configuration(system_configuration_id=1, business_name="My Business").

## Normalization

The database model has been normalized up to the Fourth Normal Form (4NF).
This design ensures data integrity and prevents insertion, update, and deletion anomalies.