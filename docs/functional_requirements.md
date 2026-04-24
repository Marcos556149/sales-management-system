## Index

### Functional Requirements
- [RF-1: Register Product](#rf-1-register-product)
- [RF-2: View Products](#rf-2-view-products)
- [RF-3: Update Product](#rf-3-update-product)
- [RF-4: Deactivate Product (Soft Delete)](#rf-4-deactivate-product-soft-delete)
- [RF-5: Search Product by Barcode](#rf-5-search-product-by-barcode)
- [RF-6: Register Product by Barcode](#rf-6-register-product-by-barcode)
- [RF-7: Register Sale](#rf-7-register-sale)
- [RF-8: View Sales](#rf-8-view-sales)
- [RF-9: Add Product to Sale via Barcode](#rf-9-add-product-to-sale-via-barcode)
- [RF-10: Generate Sale Ticket](#rf-10-generate-sale-ticket)
- [RF-11: User Authentication](#rf-11-user-authentication)
- [RF-12: View Product](#rf-12-view-product)
- [RF-13: View Sale](#rf-13-view-sale)
- [RF-14: Change System Configuration](#rf-14-change-system-configuration)
- [RF-15: View Sales Statistics](#rf-15-view-sales-statistics)
- [RF-16: Logout](#rf-16-logout)
- [RF-17: Register User](#rf-17-register-user)
- [RF-18: View Users](#rf-18-view-users)
- [RF-19: View User](#rf-19-view-user)
- [RF-20: Update User](#rf-20-update-user)
- [RF-21: Change User Status](#rf-21-change-user-status)
- [RF-22: Activate Product](#rf-22-activate-product)


### General Rules
- [System Access Rules](#system-access-rules)
  - [User Types](#user-types)
- [Numeric and Decimal Data](#numeric-and-decimal-data)
- [Date and Time Formats](#date-and-time-formats)

## RF-1: Register Product

### Description
The system must allow the user to register a new product.

### Main Flow
1. The user accesses the product section.  
2. The user requests to register a new product.  
3. The system requests the following product data: code, name, price, unit of measure, available stock, and minimum stock level. 
4. The user enters the required data.  
5. The system validates the entered information.  
6. The system saves the product in the database.  
7. The system confirms the successful registration with a confirmation message: "Product successfully registered".  

### Alternative Flows

**5.a Invalid data**  
5.a.1 The system displays an error message indicating the incorrect fields.  
5.a.2 The user corrects the data.  

**5.b Product already exists in the database**  
5.b.1 The system detects that a product with the same code already exists.  
5.b.2 The system displays an error message: "Product with code '{productCode}' already exists".  

### Business Rules
- The product code is mandatory and must be unique within the system.  
- The product name is mandatory.  
- The product status can only take the values "Active" or "Inactive".
- The product status is not required as input and is automatically set to "Active" when the product is created.
- The product price is mandatory and must be a real number greater than or equal to 0.  
- The available stock is mandatory and must be a real number greater than or equal to 0.  
- The minimum stock level is mandatory and must be a real number greater than or equal to 0.
- The price, available stock, and minimum stock level fields must be initialized with a default value of 0 in the registration form.
- The product name should be descriptive and clearly distinguishable from other similar products in the system.
- The unit of measure must be one of the following values: "Units", "Kilograms", or "Liters".
- The unit of measure is mandatory and defaults to "Units" in the registration form.
- If the unit of measure is "Units", both available stock and minimum stock level must be expressed in integer values.

---

## RF-2: View Products

### Description
The system must allow the user to view the products registered in the system.

### Main Flow
1. The user accesses the product section.  
2. The user requests to view products.  
3. The system retrieves a paginated list of products from the database.
4. The system displays the registered products with the following data:
   - Product code  
   - Product name  
   - Price  
   - Status  
   - Available stock (displayed with its unit of measure, e.g., "2.5 kg", "3 u")  

### Alternative Flows

**3.a No registered products**  
3.a.1 The system detects that there are no products in the database.  
3.a.2 The system displays a message: "No products available".  

**3.b Product search**  

**3.b.1 By name**  
3.b.1.1 The user enters a name or part of the product name.  
3.b.1.2 The system filters products that match the entered name.  

**3.b.2 By code**  
3.b.2.1 The user enters a code or part of the product code.  
3.b.2.2 The system filters products that match the entered code.  

**3.c Product sorting**  

**3.c.1 By name**  
3.c.1.1 The user selects a sorting criterion by name ("Ascending" or "Descending").  
3.c.1.2 The system sorts the products according to the selected criterion.  

**3.d Product filtering**  

**3.d.1 By status**  
3.d.1.1 The user selects a product status ("Active", "Inactive", or "All Status").  
3.d.1.2 The system filters products according to the selected status.  

**3.d.2 By stock level**  
3.d.2.1 The user selects a stock filter ("All Stock Levels", "Normal Stock", "Low Stock", or "Out of Stock").  
3.d.2.2 The system filters products according to the selected stock condition.

**3.f Product pagination**
3.f.1 The system allows navigation between pages of products (e.g., next page, previous page, or direct page selection).

**3.g Products not found**  
3.g.1 The system detects that no products match the search criteria.  
3.g.2 The system displays a message: "No products match the search criteria".  

### Business Rules
- The system must allow viewing all registered products using pagination.
- The system must retrieve products in pages of 50 items by default.
- The system must allow navigation between pages of products (e.g., next page, previous page, or direct page selection).
- If no page is specified, the system must return the first page by default.
- The system must allow searching products by name or code.  
- The system must allow sorting products by name ("Ascending" or "Descending").  
- The system must allow filtering products by status ("Active", "Inactive", or "All Status").  
- If no status filter is selected, the system displays "All Status" by default.  
- If no sorting criterion is selected, the system applies "Ascending" by default.  
- The system must ensure that only the products belonging to the requested page are retrieved from the database (server-side pagination).
- The system must visually indicate the stock condition of each product based on available stock and minimum stock level.
- A product is considered "Normal Stock" when available stock is greater than minimum stock level.
- A product is considered "Low Stock" when available stock is greater than 0 and less than or equal to minimum stock level.
- A product is considered "Out of Stock" when available stock is equal to 0.
- Products with Low Stock or Out of Stock condition must be visually highlighted in the product list.
- The system must allow filtering products by stock condition ("All Stock Levels", "Normal Stock", "Low Stock", or "Out of Stock").
- If no stock filter is selected, the system displays "All Stock Levels" by default.

---

## RF-3: Update Product

### Description
The system must allow the user to update the data of an existing product.

### Main Flow
1. The user accesses the product section.  
2. The user selects the update option for an existing product.  
3. The system displays the current selected product data in editable form.
4. The user modifies the following product data: Name, price, unit of measure, available stock, and minimum stock level.  
5. The system validates the entered information.  
6. The system updates the product data in the database.  
7. The system displays a confirmation message: "Product successfully updated".  

### Alternative Flows

**2.a Product not found**  
2.a.1 The system detects that the selected product does not exist.  
2.a.2 The system displays a message: "Product with code '{productCode}' not found".  

**5.a Invalid data**  
5.a.1 The system displays an error message indicating the incorrect fields.  
5.a.2 The user corrects the data.  

### Business Rules
- All editable fields (name, price, unit of measure, available stock, and minimum stock level) are mandatory.
- The product price must be a real number greater than or equal to 0.
- The available stock must be a real number greater than or equal to 0.
- The minimum stock level must be a real number greater than or equal to 0.
- The product status cannot be modified in this process.  
- The product code cannot be modified in this process
- The product name should be descriptive and clearly distinguishable from other similar products in the system.
- The unit of measure must be one of the following values: "Units", "Kilograms", or "Liters".
- If the unit of measure is "Units", both available stock and minimum stock level must be expressed in integer values.
- If the unit of measure is modified, the system must validate that available stock and minimum stock level comply with the new unit constraints.

---

## RF-4: Deactivate Product (Soft Delete)

### Description
The system must allow the user to logically deactivate a product by marking it as inactive.

### Main Flow

1. The user accesses the product section.  
2. The user selects the deactivate option for an active product.
3. The system requests confirmation of the action.  
4. The user confirms the operation.  
5. The system updates the product status to "Inactive".  
6. The system displays a confirmation message: "Product successfully deactivated".

### Alternative Flows

**2.a Product not found**  
2.a.1 The system detects that the selected product does not exist.  
2.a.2 The system displays a message: "Product not found".

**3.a Operation canceled**  
3.a.1 The user cancels the operation.  
3.a.2 The system does not apply any changes to the product.

**5.a Product already inactive**  
5.a.1 The system detects that the product is already inactive.  
5.a.2 The system displays a message: "Product is already inactive".

### Business Rules

- Product deactivation is logical, not physical.  
- The product status can only take the values "Active" or "Inactive".  
- An inactive product must not be available for sales operations.  
- Product historical data must remain stored after deactivation.

---

## RF-5: Search Product by Barcode

### Description
The system must allow the user to identify an existing product by scanning its barcode.

### Main Flow
1. The user accesses the product section.  
2. The user scans the product barcode using a reader.  
3. The system retrieves the product associated with the scanned barcode.  
4. The system displays the product data using the View Product operation (RF-12).

### Alternative Flows

**2.a Unreadable or unrecognized barcode**  
2.a.1 The system detects that the barcode cannot be read.  
2.a.2 The system displays a message: "Barcode not recognized, please try again".  

**3.a Product not found**  
3.a.1 The system detects that no product exists with the scanned code.  
3.a.2 The system offers the user the option to register a new product using the scanned barcode (see RF-6: Register Product by Barcode).  

### Business Rules
- The barcode must be unique within the system.  
- The scanned barcode must correspond to an existing product in the database.

---

## RF-6: Register Product by Barcode

### Description
The system allows the user to initiate product registration using a scanned barcode as an identifier. This functionality complements RF-1: Register Product.

### Main Flow

1. The user accesses the product section.  
2. The user scans the product barcode using a reader.  
3. The system opens the product registration process (RF-1) with the scanned barcode prefilled as the product code.

### Business Rules
- Full product registration follows the rules defined in RF-1: Register Product.    

---

## RF-7: Register Sale

### Description
The system must allow the user to register sales made in the business, storing the general sale data and managing the products included in the sale during the registration process.

### Main Flow
1. The user accesses the sales section.  
2. The user requests to register a new sale.  
3. The system creates a sale in progress.  
4. The system displays an empty sale detail and the product selection interface. 
5. The system displays the product selection interface, which includes:
   - Product search by name or code  
   - Product list with pagination  
   - Refresh product list option   
6. The user searches and selects a product from the available product list.
7. The system displays the selected product information:
   - Product code  
   - Product name  
   - Unit price  
   - Available stock (displayed with its unit of measure, e.g., "2.5 kg", "3 u")   
8. The system assigns a default quantity of 1 and allows the user to modify it.  
9. The user confirms the product addition.  
10. The system validates the quantity and product availability.  
11. The system records the product in the sale.  
12. If the product is already included in the sale, the system increases its quantity instead of creating a duplicate line.  
13. The system displays the updated sale in progress.  
14. The user may repeat the product addition process as many times as needed.  
15. The user may remove any product previously added to the sale.  
16. The user confirms the sale.  
17. The system automatically records the current date and time of the sale.  
18. The system registers the sale with the following data:
   - Unique sale identifier (automatically generated by the system)  
   - Sale date  
   - Sale time  
   - Total amount  
   - User who performed the sale  
19. The system stores the associated sale details with the following data:
   - Unique sale detail identifier (automatically generated by the system)  
   - Associated product  
   - Associated sale  
   - Quantity sold  
   - Unit price at the time of the sale  
   - Subtotal (calculated as quantity × unit price, not stored) 
20. The system displays a confirmation message: "Sale successfully registered. Do you want to print the receipt?"  
21. The user confirms receipt printing, invoking the Generate Receipt operation (RF-10).  
22. The system returns to the sales section.

### Alternative Flows

**3.a Product Not Found**  
3.a.1 The system detects that the product does not exist.  
3.a.2 The system displays a message: "Product with code '{productCode}' not found".

**10.a Invalid quantity**  
10.a.1 The system detects that the entered quantity is less than or equal to 0, greater than available stock, or incompatible with the product's unit of measure.  
10.a.2 The system displays an error message indicating the required correction.

**10.b Inactive product**  
10.b.1 The system detects that the selected product has inactive status.  
10.b.2 The system displays a message: "Product '{productCode}' is inactive and cannot be added to the sale".

**16.a Sale canceled**  
16.a.1 The user decides to cancel the sale before confirmation.  
16.a.2 The system discards the sale in progress and returns to the sales section.

**16.b Sale without products**  
16.b.1 The system detects that the sale has no associated products.  
16.b.2 The system displays a message: "The sale must contain at least one product".

**21.a Receipt printing canceled**  
21.a.1 The user cancels receipt printing.  
21.a.2 The system completes the sale process and returns to the sales section without generating the receipt.

### Business Rules
- A sale must contain at least one associated product to be registered.
- Products can only be added or removed while the sale is in progress.
- Products with status "Inactive" cannot be added to a sale.
- The quantity must be greater than 0 and compatible with the product's unit of measure.
- If the unit of measure is "Units", the quantity must not contain decimals.
- A quantity greater than available stock cannot be assigned.
- If a product is already included in the sale, its quantity must be increased instead of duplicating the line.
- If all products are removed during the registration process, the sale in progress must remain available until confirmed or canceled.
- The system must update line subtotals and the total sale amount whenever products are added, removed, or their quantities are modified.
- Each sale detail must have an automatically generated unique identifier assigned by the system.
- The unique sale identifier is automatically assigned by the system upon confirmation.
- Each sale must be associated with the authenticated user who registered it.
- Product stock must be updated after the sale is confirmed, according to the final quantities of the sale.
- Receipt generation is performed using the operation defined in RF-10. 
- The product selection interface must allow searching products by name or product code.
- The product selection interface must support pagination of 10 items per page.
- The system must allow navigation between product pages (next and previous).
- The system must allow refreshing the product list to retrieve updated data.
- The product selection interface must only display active products with available stock greater than 0.
- The system must exclude inactive products and products with zero stock from the product selection list.
- The product selection interface must visually indicate products with low stock to assist the user during the sale process.

---

## RF-8: View Sales

### Description
The system must allow the user to view sales registered in the system, displaying general sale data and their details.

### Main Flow
1. The user accesses the sales section.  
2. The user requests to view sales.  
3. The system retrieves a paginated list of sales from the database.  
4. The system displays the registered sales with the following data:
   - Unique sale identifier  
   - Sale date and time  
   - Seller username (user_name)  
   - Total amount  

### Alternative Flows

**3.a No registered sales**  
3.a.1 The system detects that there are no sales in the database.  
3.a.2 The system displays a message: "No sales found".  

**3.b Sale search**

**3.b.1 By sale code**  
3.b.1.1 The user enters a sale code or part of the sale code.  
3.b.1.2 The system filters sales that match the entered code.

**3.c Sale pagination**  
3.c.1 The system allows navigation between pages of sales (e.g., next page, previous page, or direct page selection).  

**3.d Sale sorting**

**3.d.1 By time**  
3.d.1.1 The user selects to sort sales by time ("Most recent first" or "Oldest first").  
3.d.1.2 The system sorts the sales according to the selected criterion.

**3.e Sale filtering**

**3.e.1 By date**  
3.e.1.1 The user selects a specific date.  
3.e.1.2 The system filters sales according to the selected date.

**3.f Sales not found**  
3.f.1 The system detects that no sales match the applied criteria.  
3.f.2 The system displays a message: "No sales match the search criteria". 

### Business Rules
- The system must allow viewing all registered sales using pagination.
- The system must retrieve sales in pages of 50 items by default.
- The system must allow navigation between pages of sales (e.g., next page, previous page, or direct page selection).
- If no page is specified, the system must return the first page by default.
- The system must allow searching sales by sale code.
- The system must allow viewing sales filtered by a specific date (day, month, and year).
- The system must allow sorting sales by time ("Most recent first" or "Oldest first").
- If no date filter is selected, the system uses the current date (current day, month, and year) by default.
- If no sorting criterion by time is selected, the system sorts sales by time using "Most recent first" by default.
- The system must ensure that only the sales belonging to the requested page are retrieved from the database (server-side pagination).

---

## RF-9: Add Product to Sale via Barcode

### Description
The system must allow products to be added to the current sale using barcode scanning, acting as a shortcut for product selection during the sale registration process.

### Main Flow

1. The user accesses the sales section.  
2. The user scans a product barcode using a barcode reader.  
3. The system identifies the product associated with the scanned barcode.  
4. The system adds the product to the current sale using the same rules defined in RF-7 for product addition during sale registration.

### Alternative Flows

**3.a Product not found**  
3.a.1 The system cannot find a product associated with the scanned barcode.  
3.a.2 The system displays a message: "Product with code '{productCode}' not found".  

**4.a No sale in progress**  
4.a.1 The system detects that no sale is currently in progress.  
4.a.2 The system automatically creates a sale in progress.  
4.a.3 The system proceeds with product addition.

### Business Rules

- A barcode must uniquely identify a product in the system.  
- The product must exist, be active, and have available stock greater than 0 to be added to a sale.
- If no sale is in progress, the system must create one in "in progress" state.  
- Product addition must follow the rules defined in RF-7.  
- Stock is not updated during sale registration, only upon sale confirmation.  

---

## RF-10: Generate Sale Ticket

### Description
The system must allow generating a purchase ticket for each registered sale, representing the proof of the transaction and including the relevant information of the sale and its associated products.

### Main Flow
1. The user accesses the sales section.  
2. The user selects a registered sale.  
3. The user requests to print the sale ticket.   
4. The system retrieves the general sale data.  
5. The system retrieves the associated sale details.  
6. The system generates the sale ticket with the following information:  
   - Business name  
   - Business address
   - Ticket issue date  
   - Ticket issue time  
   - Name of each sold product  
   - Quantity of each product along with its unit of measure (e.g., "2.5 kg", "1 u")  
   - Unit price  
   - Subtotal of each product  
   - Total sale amount  
7. The system generates and prints the sale ticket using the configured output printer.

### Alternative Flows

**2.a Sale Not Found**  
2.a.1 The system detects that the sale does not exist.  
2.a.2 The system displays a message: "Sale with ID '{saleId}' not found".

**7.a Ticket Generation Error**  
7.a.1 The system detects an error while generating or printing the ticket.  
7.a.2 The system displays a message: "The ticket could not be generated".

### Business Rules
- The ticket can only be generated for previously registered sales.  
- The ticket information must accurately reflect the sale and its details.  
- The subtotal of each sale detail is automatically calculated by the system.  
- The ticket must include all products associated with the sale.  
- The ticket constitutes proof of the completed transaction.

---

## RF-11: User Authentication

### Description
The system must allow users to access its functionalities through an authentication process based on a unique username and password associated with a registered user account.

### Main Flow
1. The user accesses the login screen.  
2. The user enters their unique username.  
3. The user enters their password.  
4. The system validates that the entered credentials are correct.  
5. The system validates that the user status is "Active". 
6. The system determines the user role (`Administrator` or `Operator`) and grants access to functionalities according to the associated permissions.

### Alternative Flows

**4.a Invalid Credentials**  
4.a.1 The system detects that the username does not exist or that the password is incorrect.  
4.a.2 The system displays a message: "Invalid credentials".

**5.b Inactive User**  
5.b.1 The system detects that the user status is "Suspended" or "Deleted".  
5.b.2 The system denies access.  
5.b.3 The system displays an error message: "User account is not active".

### Business Rules
- Access to the system requires prior authentication.  
- The entered password must match the provided username.  
- The system automatically determines the user role from the user record and restricts access to functionalities accordingly.
- Only users with "Active" status can access the system.  
- Users with "Suspended" or "Deleted" status must be denied access.
- The system must apply the user's preferred language configuration upon successful authentication.

---

## RF-12: View Product

### Description
The system must allow the user to view detailed information of a specific product registered in the system.

### Main Flow

1. The user accesses the product section.  
2. The user requests to view a specific product.  
3. The system retrieves the product using its product code.  
4. The system displays the product information with the following data:
   - Product code  
   - Product name  
   - Price  
   - Unit of measure  
   - Status  
   - Available stock  
   - Minimum stock level  

### Alternative Flows

**3.a Product Not Found**  
3.a.1 The system detects that the product does not exist.  
3.a.2 The system displays a message: "Product with code '{productCode}' not found".

### Business Rules

- The system must allow querying a specific product using its product code.  
- The product information displayed must reflect the latest data stored in the database. 
- Products with "Low Stock" or "Out of Stock" condition must be visually highlighted in the product detail view. 

---

## RF-13: View Sale

### Description
The system must allow the user to view detailed information of a specific sale registered in the system.

### Main Flow
1. The user accesses the sales section.  
2. The user selects a specific sale.  
3. The system receives the sale identifier.  
4. The system retrieves the sale data from the database.  
5. The system displays the sale information with the following data:
   - Unique sale identifier  
   - Sale date  
   - Sale time  
   - Seller username (user_name)  
   - Total sale amount  
   - Details of each sold product: code, name, quantity along with its unit of measure, price at the time of sale, and subtotal  

### Alternative Flows

**3.a Sale Not Found**  
3.a.1 The system detects that the sale does not exist.  
3.a.2 The system displays a message: "Sale with ID '{saleId}' not found".

### Business Rules
- The system must allow querying a specific sale using its identifier.  
- The displayed information must accurately reflect the stored data of the sale and its details.  
- The quantity of each product must be displayed together with its unit of measure to correctly represent the sale.

---

## RF-14: Change System Configuration

### Description
The system must allow managing configuration settings at both global and user levels, including the business name, business address, and the interface language.

### Main Flow
1. The user accesses the system configuration section.  
2. The system displays the current configuration values:
   - Business name  
   - Business address  
   - Interface language  
3. The user modifies configuration values depending on their role:
   - Interface language (all users)  
   - Business name and business address (administrator only)  
4. The system validates the entered data.  
5. The system applies the changes:
   - The business name and business address are updated globally (if modified by an administrator)  
   - The interface language is updated for the current user   
6. The system confirms the changes by updating the interface and displayed business name.

### Alternate Flow
**4.a Invalid data**  
4.a.1 The system displays an error message indicating the incorrect fields.  
4.a.2 The user corrects the data.  

### Business Rules
- The business name is mandatory.
- The business name is shared by all users.
- The business address is mandatory.  
- The business address is shared by all users.
- The system must initialize the business name with a default value ("My Business").
- The system must initialize the business address with a default value ("Business Address").
- Only administrators can modify the business name and business address.
- The interface language is specific to each user.
- The interface language is mandatory for each user.
- The selected language must persist for each user and be applied automatically on login.
- The language change must not affect the data stored in the system.  
- All interface elements (menus, buttons, messages, notifications) must be displayed in the selected language.
- The system must assign a default interface language (English) when a user is created.

---

## RF-15: View Sales Statistics

### Description
The system must allow the user to view sales statistics within a selected time range, showing the total revenue and the list of products sold during that period.

### Main Flow
1. The user accesses the sales statistics section.  
2. The user selects the period to query:  
   - Specific day of the year (e.g., August 15, 2026)  
   - Specific month of a year (e.g., July 2025)  
   - Specific year (e.g., 2024)  
3. The system retrieves the statistical data from the database.  
4. The system displays:  
   - Total revenue for the selected period  
   - List of products (code and name) sold with their corresponding quantities  

### Alternative Flows

**2.a No Sales in Selected Period**  
2.a.1 The system detects that there are no sales in the database for the selected period.  
2.a.2 The system displays a message: "No sales found for the selected period".

**2.b Product Sorting**

**2.b.1 By Quantity**  
2.b.1.1 The user selects to sort sold products by quantity:  
- Most sold → least sold  
- Least sold → most sold 
2.b.1.2 The system sorts the product list according to the selected criteria.

**2.c Product Filters**

**2.c.1 Product Sales**  
2.c.1.1 The user selects "Sold products", "Unsold products", or "All".  
2.c.1.2 The system filters the products according to the selected criteria.

**2.d No Products Found**  
2.d.1 The system detects that no products match the applied criteria.  
2.d.2 The system displays a message: "No products found".

### Business Rules
- The system must allow viewing sales statistics by day, month, or year.  
- The displayed information must include total revenue and the list of sold products with their quantities.  
- The system must allow sorting products by quantity: "Most sold → least sold" or "Least sold → most sold".  
- The system must allow filtering products by "Sold products", "Unsold products", or "All".  
   - "Sold products" displays only products with a quantity sold greater than 0.  
   - "Unsold products" displays only products with a quantity sold equal to 0.  
- If the user does not select a product sales filter, the system uses "Sold products" by default.  
- If the user does not select a sorting criterion, the system sorts products by default using "Most sold → least sold".

---

## RF-16: Logout

### Description
The system must allow the user to log out from the account they are currently using.

### Main Flow
1. The user clicks the "Logout" button.  
2. The system displays a confirmation message: "Are you sure you want to log out? Any unsaved data will be lost."  
3. If the user confirms, the system terminates the user's session.  
4. The system redirects the user to the login screen.

### Business Rules
- The system must ensure that the session is fully terminated.

---

## RF-17: Register User

### Description
The system must allow administrators to register new users with the Operator role.

### Main Flow
1. The administrator accesses the user management section.  
2. The administrator selects the option to register a new user.  
3. The administrator enters the required user data:
   - Username  
   - Password  
4. The system validates that the entered data is correct.  
5. The system verifies that the username is unique.  
6. The system assigns the role "Operator" to the new user.  
7. The system assigns the status "Active" to the new user.  
8. The system assigns the default language "EN" to the new user.  
9. The system assigns a unique identifier (User ID) to the new user.  
10. The system stores the user in the database.  
11. The system confirms that the user has been successfully registered.

### Alternative Flows

**5.a Username already exists**  
5.a.1 The system detects that the username is already registered.  
5.a.2 The system displays an error message: "Username already exists".

**4.a Invalid input data**  
4.a.1 The system detects invalid or incomplete input data.  
4.a.2 The system displays an error message indicating invalid input.

### Business Rules
- Only users with the Administrator role can perform this action.  
- The username must be unique within the system.  
- The system must assign the "Operator" role by default.  
- The system must assign the "Active" status by default.  
- The system must assign "EN" as the default language.
- The system assigns a unique identifier to the user upon creation.

---

## RF-18: View Users

### Description
The system must allow administrators to view the list of registered users.

### Main Flow
1. The administrator accesses the user management section.  
2. The system retrieves the list of users from the database.  
3. The system displays the list of users with their main information:
   - Username  
   - Role  
   - Status  

### Alternative Flows

**2.a No users found**  
2.a.1 The system detects that there are no registered users.  
2.a.2 The system displays a message: "No users found".

### Business Rules
- Only users with the Administrator role can perform this action.  
- The system must display all users regardless of their status.

---

## RF-19: View User

### Description
The system must allow administrators to view detailed information of a specific user.

### Main Flow
1. The administrator accesses the user management section.  
2. The administrator selects a specific user from the list.  
3. The system retrieves the user information from the database.  
4. The system displays the user details, including:
   - Username  
   - Role  
   - Status  

### Alternative Flows

**3.a User not found**  
3.a.1 The system detects that the selected user does not exist.  
3.a.2 The system displays an error message: "User not found".

### Business Rules
- Only users with the Administrator role can perform this action.  
- The system must not display the user's password.  
- The system must display the user information regardless of their status.

---

## RF-20: Update User

### Description
The system must allow administrators to update user information.

### Main Flow
1. The administrator accesses the user management section.  
2. The administrator selects a user to update.  
3. The system displays the current user data, excluding the password.  
4. The administrator modifies the desired fields:
   - Username (optional)  
   - New password (optional)  
5. The system validates the updated data.  
6. The system verifies that the new username (if modified) is unique.  
7. The system updates the user information in the database.  
8. The system confirms that the user has been successfully updated.

### Alternative Flows

**5.a Invalid input data**  
5.a.1 The system detects invalid or incomplete input data.  
5.a.2 The system displays an error message indicating invalid input.

**6.a Username already exists**  
6.a.1 The system detects that the new username is already in use.  
6.a.2 The system displays an error message: "Username already exists".

### Business Rules
- Only users with the Administrator role can perform this action.  
- The username must remain unique within the system.  
- The system must not display the current user password.  
- If a new password is provided, it must replace the existing password.

---

## RF-21: Change User Status

### Description
The system must allow administrators to change the status of a user.

### Main Flow
1. The administrator accesses the user management section.  
2. The administrator selects a user.  
3. The system displays the current user status.  
4. The administrator selects a new status:
   - Active  
   - Suspended  
   - Deleted  
5. The system validates the selected status.  
6. The system updates the user status in the database.  
7. The system confirms that the user status has been successfully updated.

### Alternative Flows

**5.a Invalid status selection**  
5.a.1 The system detects an invalid status value.  
5.a.2 The system displays an error message: "Invalid user status".

### Business Rules
- Only users with the Administrator role can perform this action.  
- The system must allow only the following status values: Active, Suspended, Deleted.  
- Users with "Deleted" status must not be physically removed from the database.  
- Users with "Suspended" or "Deleted" status must not be able to access the system.

---

## RF-22: Activate Product

### Description
The system must allow the user to activate an inactive product by marking it as active.

### Main Flow

1. The user accesses the product section.  
2. The user selects the activate option for an inactive product.  
3. The system requests confirmation of the action.  
4. The user confirms the operation.  
5. The system updates the product status to "Active".  
6. The system displays a confirmation message: "Product successfully activated".

### Alternative Flows

**2.a Product not found**  
2.a.1 The system detects that the selected product does not exist.  
2.a.2 The system displays a message: "Product not found".

**3.a Operation canceled**  
3.a.1 The user cancels the operation.  
3.a.2 The system does not apply any changes to the product.

**5.a Product already active**  
5.a.1 The system detects that the product is already active.  
5.a.2 The system displays a message: "Product is already active".

### Business Rules

- Only inactive products can be activated.  
- An active product is available for sales operations.  
- Product activation is a logical update and does not modify historical data.

---

## General Rules

### System Access Rules

#### User Types

**Administrator**
- Full access to all system functionalities.  
- Can register products (RF-1).  
- Can view products (RF-2).  
- Can update products (RF-3).  
- Can deactivate products (RF-4).  
- Can activate products (RF-22).  
- Can search products by barcode (RF-5).  
- Can register products by barcode (RF-6).  
- Can view a specific product (RF-12).  
- Can register sales (RF-7).  
- Can view sales (RF-8).  
- Can add products to a sale via barcode (RF-9).  
- Can generate sale tickets (RF-10).  
- Can view a specific sale (RF-13).  
- Can authenticate in the system (RF-11).  
- Can change system configuration settings, including business name, business address, and interface language (RF-14).
- Can view sales statistics (RF-15).
- Can log out of the system (RF-16).
- Can register users (RF-17).  
- Can view users (RF-18).  
- Can view a specific user (RF-19).  
- Can update users (RF-20).  
- Can change user status (RF-21). 

**Operator (Cashier)**
- Can authenticate in the system (RF-11).  
- Can view products (RF-2).  
- Can search products by barcode (RF-5).  
- Can view a specific product (RF-12).  
- Can register sales (RF-7).  
- Can view sales (RF-8).  
- Can add products to a sale via barcode (RF-9).  
- Can generate sale tickets (RF-10).  
- Can view a specific sale (RF-13).  
- Can change the interface language (RF-14).  
- Can log out of the system (RF-16). 
- Cannot register products (RF-1).  
- Cannot update products (RF-3).  
- Cannot deactivate products (RF-4).  
- Cannot activate products (RF-22).  
- Cannot register products by barcode (RF-6).  
- Cannot modify business name or business address (RF-14).  
- Cannot view sales statistics (RF-15).  
- Cannot register users (RF-17).  
- Cannot view users (RF-18).  
- Cannot view a specific user (RF-19).  
- Cannot update users (RF-20).  
- Cannot change user status (RF-21).

---

### Numeric and Decimal Data

- All numeric values representing monetary amounts or product quantities must be stored and displayed with a maximum of 2 decimal places and 10 digits in the integer part.  
  This includes:  
  - Product price.  
  - Product price in sale detail (sale price).  
  - Subtotal of each sale detail.  
  - Total sale amount.  
  - Product stock.  
  - Product quantity in each sale detail.
  - Minimum stock of a product.
- For products with unit of measure "Units", stock, quantity, and minimum stock must be integers, even though the system supports decimal precision.

---

### Date and Time Formats

- All dates in the system must be displayed in `DD/MM/YYYY` format.  
- All times in the system must be displayed in 24-hour format with seconds `HH:MM:SS`.  
- These formats apply to the user interface and reports.  
- Any milliseconds stored in the database must not be displayed in the user interface or reports.