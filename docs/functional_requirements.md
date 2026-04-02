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
- [RF-9: Update Sale](#rf-9-update-sale)
- [RF-10: Delete Sale](#rf-10-delete-sale)
- [RF-11: Add Product to Sale via Barcode](#rf-11-add-product-to-sale-via-barcode)
- [RF-12: Associate Product to Sale](#rf-12-associate-product-to-sale)
- [RF-13: Remove Product from Sale](#rf-13-remove-product-from-sale)
- [RF-14: Generate Sale Ticket](#rf-14-generate-sale-ticket)
- [RF-15: User Authentication](#rf-15-user-authentication)
- [RF-16: Refresh Product / Sale / Sales Statistics List](#rf-16-refresh-product--sale--sales-statistics-list)
- [RF-17: View Product](#rf-17-view-product)
- [RF-18: View Sale](#rf-18-view-sale)
- [RF-19: Change System Configuration](#rf-19-change-system-configuration)
- [RF-20: View Sales Statistics](#rf-20-view-sales-statistics)


### General Rules
- [System Access Rules](#system-access-rules)
  - [User Types](#user-types)
- [Stored User Data](#stored-user-data)
- [Numeric and Decimal Data](#numeric-and-decimal-data)
- [Date and Time Formats](#date-and-time-formats)

## RF-1: Register Product

### Description
The system must allow the user to register a new product.

### Main Flow
1. The user accesses the product section.  
2. The user requests to register a new product.  
3. The system requests the following product data: code, name, price, unit of measure, status, and available stock.  
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
5.b.2 The system displays an error message: "Product is already registered".  

### Business Rules
- The product code is mandatory and must be unique within the system.  
- The product name is mandatory.  
- The product status is mandatory and defaults to "Active".  
- The product price is mandatory and must be a real number greater than or equal to 0.  
- The available stock is mandatory and must be a real number greater than or equal to 0.  
- The system must assign a default value of 0 to both price and stock when creating the product.  
- The product name must be descriptive and clearly distinguishable from other similar products in the system.  
- The unit of measure is mandatory (e.g., unit, kilogram, liter), and defaults to "Units".  
- If the unit of measure is "unit", the stock must be expressed in integer values.  

---

## RF-2: View Products

### Description
The system must allow the user to view the products registered in the system.

### Main Flow
1. The user accesses the product section.  
2. The user requests to view products.  
3. The system retrieves the list of products from the database.  
4. The system displays the registered products with the following data:
   - Product code  
   - Product name  
   - Price  
   - Status  
   - Available stock (displayed with its unit of measure, e.g., "2.5 kg", "3 units")  

### Alternative Flows

**3.a No registered products**  
3.a.1 The system detects that there are no products in the database.  
3.a.2 The system displays a message: "No products found".  

**3.b Product search**  

**3.b.1 By name**  
3.b.1.1 The user enters a name or part of the product name.  
3.b.1.2 The system filters products that match the entered name.  

**3.b.2 By code**  
3.b.2.1 The user enters a code or part of the product code.  
3.b.2.2 The system filters products that match the entered code.  

**3.c Product sorting**  

**3.c.1 By name**  
3.c.1.1 The user selects a sorting criterion by name (ascending or descending).  
3.c.1.2 The system sorts the products according to the selected criterion.  

**3.d Product filtering**  

**3.d.1 By status**  
3.d.1.1 The user selects a product status (Active, Inactive, or All).  
3.d.1.2 The system filters products according to the selected status.  

**3.f Product not found**  
3.f.1 The system detects that no products match the search criteria.  
3.f.2 The system displays a message: "No products found".  

### Business Rules
- The system must allow viewing all registered products.  
- The system must allow searching products by name or code.  
- The system must allow sorting products by name (ascending or descending).  
- The system must allow filtering products by status (Active, Inactive, or All).  
- If no status filter is selected, the system displays "All" by default.  
- If no sorting criterion is selected, the system applies "Ascending" by default.  

---

## RF-3: Update Product

### Description
The system must allow the user to update the data of an existing product.

### Main Flow
1. The user accesses the product section.  
2. The user selects a product.  
3. The system displays the current product data.  
4. The user requests to update the product.  
5. The user modifies the following product data: code, name, price, unit of measure, and available stock.  
6. The system validates the entered information.  
7. The system updates the product data in the database.  
8. The system displays a confirmation message: "Product successfully updated".  

### Alternative Flows

**2.a Product not found**  
2.a.1 The system detects that the selected product does not exist.  
2.a.2 The system displays a message: "Product does not exist".  

**6.a Invalid data**  
6.a.1 The system displays an error message indicating the incorrect fields.  
6.a.2 The user corrects the data.  

**6.b Duplicate product code**  
6.b.1 The system detects that the entered code already belongs to another product.  
6.b.2 The system displays an error message: "Product code is already in use".  

### Business Rules
- The product code must be unique within the system.  
- All editable fields (code, name, price, unit of measure, and stock) are mandatory.  
- The product price must be a real number greater than or equal to 0.  
- The available stock must be a real number greater than or equal to 0.  
- The product status cannot be modified in this process.  
- The product code can be modified as long as it is not used by another product.  
- The product name must be descriptive and clearly distinguishable from other similar products in the system.  
- The unit of measure is mandatory.  
- If the unit of measure is "unit", the stock must be expressed in integer values.  
- If the unit of measure is modified, the system must validate that the stock complies with the new unit constraints.

---

## RF-4: Deactivate Product (Soft Delete)

### Description
The system must allow the user to logically deactivate a product by marking it as inactive.

### Main Flow
1. The user accesses the product section.  
2. The user selects a product.  
3. The system displays the product data.  
4. The user requests to deactivate the product.  
5. The system requests confirmation of the action.  
6. The user confirms the operation.  
7. The system updates the product status to "Inactive".  
8. The system displays a confirmation message: "Product successfully deactivated".  

### Alternative Flows

**2.a Product not found**  
2.a.1 The system detects that the selected product does not exist.  
2.a.2 The system displays a message: "Product does not exist".  

**5.a Operation canceled**  
5.a.1 The user cancels the operation.  
5.a.2 The system does not apply any changes to the product.  

**7.a Product already inactive**  
7.a.1 The system detects that the product is already inactive.  
7.a.2 The system displays a message: "Product is already inactive".  

### Business Rules
- Product deactivation is logical, not physical.  
- The product status can be "Active" or "Inactive".  
- An inactive product must not be available for sales operations.  

---

## RF-5: Search Product by Barcode

### Description
The system must allow the user to identify an existing product by scanning its barcode.

### Main Flow
1. The user accesses the product section.  
2. The user scans the product barcode using a reader.  
3. The system recognizes the barcode.  
4. The system displays the product data using the View Product operation (RF-17): code, name, price, unit of measure, status, and available stock.  

### Alternative Flows

**3.a Unreadable or unrecognized barcode**  
3.a.1 The system detects that the barcode cannot be read.  
3.a.2 The system displays a message: "Barcode not recognized, please try again".  

**4.a Product not found**  
4.a.1 The system detects that no product exists with the scanned code.  
4.a.2 The system offers the user the option to register a new product using the scanned barcode (see RF-6: Register Product by Barcode).  

### Business Rules
- The barcode must be unique within the system.  
- The product must exist in the database to be identified.  

---

## RF-6: Register Product by Barcode

### Description
The system allows the user to initiate product registration using a scanned barcode as an identifier. This functionality complements RF-1: Register Product.

### Main Flow
1. The user accesses the product registration section.  
2. The user scans the product barcode using a reader.  
3. The system redirects to RF-1: Register Product to complete the registration.  

### Alternative Flows

**3.a Barcode already registered**  
3.a.1 The system detects that the scanned barcode already belongs to another product.  
3.a.2 The system offers the user the option to view the product data using the scanned barcode (see RF-5: Search Product by Barcode).  

### Business Rules
- Full product registration follows the rules defined in RF-1: Register Product.    

---

## RF-7: Register Sale

### Description
The system must allow the user to register sales made in the business, storing the general sale data and managing associated products through operations on sale details.

### Main Flow
1. The user accesses the sales section.  
2. The user requests to register a new sale.  
3. The system creates a sale in progress.  
4. The user adds products to the sale using the Add Product to Sale operation (RF-12).  
5. The system displays, for each added product, its name, price, and quantity along with its unit of measure.  
6. The system calculates line subtotals and the total sale amount.  
7. The system automatically records the current date and time of the sale.  
8. The user confirms the sale.  
9. The system registers the sale with the following data:
   - Unique sale identifier (automatically generated by the system)  
   - Sale date  
   - Sale time  
   - Total amount  
   - User who performed the sale  
10. The system registers the sale details associated with the sale.  
11. The system displays a confirmation message: "Sale successfully registered. Do you want to print the receipt?".  
12. The user confirms receipt printing, invoking the Generate Receipt operation (RF-14).  
13. The system automatically returns to the sales section.  

### Alternative Flows

**4.a Error adding product**  
4.a.1 The system detects an error during the product addition (according to RF-12).  
4.a.2 The system displays the corresponding message and allows the operation to continue.  

**8.a Sale canceled**  
8.a.1 The user decides to cancel the sale before confirming it.  
8.a.2 The system discards the sale in progress and returns to the sales section.  

**12.a Receipt printing canceled**  
12.a.1 The user cancels the receipt printing.  
12.a.2 The system completes the sale process and returns to the sales section without generating the receipt.  

### Business Rules
- A sale must have at least one associated product to be registered.  
- Products are added to the sale using the operation defined in RF-12.  
- Subtotals and total sale amount are calculated automatically.  
- While a sale is in progress, it may exist without associated products.  
- If all products are removed during the registration process, the sale must not be automatically deleted.  
- During the registration process, the user can modify product quantities.  
- If a product quantity increases during registration, the system decreases the stock by the corresponding difference.  
- If a product quantity decreases or a detail is removed, the system increases the stock by the corresponding difference.  
- During the registration process, the user can remove products (sale details) using the operation defined in RF-13.  
- The unique sale identifier is automatically assigned by the system.  
- Each sale is associated with the user who registered it.  
- Receipt generation is performed using the operation defined in RF-14.  

---

## RF-8: View Sales

### Description
The system must allow the user to view sales registered in the system, displaying general sale data and their details.

### Main Flow
1. The user accesses the sales section.  
2. The user requests to view sales.  
3. The system retrieves the list of sales from the database.  
4. The system displays the registered sales with the following data:
   - Unique sale identifier  
   - Sale date and time  
   - Seller username (user_name)  
   - Total amount  

### Alternative Flows

**3.a No registered sales**  
3.a.1 The system detects that there are no sales in the database.  
3.a.2 The system displays a message: "No sales found".  

**3.b Sale sorting**

**3.b.1 By time**  
3.b.1.1 The user selects to sort sales by time (Ascending or Descending).  
3.b.1.2 The system sorts the sales according to the selected criterion.  

**3.c Sale filtering**

**3.c.1 By day**  
3.c.1.1 The user selects a specific day.
3.c.1.2 The system filters sales according to the selected day.  

**3.c.2 By month**  
3.c.2.1 The user selects a specific month.
3.c.2.2 The system filters sales according to the selected month.  

**3.c.3 By year**  
3.c.3.1 The user selects a specific year.
3.c.3.2 The system filters sales according to the selected year.  

**3.d Sale not found**  
3.d.1 The system detects that no sales match the applied criteria.  
3.d.2 The system displays a message: "No sales found".  

### Business Rules
- The system must allow viewing sales filtered by a specific date (day, month, and year).
- The system must allow sorting sales by time, in ascending or descending order.  
- By default, the system must use the current date (current day, month, and year).
- If no sorting criterion by time is selected, the system sorts sales by time in descending order by default.    

---

## RF-9: Update Sale

### Description
The system must allow the user to update an existing sale by modifying the products included in the sale and their quantities.

### Main Flow
1. The user accesses the sales section.  
2. The user selects an existing sale.  
3. The system displays the current sale data and its details, including the quantity of each product along with its unit of measure.  
4. The user modifies the quantity of one or more products, respecting the quantity rules according to the product's unit of measure.  
5. The system automatically recalculates line subtotals and the total sale amount.  
6. The user confirms the changes.  
7. The system updates the sale data in the database.  
8. The system updates the stock of affected products.  
9. The system displays a confirmation message: "Sale successfully updated".  

### Alternative Flows

**2.a Sale not found**  
2.a.1 The system detects that the selected sale does not exist.  
2.a.2 The system displays a message: "The sale does not exist".  

**4.a Invalid quantity**  
4.a.1 The system detects that the entered quantity is less than or equal to 0 or greater than the available stock (in case of increase).  
4.a.2 The system displays an error message indicating the required correction.  

**4.b Sale detail not found**  
4.b.1 The system detects that the selected product to modify no longer exists in the sale.  
4.b.2 The system displays a message: "The product is not associated with this sale" and does not apply changes for that product.  

**6.a Update canceled**  
6.a.1 The user cancels the update.  
6.a.2 The system does not apply changes and returns to the sales section.  

### Business Rules
- Only product quantities can be modified.  
- The sale identifier, date, time, and total amount cannot be manually modified.  
- The product price in the sale must not be modified.  
- Line subtotals are calculated automatically.  
- The total sale amount is automatically recalculated based on the changes made.  
- A quantity greater than the available stock cannot be assigned when the modification implies an increase.  
- Quantity must be a real number greater than 0.  
- Product stock must be updated based on the changes made.  
- If a product quantity increases, the system decreases the stock by the corresponding difference.  
- If a product quantity decreases, the system increases the stock by the corresponding difference.  
- If a product quantity remains unchanged, the system must not modify the stock.  
- Modified quantities must be valid according to the product's unit of measure.  
- If the unit of measure is "unit", the quantity must not contain decimals.  

---

## RF-10: Delete Sale

### Description
The system must allow the user to delete an existing sale, including all associated sale details.

### Main Flow
1. The user accesses the sales section.  
2. The user selects an existing sale.  
3. The system displays the sale data and its details.  
4. The user requests to delete the sale.  
5. The system requests confirmation.  
6. The user confirms the deletion.  
7. The system deletes the sale and all its associated details from the database.  
8. The system displays a confirmation message: "Sale successfully deleted".  

### Alternative Flows

**2.a Sale not found**  
2.a.1 The system detects that the selected sale does not exist.  
2.a.2 The system displays a message: "The sale does not exist".  

**5.a Deletion canceled**  
5.a.1 The user cancels the operation.  
5.a.2 The system does not apply changes and returns to the sales section.  

### Business Rules
- Deleting a sale implies deleting all its associated details.  
- Sale deletion is physical (removed from the database).  
- Deleting a sale automatically updates product stock, increasing quantities according to the removed sale details.  

---

## RF-11: Add Product to Sale via Barcode

### Description
The system must allow faster product selection during the sale process using barcode scanning, adding products to the current sale or automatically starting a new one.  
This operation uses RF-12 to associate the product once identified.  

### Main Flow
1. The user accesses the sales section.  
2. The user scans a product using a barcode reader.  
3. The system identifies the product associated with the scanned code.  
4. The system uses the operation defined in RF-12 to add the product to the sale, including quantity and stock update.  

### Alternative Flows

**2.a Product not found**  
2.a.1 The system cannot find a product associated with the scanned barcode.  
2.a.2 The system displays a message: "Product not found".  

**3.a No active sale**  
3.a.1 The system detects that no sale is in progress.  
3.a.2 The system automatically starts a new sale.  
3.a.3 The system continues with step 4.  

### Business Rules
- A barcode must uniquely identify a product.  
- Product addition follows RF-12 rules.  
- If no sale is in progress, the system must start one automatically.  

---

## RF-12: Associate Product to Sale

### Description
The system must allow associating a product to a sale by specifying the desired quantity, regardless of whether the sale is in the process of being registered or has already been registered.

### Main Flow
1. The user provides a sale.  
2. The user selects a product.  
3. The system displays the product information.  
4. The system prompts the user to enter the quantity of units to add, displaying the product’s unit of measure for clarity.  
5. The user enters the desired quantity.  
6. The system validates the entered quantity, ensuring it complies with the product’s unit of measure rules.  
7. The system associates the product to the sale.  
8. The system updates the product stock based on the added quantity.  
9. The system records the sale detail with the following data:  
   - Unique identifier of the sale detail (automatically generated by the system)  
   - Associated product  
   - Associated sale  
   - Added quantity  
   - Unit price  
   *Note:* The subtotal is automatically calculated as `quantity × unit price` and is *not stored*.  
10. The system reflects the product in the sale detail.

### Alternative Flows

**1.a Invalid Sale**  
1.a.1 The system detects that the sale does not exist or is not valid.  
1.a.2 The system displays a message: "Invalid sale".

**2.a Product Not Found**  
2.a.1 The system cannot find the selected product.  
2.a.2 The system displays a message: "Product not found".

**5.a Invalid Quantity**  
5.a.1 The system detects that the entered quantity is less than or equal to 0, greater than the available stock, or not compatible with the product’s unit of measure (e.g., a decimal number for products sold per unit).  
5.a.2 The system displays an error message indicating the required correction.

**7.a Product Already Associated to the Sale**  
7.a.1 The system detects that the product is already associated with the sale.  
7.a.2 The system increments the product quantity in the sale.  
7.a.3 The system continues with the main flow.

### Business Rules
- The sale must exist as a valid entity in the system (in progress or registered).  
- The product must exist in the system.  
- A quantity greater than the available stock cannot be added.  
- If the product is already associated with the sale, its quantity must be increased instead of duplicating the record.  
- Products with status "Inactive" cannot be added to the sale.  
- By default, when associating a product to a sale, the system assigns an initial quantity of 1 unit, which the user can modify before confirmation.  
- The unique identifier of the sale detail is automatically assigned by the system.  
- The quantity must be a number greater than 0 and compatible with the product’s unit of measure.  
- If the product’s unit of measure is "unit", the quantity must not contain decimals.

---

## RF-13: Remove Product from Sale

### Description
The system must allow removing a product from a sale by deleting the corresponding sale detail.

### Main Flow
1. The user provides a sale.  
2. The user selects a product associated with the sale.  
3. The system identifies the corresponding sale detail.  
4. The user requests to remove the product from the sale.  
5. The system deletes the sale detail.  
6. The system updates the sale information.  
7. The system displays the updated sale.

### Alternative Flows

**1.a Invalid Sale**  
1.a.1 The system detects that the sale does not exist or is not valid.  
1.a.2 The system displays a message: "Invalid sale".

**2.a Product Not Associated**  
2.a.1 The system detects that the product is not associated with the sale.  
2.a.2 The system displays a message: "The product is not associated with the sale".

**4.a Operation Cancelled**  
4.a.1 The user cancels the operation.  
4.a.2 The system makes no changes.

### Business Rules
- The sale must exist as a valid entity in the system (in progress or registered).  
- The product must be previously associated with the sale.  
- If the sale is in progress, removing a sale detail will automatically adjust the stock by increasing the corresponding quantity.  
- If the sale has already been registered, removing a sale detail will also automatically adjust the stock by increasing the corresponding quantity.  
- If the sale is in progress and all its products are removed, the sale must not be automatically deleted.  
- If the sale has already been registered and all its products are removed, the sale must be automatically deleted.

---

## RF-14: Generate Sale Ticket

### Description
The system must allow generating a purchase ticket for each registered sale, representing the proof of the transaction and including the relevant information of the sale and its associated products.

### Main Flow
1. The system receives a registered sale.  
2. The system retrieves the general sale data.  
3. The system retrieves the associated sale details.  
4. The system generates the sale ticket with the following information:  
   - Business name  
   - Ticket issue date  
   - Ticket issue time  
   - Name of each sold product  
   - Quantity of each product along with its unit of measure (e.g., "2.5 kg", "1 unit")  
   - Unit price  
   - Subtotal of each product  
   - Total sale amount  
5. The system displays the generated ticket or sends it to the corresponding output medium (screen or printer).

### Alternative Flows

**1.a Invalid Sale**  
1.a.1 The system detects that the sale does not exist or has not been registered.  
1.a.2 The system displays a message: "Invalid sale".

**5.a Ticket Generation Error**  
5.a.1 The system detects an error while generating or printing the ticket.  
5.a.2 The system displays a message: "The ticket could not be generated".

### Business Rules
- The ticket can only be generated for previously registered sales.  
- The ticket information must accurately reflect the sale and its details.  
- The subtotal of each sale detail is automatically calculated by the system.  
- The ticket must include all products associated with the sale.  
- The ticket constitutes proof of the completed transaction.

---

## RF-15: User Authentication

### Description
The system must allow users to access its functionalities through an authentication process based on a **unique username** and password.

### Main Flow
1. The user accesses the login screen.  
2. The user enters their unique username.  
3. The user enters their password.  
4. The system validates that the entered credentials are correct.  
5. The system determines the user role (`Administrator` or `Operator`) and grants access to functionalities according to the associated permissions.

### Alternative Flows

**4.a Invalid Credentials**  
4.a.1 The system detects that the username does not exist or that the password is incorrect.  
4.a.2 The system displays a message: "Invalid credentials".

### Business Rules
- Access to the system requires prior authentication.  
- The entered password must match the provided username.  
- The system automatically determines the user role from the user record and restricts access to functionalities accordingly.

---

## RF-16: Refresh Product / Sale / Sales Statistics List

### Description
The system must allow the user to refresh the list of products, sales, or sales statistics displayed on screen, reflecting any changes that have occurred in the system since the last view.

### Main Flow
1. The user accesses the Products, Sales, or Sales Statistics section.  
2. The user clicks the "Refresh" button.  
3. The system retrieves the most recent information from the database.  
4. The system displays the updated list on screen:  
   - Products  
   - Sales  
   - Sales Statistics (total revenue and list of sold products with quantities)

### Alternative Flows

**3.a Data Retrieval Error**  
3.a.1 The system detects an error while fetching the information.  
3.a.2 The system displays a message: "The list could not be refreshed. Please try again."

### Business Rules
- The refresh operation must only update the displayed information without modifying existing data.  
- The system must reflect changes made by other users or processes since the last update.  
- The refresh button must be available to all users with viewing permissions in the corresponding section (Products, Sales, or Sales Statistics).

---

## RF-17: View Product

### Description
The system must allow the user to view detailed information of a specific product registered in the system.

### Main Flow
1. The user accesses the products section.  
2. The user selects a specific product.  
3. The system receives the product identifier.  
4. The system retrieves the product data from the database.  
5. The system displays the product information with the following data:
   - Product code  
   - Product name  
   - Price  
   - Unit of measure  
   - Status  
   - Available stock  

### Alternative Flows

**3.a Product Not Found**  
3.a.1 The system detects that the product does not exist.  
3.a.2 The system displays a message: "Product not found".

### Business Rules
- The system must allow querying a specific product using its identifier.  

---

## RF-18: View Sale

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
3.a.2 The system displays a message: "Sale not found".

### Business Rules
- The system must allow querying a specific sale using its identifier.  
- The displayed information must accurately reflect the stored data of the sale and its details.  
- The quantity of each product must be displayed together with its unit of measure to correctly represent the sale.

---

## RF-19: Change System Configuration

### Description
The system must allow the user to modify certain system settings, including the interface language and the business name.

### Main Flow
1. The user accesses the system configuration section.  
2. The user can perform the following actions:  
   - Change the interface language
   - Update the business name.  
3. The system validates the entered data
4. The system applies the changes immediately.  
5. The system confirms the changes by updating the displayed interface and business name.

### Alternate Flow
**3.a Invalid data**  
3.a.1 The system displays an error message indicating the incorrect fields.  
3.a.2 The user corrects the data.  

### Business Rules
- The business name is mandatory.
- All changes must persist during the user's session.
- The language change must not affect the data stored in the system.  
- The selected language must persist throughout the user session.  
- All interface elements (menus, buttons, messages, notifications) must be displayed in the selected language.

---

## RF-20: View Sales Statistics

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

## General Rules

### System Access Rules

#### User Types

**Administrator**
- Full access to all system functionalities.  
- Can register products (RF-1).  
- Can view products (RF-2).  
- Can update products (RF-3).  
- Can deactivate products (RF-4).  
- Can search products by barcode (RF-5).  
- Can register products by barcode (RF-6).  
- Can view a specific product (RF-17).  
- Can register sales (RF-7).  
- Can view sales (RF-8).  
- Can update sales (RF-9).  
- Can delete sales (RF-10).  
- Can associate products to sales (RF-11, RF-12).  
- Can remove products from sales (RF-13).  
- Can generate sale tickets (RF-14).  
- Can view a specific sale (RF-18).  
- Can refresh product and sales lists (RF-16).  
- Can authenticate in the system (RF-15).  
- Can change all system configuration settings (RF-19).
- Can view sales statistics (RF-20).

**Operator (Cashier)**
- Can authenticate in the system (RF-15).  
- Can view products (RF-2).  
- Can view a specific product (RF-17).  
- Can search products by barcode (RF-5).  
- Can view sales (RF-8).  
- Can view a specific sale (RF-18).  
- Can register sales (RF-7).  
- Can associate products to sales (RF-11, RF-12).  
- Can remove products from sales during the sale registration process (RF-13).  
- Can generate sale tickets (RF-14).  
- Can refresh product and sales lists (RF-16).  
- Can change the interface language (RF-19).  
- Can view sales statistics (RF-20).  
- Can change the interface language (RF-19).
- Cannot register products (RF-1).  
- Cannot update products (RF-3).  
- Cannot deactivate products (RF-4).  
- Cannot register products by barcode (RF-6).  
- Cannot update sales (RF-9).  
- Cannot delete sales (RF-10).  
- Cannot remove products from sales once the sale has been registered in the system (RF-13).
- Cannot change business name (RF-19)

---

### Stored User Data

- The system must store information for each user to manage access and permissions.  
- Each user will have:
  - User ID: internal unique identifier.  
  - Username: unique name to identify the user in the system.  
  - Role: user type that determines system permissions.  
  - Password: associated with the user account.  

---

### Numeric and Decimal Data

- All numeric values representing monetary amounts or product quantities must be stored and displayed with a maximum of 2 decimal places.  
  This includes:  
  - Product price.  
  - Product price in sale detail (sale price).  
  - Subtotal of each sale detail.  
  - Total sale amount.  
  - Product stock.  
  - Product quantity in each sale detail.

---

### Date and Time Formats

- All dates in the system must be displayed in `DD/MM/YYYY` format.  
- All times in the system must be displayed in 24-hour format with seconds `HH:MM:SS`.  
- These formats apply to the user interface and reports.  