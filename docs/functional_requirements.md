## Índice

### Requerimientos Funcionales
- [RF-1: Registrar Producto](#rf-1-registrar-producto)
- [RF-2: Visualizar Productos](#rf-2-visualizar-productos)
- [RF-3: Actualizar Producto](#rf-3-actualizar-producto)
- [RF-4: Desactivar Producto (Eliminación Lógica)](#rf-4-desactivar-producto-eliminación-lógica)
- [RF-5: Buscar Producto por Código de Barras](#rf-5-buscar-producto-por-código-de-barras)
- [RF-6: Registrar Producto mediante Código de Barras](#rf-6-registrar-producto-mediante-código-de-barras)
- [RF-7: Registrar Venta](#rf-7-registrar-venta)
- [RF-8: Consultar Ventas](#rf-8-consultar-ventas)
- [RF-9: Agregar Producto a la Venta mediante Código de Barras](#rf-9-agregar-producto-a-la-venta-mediante-código-de-barras)
- [RF-10: Generar Comprobante de Venta](#rf-10-generar-comprobante-de-venta)
- [RF-11: User Authentication](#rf-11-user-authentication)
- [RF-12: Visualizar Producto](#rf-12-visualizar-producto)
- [RF-13: Consultar Venta](#rf-13-consultar-venta)
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

## RF-7: Registrar Venta

### Descripción
El sistema debe permitir al usuario registrar las ventas realizadas en el negocio, almacenando los datos generales de la venta y gestionando los productos incluidos durante el proceso de registro.

### Flujo Principal
1. El usuario accede a la sección de ventas.
2. El usuario solicita registrar una nueva venta.
3. El sistema crea una venta en curso.
4. El sistema inicializa una nueva venta en curso y muestra un detalle de venta vacío.
5. El sistema muestra la interfaz de selección de productos, que incluye:
   - Búsqueda de productos por nombre o código
   - Lista de productos con paginación
   - Opción para actualizar la lista de productos
6. El usuario busca y selecciona un producto de la lista de productos disponibles.
7. El sistema muestra la información del producto seleccionado:
   - Código del producto
   - Nombre del producto
   - Precio del producto
   - Stock disponible (mostrado con su unidad de medida, por ejemplo, "2,5 kg", "3 u")
8. El sistema asigna una cantidad predeterminada de 1 y permite al usuario modificarla.
9. El usuario confirma la adición del producto.
10. El sistema valida la cantidad y la disponibilidad del producto.
11. El sistema registra el producto en la venta.
12. Si el producto ya está incluido en la venta, el sistema incrementa su cantidad en lugar de crear una línea duplicada.
13. El sistema muestra la venta en curso actualizada.
14. El usuario puede repetir el proceso de adición de productos tantas veces como sea necesario.
15. El usuario puede eliminar cualquier producto previamente agregado a la venta.
16. El usuario confirma la venta.
17. El sistema registra automáticamente la fecha y hora actual de la venta.
18. El sistema registra la venta con los siguientes datos:
   - Identificador único de venta (generado automáticamente por el sistema)
   - Fecha de venta
   - Hora de venta
   - Importe total
   - Usuario que realizó la venta
19. El sistema almacena los detalles de venta asociados con los siguientes datos:
   - Identificador único del detalle de venta (generado automáticamente por el sistema)
   - Producto asociado
   - Venta asociada
   - Nombre del producto al momento de la venta
   - Precio al momento de la venta
   - Cantidad vendida
   - Unidad de medida al momento de la venta
   - Subtotal (calculado como cantidad × precio, no almacenado)
20. El sistema muestra una confirmación: "¿Desea imprimir el comprobante?"
21. El usuario confirma la impresión del comprobante, invocando la operación Generar Comprobante (RF-10).
22. El sistema completa el proceso de registro de venta y regresa a la sección de ventas.
23. El sistema muestra el mensaje: "Venta registrada correctamente".

### Flujos Alternativos

**5.a No hay productos disponibles**
5.a.1 El sistema detecta que no existen productos activos con stock disponible.
5.a.2 El sistema muestra el mensaje: "No se encontraron productos activos con stock disponible".

**5.b Paginación de productos**
5.b.1 El usuario navega entre las páginas de productos (por ejemplo, página siguiente o página anterior).
5.b.2 El sistema recupera la página correspondiente de productos disponibles.
5.b.3 El sistema muestra los productos pertenecientes a la página seleccionada.

**6.b Productos no encontrados**
6.b.1 El sistema detecta que ningún producto coincide con los criterios de búsqueda.
6.b.2 El sistema muestra el mensaje: "Ningún producto coincide con los criterios de búsqueda".

**10.a Cantidad inválida**
10.a.1 El sistema detecta que la cantidad ingresada es menor o igual a 0 o incompatible con la unidad de medida del producto.
10.a.2 El sistema muestra un mensaje de error indicando la corrección requerida.

**16.a Venta cancelada**
16.a.1 El usuario decide cancelar la venta antes de confirmarla.
16.a.2 El sistema descarta la venta en curso y regresa a la sección de ventas.

**16.b Venta sin productos**
16.b.1 El sistema detecta que la venta no tiene productos asociados.
16.b.2 El sistema muestra el mensaje: "La venta debe contener al menos un producto".

**16.c Producto no encontrado**
16.c.1 El sistema detecta que uno o más productos incluidos en la venta no existen.
16.c.2 El sistema muestra el mensaje: "No se encontró el producto con código '{productCode}'".

**16.d Producto inactivo**
16.d.1 El sistema detecta que uno o más productos incluidos en la venta tienen estado inactivo.
16.d.2 El sistema muestra el mensaje: "El producto '{productCode} - {productName}' está inactivo y no puede ser agregado a la venta".

**16.e Stock insuficiente**
16.e.1 El sistema detecta que uno o más productos incluidos en la venta tienen una cantidad solicitada superior al stock disponible.
16.e.2 El sistema muestra el mensaje: "Stock insuficiente para el producto {productCode} - {productName}".

**21.a Impresión de comprobante cancelada**
21.a.1 El usuario cancela la impresión del comprobante.
21.a.2 El sistema completa el proceso de venta y regresa a la sección de ventas sin generar el comprobante.

### Reglas de Negocio
- Una venta debe contener al menos un producto asociado para poder registrarse.
- Los productos solo pueden agregarse o eliminarse mientras la venta se encuentre en curso.
- Los productos con estado "Inactivo" no pueden agregarse a una venta.
- La cantidad debe ser mayor a 0 y compatible con la unidad de medida del producto.
- Si la unidad de medida es "Unidades", la cantidad no debe contener decimales.
- No se puede asignar una cantidad superior al stock disponible.
- Si un producto ya está incluido en la venta, su cantidad debe incrementarse en lugar de duplicar la línea.
- Si todos los productos son eliminados durante el proceso de registro, la venta en curso debe permanecer disponible hasta ser confirmada o cancelada.
- El sistema debe actualizar los subtotales de las líneas y el importe total de la venta cada vez que se agreguen, eliminen o modifiquen productos.
- Cada detalle de venta debe tener un identificador único generado automáticamente por el sistema.
- El identificador único de venta es asignado automáticamente por el sistema al momento de la confirmación.
- Cada venta debe estar asociada al usuario autenticado que la registró.
- El stock de los productos debe actualizarse después de que la venta sea confirmada, de acuerdo con las cantidades finales de la venta.
- La generación de comprobantes se realiza mediante la operación definida en RF-10.
- La interfaz de selección de productos debe permitir buscar productos por nombre o código.
- La interfaz de selección de productos debe admitir una paginación de 10 elementos por página de forma predeterminada.
- El sistema debe permitir la navegación entre páginas de productos (siguiente y anterior).
- Si no se especifica una página, el sistema debe devolver la primera página de productos de forma predeterminada.
- El sistema debe garantizar que únicamente los productos pertenecientes a la página solicitada sean recuperados desde la base de datos (paginación del lado del servidor).
- El sistema debe permitir actualizar la lista de productos para obtener datos actualizados.
- La interfaz de selección de productos solo debe mostrar productos activos con stock disponible mayor a 0.
- El sistema debe excluir de la lista de selección los productos inactivos y los productos con stock igual a 0.
- La interfaz de selección de productos debe indicar visualmente los productos con stock bajo para asistir al usuario durante el proceso de venta.
- La unidad de medida utilizada para cada producto debe almacenarse en el detalle de venta al momento de confirmar la venta.
- El detalle de venta almacena datos históricos del nombre, precio y unidad de medida del producto al momento de la venta. Los cambios posteriores en la información del producto no deben afectar las ventas previamente registradas.

---

## RF-8: Consultar Ventas

### Descripción
El sistema debe permitir al usuario consultar las ventas registradas en el sistema, mostrando los datos generales de cada venta y sus detalles.

### Flujo Principal
1. El usuario accede a la sección de ventas.
2. El usuario solicita consultar las ventas.
3. El sistema recupera una lista paginada de ventas desde la base de datos.
4. El sistema muestra las ventas registradas con los siguientes datos:
   - Identificador único de venta
   - Fecha y hora de venta
   - Nombre de usuario del vendedor (user_name)
   - Importe total

### Flujos Alternativos

**3.a No existen ventas registradas**
3.a.1 El sistema detecta que no existen ventas en la base de datos.
3.a.2 El sistema muestra el mensaje: "No se encontraron ventas".

**3.b Búsqueda de ventas**

**3.b.1 Por identificador de venta**
3.b.1.1 El usuario ingresa el identificador de la venta.
3.b.1.2 El sistema filtra la lista de ventas para mostrar la venta correspondiente.

**3.c Paginación de ventas**
3.c.1 El sistema permite navegar entre páginas de ventas (por ejemplo, página siguiente, página anterior o selección directa de página).

**3.d Ordenamiento de ventas**

**3.d.1 Por hora**
3.d.1.1 El usuario selecciona ordenar las ventas por hora ("Más recientes primero" o "Más antiguas primero").
3.d.1.2 El sistema ordena las ventas según el criterio seleccionado.

**3.e Filtrado de ventas**

**3.e.1 Por fecha**
3.e.1.1 El usuario selecciona una fecha específica.
3.e.1.2 El sistema filtra las ventas según la fecha seleccionada.

**3.f Ventas no encontradas**

**3.f.2 Por filtros**
3.f.2.1 El sistema detecta que ninguna venta coincide con los criterios aplicados.
3.f.2.2 El sistema muestra el mensaje: "Ninguna venta coincide con los criterios de búsqueda".

### Reglas de Negocio
- El sistema debe permitir consultar todas las ventas registradas mediante paginación.
- El sistema debe recuperar las ventas en páginas de 50 elementos por defecto.
- El sistema debe permitir la navegación entre páginas de ventas (por ejemplo, página siguiente, página anterior o selección directa de página).
- Si no se especifica una página, el sistema debe devolver la primera página por defecto.
- El sistema debe permitir buscar ventas por código de venta.
- El sistema debe permitir consultar ventas filtradas por una fecha específica (día, mes y año).
- El sistema debe permitir ordenar las ventas por hora ("Más recientes primero" o "Más antiguas primero").
- Si no se selecciona un filtro de fecha, el sistema utiliza por defecto la fecha actual (día, mes y año actuales).
- Si no se selecciona un criterio de ordenamiento por hora, el sistema ordena las ventas utilizando por defecto "Más recientes primero".
- El sistema debe garantizar que únicamente las ventas pertenecientes a la página solicitada sean recuperadas desde la base de datos (paginación del lado del servidor).

---

## RF-9: Agregar Producto a la Venta mediante Código de Barras

### Descripción
El sistema debe permitir agregar productos a la venta actual mediante el escaneo de códigos de barras, actuando como un acceso rápido para la selección de productos durante el proceso de registro de ventas.

### Flujo Principal

1. El usuario accede a la sección de ventas.
2. El usuario escanea el código de barras de un producto utilizando un lector de códigos de barras.
3. El sistema identifica el producto asociado al código de barras escaneado.
4. El sistema agrega el producto a la venta actual utilizando las mismas reglas definidas en el RF-7 para la adición de productos durante el registro de ventas.

### Flujos Alternativos

**3.a Producto no encontrado**
3.a.1 El sistema no encuentra un producto asociado al código de barras escaneado.
3.a.2 El sistema muestra el mensaje: "No se encontró el producto con código '{productCode}'".

**4.a No existe una venta en curso**
4.a.1 El sistema detecta que no existe una venta en curso.
4.a.2 El sistema crea automáticamente una venta en curso.
4.a.3 El sistema continúa con la adición del producto.

### Reglas de Negocio

- Un código de barras debe identificar de manera única a un producto dentro del sistema.
- El producto debe existir, estar activo y tener stock disponible mayor a 0 para poder ser agregado a una venta.
- Si no existe una venta en curso, el sistema debe crear una en estado "En curso".
- La adición de productos debe seguir las reglas definidas en el RF-7.
- El stock no se actualiza durante el registro de la venta, sino únicamente al confirmar la venta.

---

## RF-10: Generar Comprobante de Venta

### Descripción
El sistema debe permitir generar un comprobante de compra para cada venta registrada, representando el comprobante de la transacción y permitiendo su identificación única, incluyendo la información relevante de la venta y de los productos asociados.

### Flujo Principal

1. El usuario accede a la sección de ventas.
2. El usuario selecciona una venta registrada.
3. El usuario solicita imprimir el comprobante de venta.
4. El sistema recupera los datos generales de la venta.
5. El sistema recupera los detalles de venta asociados.
6. El sistema genera el comprobante de venta con la siguiente información:
   - Nombre del negocio
   - Dirección del negocio
   - Identificador único de venta
   - Fecha de emisión del comprobante
   - Hora de emisión del comprobante
   - Para cada producto incluido en la venta, se muestra la siguiente información:
     - Nombre del producto al momento de la venta
     - Cantidad junto con su unidad de medida al momento de la venta (por ejemplo, "2,5 kg", "1 u")
     - Precio al momento de la venta
     - Subtotal de cada producto
   - Importe total de la venta
7. El sistema genera e imprime el comprobante de venta utilizando la impresora configurada.

### Flujos Alternativos

**2.a Venta no encontrada**
2.a.1 El sistema detecta que la venta no existe.
2.a.2 El sistema muestra el mensaje: "No se encontró la venta con ID '{saleId}'".

**7.a Error al generar el comprobante**
7.a.1 El sistema detecta un error durante la generación o impresión del comprobante.
7.a.2 El sistema muestra el mensaje: "No se pudo generar el comprobante".

### Reglas de Negocio

- El comprobante solo puede generarse para ventas previamente registradas.
- La información del comprobante debe generarse exclusivamente a partir de los datos almacenados en la venta y sus detalles de venta.
- El comprobante debe reflejar el estado histórico de cada producto al momento de la venta (nombre, precio y unidad de medida).
- El subtotal de cada detalle de venta es calculado automáticamente por el sistema.
- El comprobante debe incluir todos los productos asociados a la venta.
- El comprobante constituye evidencia de la transacción realizada.
- Cada comprobante debe incluir un identificador único que permita rastrear y auditar la venta.

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

## RF-13: Consultar Venta

### Descripción
El sistema debe permitir al usuario consultar la información detallada de una venta específica registrada en el sistema.

### Flujo Principal
1. El usuario accede a la sección de ventas.
2. El usuario selecciona una venta específica.
3. El sistema recibe el identificador de la venta.
4. El sistema recupera los datos de la venta desde la base de datos.
5. El sistema muestra la información de la venta con los siguientes datos:
   - Identificador único de venta
   - Fecha de venta
   - Hora de venta
   - Nombre de usuario del vendedor
   - Importe total de la venta
   - Detalles de cada producto vendido, donde para cada producto se muestra la siguiente información:
     - Código del producto
     - Nombre del producto registrado al momento de la venta
     - Cantidad junto con la unidad de medida registrada al momento de la venta
     - Precio al momento de la venta
     - Subtotal

### Flujos Alternativos

**3.a Venta no encontrada**
3.a.1 El sistema detecta que la venta no existe.
3.a.2 El sistema muestra el mensaje: "No se encontró la venta con ID '{saleId}'".

### Reglas de Negocio
- El sistema debe permitir consultar una venta específica mediante su identificador.
- La información mostrada debe reflejar fielmente los datos almacenados de la venta y sus detalles.
- La cantidad de cada producto debe mostrarse junto con la unidad de medida almacenada en el detalle de venta.
- El código del producto se obtiene de la entidad de producto asociada.

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