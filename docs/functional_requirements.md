## Índice

### Requerimientos Funcionales
- [RF-1: Registrar Producto](#rf-1-registrar-producto)
- [RF-2: Visualizar Productos](#rf-2-visualizar-productos)
- [RF-3: Actualizar Producto](#rf-3-actualizar-producto)
- [RF-4: Desactivar Producto (Eliminación Lógica)](#rf-4-desactivar-producto-eliminación-lógica)
- [RF-5: Buscar Producto por Código de Barras](#rf-5-buscar-producto-por-código-de-barras)
- [RF-6: Registrar Producto mediante Código de Barras](#rf-6-registrar-producto-mediante-código-de-barras)
- [RF-7: Register Sale](#rf-7-register-sale)
- [RF-8: View Sales](#rf-8-view-sales)
- [RF-9: Add Product to Sale via Barcode](#rf-9-add-product-to-sale-via-barcode)
- [RF-10: Generate Sale Ticket](#rf-10-generate-sale-ticket)
- [RF-11: User Authentication](#rf-11-user-authentication)
- [RF-12: Visualizar Producto](#rf-12-visualizar-producto)
- [RF-13: View Sale](#rf-13-view-sale)
- [RF-14: Change System Configuration](#rf-14-change-system-configuration)
- [RF-15: View Sales Statistics](#rf-15-view-sales-statistics)
- [RF-16: Logout](#rf-16-logout)
- [RF-17: Register User](#rf-17-register-user)
- [RF-18: View Users](#rf-18-view-users)
- [RF-19: View User](#rf-19-view-user)
- [RF-20: Update User](#rf-20-update-user)
- [RF-21: Change User Status](#rf-21-change-user-status)
- [RF-22: Reactivar Producto](#rf-22-reactivar-producto)
- [RF-23: Generate Sales Statistics Report (PDF)](#rf-23-generate-sales-statistics-report-pdf)


### General Rules
- [System Access Rules](#system-access-rules)
  - [User Types](#user-types)
- [Numeric and Decimal Data](#numeric-and-decimal-data)
- [Date and Time Formats](#date-and-time-formats)

## RF-1: Registrar Producto

### Descripción

El sistema debe permitir al usuario registrar un nuevo producto.

### Flujo Principal

1. El usuario accede a la sección de productos.
2. El usuario solicita registrar un nuevo producto.
3. El sistema solicita los siguientes datos del producto: código, nombre, precio, unidad de medida, stock disponible y stock mínimo.
4. El usuario ingresa los datos requeridos.
5. El sistema valida la información ingresada.
6. El sistema guarda el producto en la base de datos.
7. El sistema confirma el registro exitoso mediante un mensaje de confirmación: "Producto registrado correctamente".

### Flujos Alternativos

**5.a Datos inválidos**

5.a.1 El sistema muestra un mensaje de error indicando los campos incorrectos.
5.a.2 El usuario corrige los datos.

**5.b El producto ya existe en la base de datos**

5.b.1 El sistema detecta que ya existe un producto con el mismo código.
5.b.2 El sistema muestra un mensaje de error: "Ya existe un producto con el código '{productCode}'".

### Reglas de Negocio

- El código del producto es obligatorio y debe ser único dentro del sistema.
- El nombre del producto es obligatorio.
- El estado del producto solo puede tomar los valores "Activo" o "Inactivo".
- El estado del producto no debe ser ingresado por el usuario y se establece automáticamente como "Activo" al momento de su creación.
- El precio del producto es obligatorio y debe ser un número real mayor o igual a 0.
- El stock disponible es obligatorio y debe ser un número real mayor o igual a 0.
- El stock mínimo es obligatorio y debe ser un número real mayor o igual a 0.
- Los campos precio, stock disponible y stock mínimo deben inicializarse con un valor predeterminado de 0 en el formulario de registro.
- El nombre del producto debe ser descriptivo y distinguirse claramente de otros productos similares existentes en el sistema.
- La unidad de medida debe ser uno de los siguientes valores: "Unidades", "Kilogramos" o "Litros".
- La unidad de medida es obligatoria y se inicializa con el valor predeterminado "Unidades" en el formulario de registro.
- Si la unidad de medida es "Unidades", tanto el stock disponible como el stock mínimo deben expresarse mediante valores enteros.

---

## RF-2: Visualizar Productos

### Descripción

El sistema debe permitir al usuario visualizar los productos registrados en el sistema.

### Flujo Principal

1. El usuario accede a la sección de productos.
2. El usuario solicita visualizar los productos.
3. El sistema recupera de la base de datos una lista paginada de productos.
4. El sistema muestra los productos registrados con la siguiente información:
   - Código del producto
   - Nombre del producto
   - Precio
   - Estado
   - Stock disponible (mostrado junto con su unidad de medida, por ejemplo: "2.5 kg", "3 u")

### Flujos Alternativos

**3.a No existen productos registrados**

3.a.1 El sistema detecta que no existen productos en la base de datos.
3.a.2 El sistema muestra el mensaje: "No hay productos disponibles".

**3.b Búsqueda de productos**

**3.b.1 Por nombre**

3.b.1.1 El usuario ingresa un nombre o una parte del nombre del producto.
3.b.1.2 El sistema filtra los productos que coinciden con el nombre ingresado.

**3.b.2 Por código**

3.b.2.1 El usuario ingresa un código o una parte del código del producto.
3.b.2.2 El sistema filtra los productos que coinciden con el código ingresado.

**3.c Ordenamiento de productos**

**3.c.1 Por nombre**

3.c.1.1 El usuario selecciona un criterio de ordenamiento por nombre ("Ascendente" o "Descendente").
3.c.1.2 El sistema ordena los productos de acuerdo con el criterio seleccionado.

**3.d Filtrado de productos**

**3.d.1 Por estado**

3.d.1.1 El usuario selecciona un estado de producto ("Activo", "Inactivo" o "Todos los estados").
3.d.1.2 El sistema filtra los productos de acuerdo con el estado seleccionado.

**3.d.2 Por nivel de stock**

3.d.2.1 El usuario selecciona un filtro de stock ("Todos los niveles de stock", "Stock normal", "Bajo stock" o "Sin stock").
3.d.2.2 El sistema filtra los productos de acuerdo con la condición de stock seleccionada.

**3.f Paginación de productos**

3.f.1 El sistema permite navegar entre las páginas de productos mediante las opciones de página siguiente y página anterior.

**3.g No se encontraron productos**

3.g.1 El sistema detecta que ningún producto coincide con los criterios de búsqueda.
3.g.2 El sistema muestra el mensaje: "No existen productos que coincidan con los criterios de búsqueda".

### Reglas de Negocio

- El sistema debe permitir visualizar todos los productos registrados mediante paginación.
- El sistema debe recuperar los productos en páginas de 50 elementos por defecto.
- El sistema debe permitir navegar entre las páginas de productos mediante las opciones de página siguiente y página anterior.
- Si no se especifica una página, el sistema debe devolver la primera página por defecto.
- El sistema debe permitir buscar productos por nombre o código.
- El sistema debe permitir ordenar los productos por nombre ("Ascendente" o "Descendente").
- El sistema debe permitir filtrar productos por estado ("Activo", "Inactivo" o "Todos los estados").
- Si no se selecciona un filtro de estado, el sistema debe mostrar "Todos los estados" por defecto.
- Si no se selecciona un criterio de ordenamiento, el sistema debe aplicar "Ascendente" por defecto.
- El sistema debe garantizar que únicamente se recuperen de la base de datos los productos correspondientes a la página solicitada (paginación del lado del servidor).
- El sistema debe indicar visualmente la condición de stock de cada producto en función del stock disponible y del stock mínimo.
- Un producto se considera con "Stock normal" cuando el stock disponible es mayor que el stock mínimo.
- Un producto se considera con "Bajo stock" cuando el stock disponible es mayor que 0 y menor o igual que el stock mínimo.
- Un producto se considera "Sin stock" cuando el stock disponible es igual a 0.
- Los productos con condición de "Bajo stock" o "Sin stock" deben resaltarse visualmente en la lista de productos.
- El sistema debe permitir filtrar productos por condición de stock ("Todos los niveles de stock", "Stock normal", "Bajo stock" o "Sin stock").
- Si no se selecciona un filtro de stock, el sistema debe mostrar "Todos los niveles de stock" por defecto.

---

## RF-3: Actualizar Producto

### Descripción

El sistema debe permitir al usuario actualizar la información de un producto existente.

### Flujo Principal

1. El usuario accede a la sección de productos.
2. El usuario selecciona la opción de actualización para un producto existente.
3. El sistema muestra la información actual del producto seleccionado en un formulario editable.
4. El usuario modifica los siguientes datos del producto: nombre, precio, unidad de medida, stock disponible y stock mínimo.
5. El sistema valida la información ingresada.
6. El sistema actualiza la información del producto en la base de datos.
7. El sistema muestra un mensaje de confirmación: "Producto actualizado correctamente".

### Flujos Alternativos

**2.a Producto no encontrado**

2.a.1 El sistema detecta que el producto seleccionado no existe.
2.a.2 El sistema muestra el mensaje: "No se encontró el producto con código '{productCode}'".

**5.a Datos inválidos**

5.a.1 El sistema muestra un mensaje de error indicando los campos incorrectos.
5.a.2 El usuario corrige los datos.

### Reglas de Negocio

- Todos los campos editables (nombre, precio, unidad de medida, stock disponible y stock mínimo) son obligatorios.
- El precio del producto debe ser un número real mayor o igual a 0.
- El stock disponible debe ser un número real mayor o igual a 0.
- El stock mínimo debe ser un número real mayor o igual a 0.
- El estado del producto no puede modificarse en este proceso.
- El código del producto no puede modificarse en este proceso.
- El nombre del producto debe ser descriptivo y distinguirse claramente de otros productos similares existentes en el sistema.
- La unidad de medida debe ser uno de los siguientes valores: "Unidades", "Kilogramos" o "Litros".
- Si la unidad de medida es "Unidades", tanto el stock disponible como el stock mínimo deben expresarse mediante valores enteros.
- Si la unidad de medida es modificada, el sistema debe validar que el stock disponible y el stock mínimo cumplan con las restricciones de la nueva unidad de medida.
- Reposición de stock:
  - El sistema debe permitir incrementar el stock disponible mediante una cantidad de reposición.
  - La cantidad de reposición debe ser un número real mayor o igual a 0.
  - Al aplicar una reposición, el stock disponible resultante debe corresponder a la suma entre el stock disponible actual y la cantidad de reposición ingresada.

---

## RF-4: Desactivar Producto (Eliminación Lógica)

### Descripción

El sistema debe permitir al usuario desactivar lógicamente un producto marcándolo como inactivo.

### Flujo Principal

1. El usuario accede a la sección de productos.
2. El usuario selecciona la opción de desactivación para un producto activo.
3. El sistema solicita confirmación de la acción.
4. El usuario confirma la operación.
5. El sistema actualiza el estado del producto a "Inactivo".
6. El sistema muestra un mensaje de confirmación: "Producto desactivado correctamente".

### Flujos Alternativos

**2.a Producto no encontrado**

2.a.1 El sistema detecta que el producto seleccionado no existe.
2.a.2 El sistema muestra el mensaje: "Producto no encontrado".

**3.a Operación cancelada**

3.a.1 El usuario cancela la operación.
3.a.2 El sistema no aplica ningún cambio al producto.

**5.a Producto ya inactivo**

5.a.1 El sistema detecta que el producto ya se encuentra inactivo.
5.a.2 El sistema muestra el mensaje: "El producto con código 'productCode' ya se encuentra inactivo".

### Reglas de Negocio

- La desactivación de productos es lógica y no física.
- El estado del producto solo puede tomar los valores "Activo" o "Inactivo".
- Un producto inactivo no debe estar disponible para operaciones de venta.
- La información histórica del producto debe conservarse después de su desactivación.

---

## RF-5: Buscar Producto por Código de Barras

### Descripción

El sistema debe permitir al usuario identificar un producto existente mediante el escaneo de su código de barras.

### Flujo Principal

1. El usuario accede a la sección de productos.
2. El usuario escanea el código de barras del producto utilizando un lector.
3. El sistema recupera el producto asociado al código de barras escaneado.
4. El sistema muestra la información del producto utilizando la operación Visualizar Producto (RF-12).

### Flujos Alternativos

**2.a Código de barras ilegible o no reconocido**

2.a.1 El sistema detecta que el código de barras no puede ser leído.
2.a.2 El sistema muestra el mensaje: "Código de barras no reconocido, inténtelo nuevamente".

**3.a Producto no encontrado**

3.a.1 El sistema detecta que no existe ningún producto asociado al código escaneado.
3.a.2 El sistema ofrece al usuario la opción de registrar un nuevo producto utilizando el código de barras escaneado (ver RF-6: Registrar Producto mediante Código de Barras).

### Reglas de Negocio

- El código de barras debe ser único dentro del sistema.
- El código de barras escaneado debe corresponder a un producto existente en la base de datos.

---

## RF-6: Registrar Producto mediante Código de Barras

### Descripción

El sistema debe permitir al usuario iniciar el registro de un producto utilizando un código de barras escaneado como identificador. Esta funcionalidad complementa el RF-1: Registrar Producto.

### Flujo Principal

1. El usuario accede a la sección de productos.
2. El usuario escanea el código de barras del producto utilizando un lector.
3. El sistema inicia el proceso de registro de producto (RF-1) con el código de barras escaneado precargado como código del producto.

### Reglas de Negocio

- El registro completo del producto debe cumplir las reglas definidas en el RF-1: Registrar Producto. 

---

## RF-7: Register Sale

### Description
The system must allow the user to register sales made in the business, storing the general sale data and managing the products included in the sale during the registration process.

### Main Flow
1. The user accesses the sales section.  
2. The user requests to register a new sale.  
3. The system creates a sale in progress.  
4. The system initializes a new sale in progress and displays an empty sale detail.
5. The system displays the product selection interface, which includes:
   - Product search by name or code  
   - Product list with pagination  
   - Refresh product list option   
6. The user searches and selects a product from the available product list.
7. The system displays the selected product information:
   - Product code  
   - Product name  
   - Product price
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
   - Product name at the time of the sale  
   - Price at the time of the sale  
   - Quantity sold  
   - Unit of measure at the time of the sale
   - Subtotal (calculated as quantity × price, not stored) 
20. The system displays a confirmation prompt: "Do you want to print the receipt?"
21. The user confirms receipt printing, invoking the Generate Receipt operation (RF-10).  
22. The system completes the sale registration process and returns to the sales section.
23. The system displays the message: "Sale successfully registered".

### Alternative Flows

**5.a No available products**
5.a.1 The system detects that there are no active products with available stock.
5.a.2 The system displays a message: "No active products with available stock found".

**5.b Product pagination**  
5.b.1 The user navigates between product pages (e.g., next page or previous page).  
5.b.2 The system retrieves the corresponding page of available products.  
5.b.3 The system displays the products belonging to the selected page.

**6.b Products not found**
6.b.1 The system detects that no products match the search criteria.
6.b.2 The system displays a message: "No products match the search criteria".

**10.a Invalid quantity**  
10.a.1 The system detects that the entered quantity is less than or equal to 0 or incompatible with the product's unit of measure.  
10.a.2 The system displays an error message indicating the required correction.

**16.a Sale canceled**  
16.a.1 The user decides to cancel the sale before confirmation.  
16.a.2 The system discards the sale in progress and returns to the sales section.

**16.b Sale without products**  
16.b.1 The system detects that the sale has no associated products.  
16.b.2 The system displays a message: "The sale must contain at least one product".

**16.c Product Not Found**  
16.c.1 The system detects that one or more products included in the sale do not exist. 
16.c.2 The system displays a message: "Product with code '{productCode}' not found".

**16.d Inactive product**  
16.d.1 The system detects that one or more products included in the sale have inactive status. 
16.d.2 The system displays a message: "Product '{productCode} - {productName}' is inactive and cannot be added to the sale".

**16.e Insufficient stock**  
16.e.1 The system detects that one or more products included in the sale have a requested quantity greater than the available stock.  
16.e.2 The system displays a message: "Insufficient stock for product {productCode} - {productName}"  

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
- The product selection interface must support pagination of 10 items per page by default.
- The system must allow navigation between product pages (next and previous).
- If no page is specified, the system must return the first product page by default.
- The system must ensure that only the products belonging to the requested page are retrieved from the database (server-side pagination).
- The system must allow refreshing the product list to retrieve updated data.
- The product selection interface must only display active products with available stock greater than 0.
- The system must exclude inactive products and products with zero stock from the product selection list.
- The product selection interface must visually indicate products with low stock to assist the user during the sale process.
- The unit of measure used for each product must be stored in the sale detail at the time the sale is confirmed.
- The sale detail stores historical snapshot data for product name, price, and unit of measure at the time of the sale. Changes made later to product information must not affect previously registered sales.

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

**3.b.1 By sale ID**  
3.b.1.1 The user enters the sale ID.  
3.b.1.2 The system filters the sales list to show the matching sale.

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

**3.f.1 By sale ID**  
3.f.1.1 The system detects that no sale matches the entered sale ID.  
3.f.1.2 The system displays a message: "Sale with ID '{saleId}' not found".

**3.f.2 By filters**  
3.f.2.1 The system detects that no sales match the applied criteria.  
3.f.2.2 The system displays a message: "No sales match the search criteria".

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
The system must allow generating a purchase ticket for each registered sale, representing the proof of the transaction and allowing its unique identification, including the relevant information of the sale and its associated products.

### Main Flow
1. The user accesses the sales section.  
2. The user selects a registered sale.  
3. The user requests to print the sale ticket.   
4. The system retrieves the general sale data.  
5. The system retrieves the associated sale details.  
6. The system generates the sale ticket with the following information:  
   - Business name  
   - Business address
   - Unique sale identifier
   - Ticket issue date  
   - Ticket issue time  
   - For each product included in the sale, the following information is displayed:
     - Product name at the time of the sale  
     - Quantity along with its unit of measure at the time of the sale (e.g., "2.5 kg", "1 u")  
     - Price at the time of the sale  
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
- The ticket information must be generated exclusively from the data stored in the sale and its sale details.  
- The ticket must reflect the historical state of each product at the time of the sale (name, price, and unit of measure).  
- The subtotal of each sale detail is automatically calculated by the system.  
- The ticket must include all products associated with the sale.  
- The ticket constitutes proof of the completed transaction.
- Each ticket must include a unique identifier that allows the sale to be traced and audited.

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

## RF-12: Visualizar Producto

### Descripción

El sistema debe permitir al usuario visualizar la información detallada de un producto específico registrado en el sistema.

### Flujo Principal

1. El usuario accede a la sección de productos.
2. El usuario solicita visualizar un producto específico.
3. El sistema recupera el producto utilizando su código de producto.
4. El sistema muestra la información del producto con los siguientes datos:
   - Código del producto
   - Nombre del producto
   - Precio
   - Unidad de medida
   - Estado
   - Stock disponible
   - Stock mínimo

### Flujos Alternativos

**3.a Producto no encontrado**

3.a.1 El sistema detecta que el producto no existe.
3.a.2 El sistema muestra el mensaje: "No se encontró el producto con código '{productCode}'".

### Reglas de Negocio

- El sistema debe permitir consultar un producto específico utilizando su código de producto.
- La información mostrada del producto debe reflejar los datos más recientes almacenados en la base de datos.
- Los productos con condición de "Bajo stock" o "Sin stock" deben resaltarse visualmente en la vista de detalle del producto.

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
   - Seller username
   - Total sale amount  
   - Details of each sold product, where for each product the following information is displayed:
     - Product code
     - Product name as recorded at the time of the sale  
     - Quantity along with the unit of measure recorded at the time of the sale  
     - Price at the time of the sale  
     - Subtotal  

### Alternative Flows

**3.a Sale Not Found**  
3.a.1 The system detects that the sale does not exist.  
3.a.2 The system displays a message: "Sale with ID '{saleId}' not found".

### Business Rules
- The system must allow querying a specific sale using its identifier.  
- The displayed information must accurately reflect the stored data of the sale and its details.  
- The quantity of each product must be displayed together with the unit of measure stored in the sale detail.    
- The product code is retrieved from the associated product entity.  

---

## RF-14: Change System Configuration

### Description
The system must allow managing global configuration settings

### Main Flow
1. The user accesses the system configuration section.  
2. The system displays the current configuration values:
   - Business name  
   - Business address   
3. The user modifies the configuration values.
   - Business name and business address (administrator only)  
4. The system validates the entered data.  
5. The system applies the changes:
   - The business name and business address are updated globally (if modified by an administrator)  
6. The system confirms that the configuration has been successfully updated.

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

---

## RF-15: View Sales Statistics

### Description
The system must allow the user to view statistical information about sales and products based on selected filters.

### Main Flow
1. The user accesses the statistics section.  
2. The user selects the desired filters:
   - User ("All Users" or a specific user)  
   - Date range (start date and end date)  
3. The user requests the statistics generation.
4. The system retrieves aggregated statistical data from the database based on the selected filters.
5. The system displays the statistics divided into the following sections:

   **Sales Information:**
   - Total revenue  
   - Total number of sales  
   - Average ticket value  
   - Hour with the highest revenue  
   - Hour with the highest number of sales  
   - The system must display time-based charts for:
     - Total revenue over time
     - Number of sales over time 

   **Product Information:**

   **Sold Products:**
   - Top 10 products based on quantity sold (chart), including for each product:
     - Product code
     - Product name
     - Quantity sold

   - Top 10 products based on revenue generated (chart), including for each product:
     - Product code
     - Product name
     - Revenue generated
     
   - Product ranking list based on the selected filters, including for each product:
     - Product code  
     - Product name  
     - Quantity sold  
     - Revenue generated  

   **Unsold Products:**
   - A list of products with no sales based on the selected filters, including for each product:
     - Product code  
     - Product name  

### Alternative Flows

**4.a No data available**  
4.a.1 The system detects that no data matches the selected filters.  
4.a.2 The system displays a message: "No data available for the selected criteria".  

**5.a No unsold products**  
5.a.1 The system detects that no products match the unsold products criteria based on the selected filters.  
5.a.2 The system displays a message in the Unsold Products section: "No unsold products for the selected filters".  

**5.b Filter sold products ranking list**  
5.b.1 The user selects the desired filters:
   - Metric ("Quantity Sold" or "Revenue Generated")
   - Order ("Most sold → least sold" or "Least sold → most sold")
5.b.2 The system retrieves the corresponding products based on the selected filters.  

**5.c Product pagination**  
5.c.1 The system allows navigation between pages of product lists (e.g., next page, previous page, or direct page selection).  
5.c.2 This applies to both sold products ranking list and unsold products list. 

### Business Rules

- The system must allow filtering statistics by user ("All Users" or a specific user).  
- The system must allow filtering statistics by date range (start date and end date).  
- The system must calculate all statistical values based only on the selected filters.  

- The system must calculate:
  - Total revenue as the sum of all sales amounts  
  - Total number of sales as the count of sales records  
  - Average ticket value as total revenue divided by total number of sales  

- The system must determine:
  - Hour with the highest revenue  
  - Hour with the highest number of sales  

- The system must display time-based charts for:
  - Total revenue over time  
  - Number of sales over time  

- The charts must adapt their time granularity according to the selected date range:
  - Hour → when the selected range is a single day
  - Day → for ranges up to 31 days
  - Month → for ranges up to 365 days
  - Year → for ranges greater than 365 days

- The system must display only the top 10 products in each sold products chart:
  - Top 10 products based on quantity sold  
  - Top 10 products based on revenue generated

- The system must allow viewing detailed lists in the statistics section using pagination for both sold products ranking list and unsold products list:
  - The system must retrieve these lists in pages of 20 items by default.
  - The system must ensure server-side pagination.
  - If no page is specified, the system must return the first page by default for both paginated product lists.
  - The system must allow navigation between pages for both sold products ranking list and unsold products list.

- The system must allow sorting the product ranking list by:
  - Quantity sold  
  - Revenue generated  

- The system must allow ordering the product ranking list:
  - Most sold → least sold  
  - Least sold → most sold

- The system must identify unsold products as those with zero sales within the selected date range.  

- For the statistics global filters:
  - If no user filter is selected, the system defaults to "All Users".
  - If no date range is selected, the system defaults to the current date.

- For the sold products ranking list:
  - If no metric is selected, the system defaults to "Revenue Generated".
  - If no ordering criterion is selected, the system defaults to "Most sold → least sold".

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
8. The system assigns a unique identifier (User ID) to the new user.  
9. The system stores the user in the database.  
10. The system confirms that the user has been successfully registered.

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
The system must display all users with the role "Operator", including those with any status ("Active", "Suspended", "Deleted").

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

**3.a User Not Found**  
3.a.1 The system detects that the selected user does not exist.  
3.a.2 The system displays a message: "User with username '{username}' not found".

### Business Rules
- Only users with the Administrator role can perform this action.  
- The system must not display the user's password.  
- The system must display only non-sensitive user information.

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
6.a.2 The system displays a message: "Username '{username}' already exists".

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
5. The system updates the user status in the database.  
6. The system confirms that the user status has been successfully updated.

### Business Rules
- Only users with the Administrator role can perform this action.  
- The system must allow only the following status values: "Active", "Suspended", "Deleted".  
- Users with "Deleted" status must not be physically removed from the database.  
- Users with "Suspended" or "Deleted" status must not be able to access the system.

---

## RF-22: Reactivar Producto

### Descripción

El sistema debe permitir al usuario reactivar un producto inactivo marcándolo como activo.

### Flujo Principal

1. El usuario accede a la sección de productos.
2. El usuario selecciona la opción de reactivación para un producto inactivo.
3. El sistema solicita confirmación de la acción.
4. El usuario confirma la operación.
5. El sistema actualiza el estado del producto a "Activo".
6. El sistema muestra un mensaje de confirmación: "Producto reactivado correctamente".

### Flujos Alternativos

**2.a Producto no encontrado**

2.a.1 El sistema detecta que el producto seleccionado no existe.
2.a.2 El sistema muestra el mensaje: "Producto no encontrado".

**3.a Operación cancelada**

3.a.1 El usuario cancela la operación.
3.a.2 El sistema no aplica ningún cambio al producto.

**5.a Producto ya activo**

5.a.1 El sistema detecta que el producto ya se encuentra activo.
5.a.2 El sistema muestra el mensaje: "El producto con código 'productCode' ya se encuentra activo".

### Reglas de Negocio

- Solo los productos inactivos pueden ser reactivados.
- Un producto activo está disponible para operaciones de venta.
- La reactivación de un producto constituye una actualización lógica y no modifica la información histórica.

---

## RF-23: Generate Sales Statistics Report (PDF)

### Description
The system must allow the user to generate a downloadable PDF report based on sales statistics and product performance, using the same filters and data as the statistics module.

### Main Flow
1. The user accesses the statistics section.
2. The user selects the desired filters:
   - User ("All Users" or a specific user)
   - Date range (start date and end date)
3. The user requests to generate a PDF report.
4. The user selects which sections to include in the report:
   - Sales Information
   - Product Information
5. The system generates a PDF report containing only the selected sections.
6. The system provides the PDF file for download.

### Report Content Rules

The report must always include:

- Report title
- Report generation date and time
- Selected user
- Selected date range

Each selected section must include all its corresponding information.

**Sales Information:**
- Total revenue
- Total number of sales
- Average ticket value
- Hour with the highest revenue
- Hour with the highest number of sales

- Revenue over time table, including:
  - Time period
  - Revenue generated

- Sales over time table, including:
  - Time period
  - Number of sales

**Product Information:**

**Sold Products:**

- Top 10 products by quantity sold, including:
  - Product code
  - Product name
  - Quantity sold

- Top 10 products by revenue generated, including:
  - Product code
  - Product name
  - Revenue generated

- Product ranking list based on selected filters, including:
  - Selected ranking metric
  - Selected ranking order
  - Total products matching the selected filters
  - Number of products included in the report

  For each product:
  - Product code
  - Product name
  - Quantity sold
  - Revenue generated

**Unsold Products:**

- List of products with no sales based on selected filters, including:
  - Total products matching the selected filters
  - Number of products included in the report

  For each product:
  - Product code
  - Product name

- If no products match the criteria, the system must display the message:
  "No unsold products for the selected filters"


### Alternative Flows

**4.a Report generation canceled**
4.a.1 The user cancels the report generation process.
4.a.2 The system returns to the statistics view without generating the PDF.

**4.b No sections selected**
4.b.1 The user does not select any report section.
4.b.2 The system displays a message:
       "At least one section must be selected".
4.b.3 The system does not generate the report.

**5.a No data available**
5.a.1 The system detects that no data matches the selected filters.
5.a.2 The system displays a message:
       "No data available for the selected criteria".
5.a.3 The system does not generate the report. 

### Business Rules

- The report title must be "Sales Statistics Report".
- If the available products are fewer than the selected report limit, the system must include all available products.
- The user must select at least one report section before generating the PDF report.
- The detailed product lists included in the report must indicate:
  - Total products matching the selected filters
  - Number of products included in the report
- The report must be generated using the filters currently selected in the statistics section.
- The system must generate the report on demand and must not store it in the system.  
- The user must be able to select which full sections are included in the report:
  - Sales Information
  - Product Information

- The system must include the report generation date and time in the PDF.

- The system must ensure that each section is included in its entirety (no partial sections).  

- The system must allow configuring the number of products included in the detailed product lists:
  - 10 items  
  - 20 items  
  - 50 items  
  - 100 items  

  This configuration is applied independently to each list:
  - Product ranking list
  - List of products with no sales

- The system must apply default selections when the report generation process is opened:
  - All report sections are selected by default:
    - Sales Information
    - Product Information

- The system must apply default values for detailed product lists:
  - 20 items for Product ranking list
  - 20 items for List of products with no sales

- The system must allow configuring the Product Ranking List included in the report by:
  - Quantity Sold
  - Revenue Generated

- The system must allow ordering the Product Ranking List included in the report:
  - Most sold → least sold
  - Least sold → most sold

- If no metric is selected for the Product Ranking List, the system defaults to "Revenue Generated".

- If no ordering criterion is selected for the Product Ranking List, the system defaults to "Most sold → least sold".

- The system must ensure that the PDF reflects exactly the selected filters at the moment of generation.

- Revenue over time and number of sales over time statistics included in the report must be presented as tabular data.



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
- Can change system configuration settings, including business name and business address (RF-14).
- Can view sales statistics (RF-15).
- Can log out of the system (RF-16).
- Can register users (RF-17).  
- Can view users (RF-18).  
- Can view a specific user (RF-19).  
- Can update users (RF-20).  
- Can change user status (RF-21). 
- Can generate sales statistics reports in PDF format (RF-23).

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
- Cannot generate sales statistics reports in PDF format (RF-23).

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