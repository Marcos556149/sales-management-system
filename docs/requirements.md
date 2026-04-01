# System Requirements

Sales management system for a kiosk

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
- User who performed the sale  

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
- Cannot create, update, or deactivate products  
- Cannot register products by barcode  
- Cannot modify or delete sales or their details once registered  

The system must store information about each user to manage access and permissions. Each user will have the following data:
- User ID: unique internal identifier  
- Username: unique name used to log into the system  
- Role: user type that determines system permissions  
- Password: password associated with the user account, used for authentication  

**Note:** This information is stored in the database to authenticate users and control access to system functionalities.

## 9. System Access
The system must require authentication to allow access to its functionalities.

To access the system, the user must enter:
- Username (unique identifier assigned to the user)  
- Corresponding password  

## 10. User Interface

The system must provide a user interface that allows interaction with all system functionalities.

The interface must support multiple languages (Spanish and English), according to the configuration defined in the system.

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

## 12. System Configuration

The system must allow managing basic configuration settings of the business.

The system must allow storing and updating the following configuration data:

- Business name  
- Interface language (Spanish or English)  

The system must allow:
- Viewing current configuration settings  
- Updating the business name  
- Changing the interface language  

The business name is required and must be used in the sales receipt generation.  
The interface language selection must persist during the user session.  