# PrimeSale - System Requirements

Web-based sales and inventory management system for retail businesses

## Index

### System Overview
- [1. General Description](#1-general-description)
- [2. System Objective](#2-system-objective)

### Core Functional Areas
- [3. Product Management](#3-product-management)
- [4. Sales Management](#4-sales-management)
- [5. Sale Detail](#5-sale-detail)
- [6. Barcode Scanning](#6-barcode-scanning)
- [7. Sales Receipt Generation](#7-sales-receipt-generation)

### User and Access Management
- [8. Users](#8-users)
  - [User Management](#user-management)
- [9. System Access](#9-system-access)

### System Configuration
- [10. System Configuration](#10-system-configuration)
  - [Global Configuration](#global-configuration)
  - [Functionalities](#functionalities)

### Additional Functionalities
- [11. Sales Statistics](#11-sales-statistics)
  - [11.1 Sales Information](#111-sales-information)
  - [11.2 Product Information](#112-product-information)
    - [11.2.1 Sold Products](#1121-sold-products)
    - [11.2.2 Unsold Products](#1122-unsold-products)
  - [11.3 Report Generation](#113-report-generation)
- [12. Logout](#12-logout)

---

## 1. General Description

PrimeSale is a web-based business management system designed to help retail businesses manage their daily commercial operations.

The system provides tools for product administration, inventory control, sales registration, receipt generation, user management and access control, configuration settings, and business reporting and analytics through an intuitive and efficient interface.

## 2. System Objective

The objective of PrimeSale is to allow a business to:

- Manage and maintain product information  
- Control available inventory and stock levels  
- Register and track completed sales transactions  
- Generate detailed receipts for each transaction  
- Manage system users and access permissions  
- Obtain reports and statistical insights about sales and products  
- Centralize daily commercial operations in an efficient web-based platform

## 3. Product Management

The system must allow managing the products available in the business, including their registration, maintenance, availability control, and consultation.

For each product, the following data must be recorded:

- Product name  
- Product code (unique identifier within the system)  
- Product price  
- Unit of measure (e.g., unit, kilogram, liter)  
- Available stock (according to the product’s unit of measure)
- Minimum stock level used to identify low stock products (according to the product’s unit of measure)
- Product status (active/inactive)

The system must allow:

- Register a new product  
- View the list of existing products  
- View detailed information of a specific product  
- Update product information  
- Logically deactivate a product (mark it as inactive)  
- Reactivate a product (mark it as active)  

## 4. Sales Management
The system must allow managing sales transactions made in the business, including their registration and consultation.

For each sale, the following data must be stored:
- Unique sale identifier  
- Sale date  
- Sale time  
- Total amount  
- User who performed the sale (must be an active user at the time of the transaction)

The system must allow:
- Register a new sale  
- View the list of existing sales
- View detailed information of a specific sale

## 5. Sale Detail
For each product included in a sale, the following data must be stored:
- Unique sale detail identifier  
- Associated sale  
- Sold product  
- Product name at the time of the sale  
- Price at the time of the sale  
- Quantity sold (according to the product’s unit of measure at the time of the sale)
- Unit of measure at the time of the sale
- Line subtotal  

The product name, sale price, and unit of measure stored in the sale detail must preserve the values used during the transaction, even if the corresponding product information is modified later.

The system must allow:
- Add a product to a sale while the sale is being registered
- Remove a product from a sale while the sale is being registered

## 6. Barcode Scanning
The system must allow barcode scanning using a barcode reader.

This functionality will allow:
- Identifying existing products in the system through the scanned code  
- Facilitating the registration of new products using the barcode  
- Speeding up product selection in sales processes through barcode scanning  

## 7. Sales Receipt Generation

For each recorded sale, the system must be able to generate a purchase receipt that includes:

- Business name
- Business address
- Receipt issue date
- Receipt issue time
- Unique sale identifier
- List of sold products, including the product name as recorded at the time of the sale
- Quantity of each product along with the unit of measure recorded at the time of the sale
- Price of each product at the time of the sale
- Subtotal for each product
- Total sale amount

The receipt must represent proof of the completed transaction.

## 8. Users
The system must have two predefined access types (roles):

**Administrator**
- Has full access to all system functionalities  
- Can access the system through authentication
- Can manage products, including registration, updates, activation, and deactivation  
- Can register and search products by barcode  
- Can view product lists, product details, and stock information
- Can register new sales
- Can view sales lists and detailed sale information, including associated sale details
- Can add and remove products from a sale while the sale is being registered
- Can generate sales receipts for recorded sales
- Can view sales statistics  
- Can update business information, including the business name and address
- Can register, view, and update users (operators)
- Can change user status (active, suspended, deleted)
- Can log out from the system

**Operator (cashier)**
- Can access the system through authentication
- Can search products by barcode
- Can view product lists, product details, and stock information
- Can register new sales
- Can add and remove products from a sale while the sale is being registered
- Can generate sales receipts for recorded sales
- Can view sales lists and detailed sale information
- Can log out from the system
- Cannot register products  
- Cannot update product information  
- Cannot deactivate or reactivate products  
- Cannot register products by barcode
- Cannot modify business information, including the business name and address
- Cannot register, view, and update users (operators)
- Cannot change user status (active, suspended, deleted)

The system must store information about each user to manage access and permissions. Each user will have the following data:
- User ID: unique internal identifier  
- Username: unique name used to log into the system  
- Role: user type that determines system permissions  
- Password: password associated with the user account, used for authentication  
- Status: indicates whether the user is active, suspended, or logically deleted

User status can be:
- Active: the user can access and operate in the system
- Suspended: the user cannot access the system temporarily
- Deleted: the user is logically removed and cannot access the system, but remains stored for historical data integrity

**Note:** This information is stored in the database to authenticate users and control access to system functionalities.

The system must validate the user's status during authentication:

- Only users with "Active" status can access the system  
- Users with "Suspended" or "Deleted" status must be denied access

### User Management

The system must allow administrators to manage system users.

The system must allow:
- Register a new user (operator role only)
- View existing users
- Update user data
- Change user status (active, suspended, deleted)

Only users with the Administrator role can perform these actions.

**Initial Setup:**
The system must include a predefined administrator account created during system initialization. This account will be used to manage users within the system.

## 9. System Access
The system must require authentication to allow access to its functionalities.

To access the system, the user must enter:
- Username (unique identifier assigned to the user)  
- Corresponding password  

## 10. System Configuration

The system must provide a user interface for managing global configuration settings.

### Global Configuration

The system must allow storing and updating the following global configuration data:

- Business name
- Business address

This configuration is shared across all users of the system.

### Functionalities

The system must allow:

- Viewing current global configuration settings  
- Updating business information, including the business name and address (administrator only)


## 11. Sales Statistics
The system must provide a statistics section that allows users to analyze sales and product performance through aggregated data, key indicators, and visual representations.

The system must allow the user to obtain statistical information based on selected filters:
- User (all users or a specific user)  
- Date range (start date and end date)  

### 11.1 Sales Information

For the selected filters, the system must display:

- Total revenue from sales  
- Total number of sales  
- Average ticket value (total revenue divided by number of sales)  
- Hour with the highest revenue  
- Hour with the highest number of sales  

The system must display time-based charts showing:

- Total revenue over time  
- Number of sales over time  

The charts must adapt their time granularity (e.g., hour, day, month, year) according to the selected date range.

### 11.2 Product Information

The system must provide insights into product performance based on the selected filters.

#### 11.2.1 Sold Products

For products with sales activity, the system must provide:

- Top 10 products based on quantity sold  
- Top 10 products based on revenue generated  

These values must be presented as visual charts in the main statistics view.

- A product ranking list that allows:
  - Selecting the metric:
    - Quantity sold  
    - Revenue generated  
  - Selecting the order:
    - Highest to lowest (most sold → least sold)  
    - Lowest to highest (least sold → most sold)  

The system must allow accessing a detailed view of the ranking list, where:

- All sold products matching the selected filters are displayed 
- Pagination is applied for navigation between results

#### 11.2.2 Unsold Products

For products with no sales, the system must provide:

- A complete list of unsold products  

The system must allow accessing a detailed view where:

- All unsold products matching the selected filters are displayed
- Pagination is applied for navigation between results

### 11.3 Report Generation

The system must allow generating a report in PDF format based on the selected filters.

The report must always include:

- Report title
- Selected user  
- Selected date range  
- Report generation date and time

The system must allow the user to select which sections to include in the report.

Each section must be included in its entirety, without partial selection.

The available sections are:

- Sales information
- Product information

Each selected section must include all corresponding information defined in the statistics section.

For product-related sections, the system must allow the user to select the number of products to include in the report.

The available options must be:

- 10 products  
- 20 products  
- 50 products  
- 100 products 

For the product ranking list included in the report, the system must allow selecting:

- Quantity sold or revenue generated as ranking metric
- Highest to lowest or lowest to highest ordering

The system must generate the report as a downloadable PDF file.

## 12. Logout

The system must allow the user to log out from the current session.

The system must allow:
- Terminating the user's session upon confirmation  
- Redirecting the user to the login screen