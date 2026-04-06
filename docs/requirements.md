# System Requirements

Sales management system for a kiosk

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
  - [User Configuration](#user-configuration)

### Additional Functionalities
- [11. Sales Statistics](#11-sales-statistics)
- [12. Logout](#12-logout)

---

## 1. General Description
A client requests the development of a web application that allows managing products and sales of their business.  
The system must allow registering products, performing sales, and generating a receipt with the details of each transaction.

## 2. System Objective
The objective of the system is to allow a business to:
- Maintain a record of its products  
- Control available stock  
- Record completed sales  
- Obtain a detailed receipt for each transaction  

## 3. Product Management
The system must allow storing and managing information about the products available in the business.  

For each product, the following data must be recorded:
- Product name  
- Product code (unique identifier within the system)  
- Product price  
- Unit of measure (e.g., unit, kilogram, liter)  
- Available stock  
- Product status (active/inactive)  

The system must allow:
- Register a new product  
- View existing products  
- Update product data  
- Logically deactivate a product (mark it as inactive)  
- Refresh the product list displayed on screen to reflect recent changes in the system  

## 4. Sales Management
The system must allow recording sales made in the business.  

For each sale, the following data must be stored:
- Unique sale identifier  
- Sale date  
- Sale time  
- Total amount  
- User who performed the sale (must be an active user at the time of the transaction)

The system must allow:
- Register a new sale  
- View existing sales  
- Update sale data  
- Delete a sale  
- Refresh the sales list displayed on screen to reflect recent changes in the system  

## 5. Sale Detail
For each product included in a sale, the following data must be stored:
- Unique sale detail identifier  
- Associated sale  
- Sold product  
- Price at the time of the sale  
- Quantity sold (according to the product’s unit of measure)  
- Line subtotal  

The system must allow:
- Add a product to a sale  
- Remove a product from a sale  
- Update sale detail data  

## 6. Barcode Scanning
The system must allow barcode scanning using a barcode reader.

This functionality will allow:
- Identifying existing products in the system through the scanned code  
- Facilitating the registration of new products using the barcode  
- Speeding up product selection in sales processes through barcode scanning  

## 7. Sales Receipt Generation
For each recorded sale, the system must be able to generate a purchase receipt that includes:
- Business name  
- Receipt issue date  
- Receipt issue time  
- List of sold products  
- Quantity of each product  
- Unit price  
- Subtotal for each product  
- Total sale amount  

The receipt must represent proof of the completed transaction.

## 8. Users
The system must have two predefined access types (roles):

**Administrator**
- Has full access to all system functionalities  
- Can view and manage products  
- Can register, update, and deactivate products  
- Can search products by barcode  
- Can view and manage sales, including sale details  
- Can register, update, and delete sales  
- Can add and remove products from sales  
- Can generate sales receipts  
- Can view detailed product and sales information  
- Can view sales statistics  
- Can update the business name
- Can change their interface language
- Can register, view, and update users (operators)
- Can change user status (active, suspended, deleted)
- Can log out from the system

**Operator (cashier)**
- Can access the system through authentication  
- Can register sales  
- Can add products to sales  
- Can remove products from sales only during sale registration  
- Can generate sales receipts  
- Can view products  
- Can search products by barcode  
- Can view sales  
- Can view sales statistics  
- Can change their interface language
- Can log out from the system
- Cannot create, update, or deactivate products  
- Cannot register products by barcode  
- Cannot modify or delete sales or their details once registered  
- Cannot modify the business name
- Cannot register, view, and update users (operators)
- Cannot change user status (active, suspended, deleted)

The system must store information about each user to manage access and permissions. Each user will have the following data:
- User ID: unique internal identifier  
- Username: unique name used to log into the system  
- Role: user type that determines system permissions  
- Password: password associated with the user account, used for authentication  
- Preferred language: interface language selected by the user (Spanish or English)
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

The system must provide a user interface for managing configuration settings at both global and user levels.

### Global Configuration

The system must allow storing and updating the following global configuration data:

- Business name (required; used in sales receipt generation)

This configuration is shared across all users of the system.

### User Configuration

The system must allow each user to manage their own interface preferences:

- Interface language (Spanish or English)

### Functionalities

The system must allow:

- Viewing current global configuration settings  
- Updating the business name (administrator only)  
- Changing the interface language (all authenticated users)  

The selected interface language must persist for each user and be applied automatically upon login.

The interface must support multiple languages according to the configuration defined in the system.

## 11. Sales Statistics
The system must allow the user to obtain statistical information about sales within a selected time range (day, month, year).

For each selected period, the following data must be displayed:
- Total revenue from sales  
- List of sold products with their quantities  

The system must allow:
- Selecting a specific day of the year (e.g., August 15, 2026) and displaying the corresponding information  
- Selecting a specific month of a year (e.g., July 2025) and displaying the corresponding information  
- Selecting a specific year (e.g., 2024) and displaying the corresponding information  
- Refreshing the information displayed on screen to reflect recent changes in the system

## 12. Logout

The system must allow the user to log out from the current session.

The system must allow:
- Terminating the user's session upon confirmation  
- Redirecting the user to the login screen