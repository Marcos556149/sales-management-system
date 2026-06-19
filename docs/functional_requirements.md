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
- [RF-11: Autenticación de Usuarios](#rf-11-autenticación-de-usuarios)
- [RF-12: Visualizar Producto](#rf-12-visualizar-producto)
- [RF-13: Consultar Venta](#rf-13-consultar-venta)
- [RF-14: Modificar Configuración del Sistema](#rf-14-modificar-configuración-del-sistema)
- [RF-15: Visualizar Estadísticas de Ventas](#rf-15-visualizar-estadísticas-de-ventas)
- [RF-16: Cerrar Sesión](#rf-16-cerrar-sesión)
- [RF-17: Registrar Usuario](#rf-17-registrar-usuario)
- [RF-18: Visualizar Usuarios](#rf-18-visualizar-usuarios)
- [RF-19: Consultar Usuario](#rf-19-consultar-usuario)
- [RF-20: Modificar Usuario](#rf-20-modificar-usuario)
- [RF-21: Cambiar Estado de Usuario](#rf-21-cambiar-estado-de-usuario)
- [RF-22: Reactivar Producto](#rf-22-reactivar-producto)
- [RF-23: Generar Reporte de Estadísticas de Ventas (PDF)](#rf-23-generar-reporte-de-estadísticas-de-ventas-pdf)


### Reglas Generales
- [Reglas de Acceso al Sistema](#reglas-de-acceso-al-sistema)
  - [Tipos de Usuario](#tipos-de-usuario)
- [Datos Numéricos y Decimales](#datos-numéricos-y-decimales)
- [Formatos de Fecha y Hora](#formatos-de-fecha-y-hora)

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
   - Usuario que realizó la venta (administrador autenticado en el sistema al momento de la transacción)
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
2.a.2 El sistema muestra el mensaje: "No se encontró la venta con identificador '{saleId}'".

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

## RF-11: Autenticación de Usuarios

### Descripción
El sistema debe permitir que los usuarios accedan a sus funcionalidades mediante un proceso de autenticación basado en un nombre de usuario único y una contraseña asociados a una cuenta de usuario registrada.

### Flujo Principal
1. El usuario accede a la pantalla de inicio de sesión.  
2. El usuario ingresa su nombre de usuario único.  
3. El usuario ingresa su contraseña.  
4. El sistema valida que las credenciales ingresadas sean correctas.  
5. El sistema valida que el estado del usuario sea "Activo".  
6. El sistema determina el rol del usuario (`Administrador` u `Operador`) y concede acceso a las funcionalidades según los permisos asociados.

### Flujos Alternativos

**4.a Usuario o contraseña incorrectos**  
4.a.1 El sistema detecta que el nombre de usuario no existe o que la contraseña es incorrecta.  
4.a.2 El sistema muestra el mensaje: "Usuario o contraseña incorrectos."

**5.b Usuario Inactivo**  
5.b.1 El sistema detecta que el estado del usuario es "Suspendido" o "Eliminado".  
5.b.2 El sistema deniega el acceso.  
5.b.3 El sistema muestra el mensaje de error: "La cuenta de usuario no está activa".

### Reglas de Negocio
- El acceso al sistema requiere autenticación previa.  
- La contraseña ingresada debe coincidir con el nombre de usuario proporcionado.  
- El sistema determina automáticamente el rol del usuario a partir de su registro y restringe el acceso a las funcionalidades en consecuencia.
- Solo los usuarios con estado "Activo" pueden acceder al sistema.  
- A los usuarios con estado "Suspendido" o "Eliminado" se les debe denegar el acceso.
- El sistema debe aplicar la configuración de idioma preferida del usuario tras una autenticación exitosa.

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
3.a.2 El sistema muestra el mensaje: "No se encontró la venta con identificador '{saleId}'".

### Reglas de Negocio
- El sistema debe permitir consultar una venta específica mediante su identificador.
- La información mostrada debe reflejar fielmente los datos almacenados de la venta y sus detalles.
- La cantidad de cada producto debe mostrarse junto con la unidad de medida almacenada en el detalle de venta.
- El código del producto se obtiene de la entidad de producto asociada.

---

## RF-14: Modificar Configuración del Sistema

### Descripción
El sistema debe permitir gestionar la configuración global del sistema.

### Flujo Principal
1. El usuario accede a la sección de configuración del sistema.  
2. El sistema muestra los valores actuales de configuración:
   - Nombre del negocio
   - Dirección del negocio
3. El usuario modifica los valores de configuración.
   - Nombre del negocio y dirección del negocio (solo administradores)
4. El sistema valida los datos ingresados.  
5. El sistema aplica los cambios:
   - El nombre del negocio y la dirección del negocio se actualizan globalmente (si fueron modificados por un administrador).
6. El sistema confirma que la configuración ha sido actualizada correctamente.

### Flujo Alternativo

**4.a Datos inválidos**  
4.a.1 El sistema muestra un mensaje de error indicando los campos incorrectos.  
4.a.2 El usuario corrige los datos.

### Reglas de Negocio
- El nombre del negocio es obligatorio.
- El nombre del negocio es compartido por todos los usuarios.
- La dirección del negocio es obligatoria.
- La dirección del negocio es compartida por todos los usuarios.
- El sistema debe inicializar el nombre del negocio con un valor predeterminado ("My Business").
- El sistema debe inicializar la dirección del negocio con un valor predeterminado ("Business Address").
- Solo los administradores pueden modificar el nombre y la dirección del negocio.

---

## RF-15: Visualizar Estadísticas de Ventas

### Descripción
El sistema debe permitir al usuario consultar información estadística sobre ventas y productos en función de los filtros seleccionados.

### Flujo Principal
1. El usuario accede a la sección de estadísticas.  
2. El usuario selecciona los filtros deseados:
   - Usuario ("Todos los usuarios" o un usuario específico)
   - Rango de fechas (fecha de inicio y fecha de fin)
3. El usuario solicita la generación de estadísticas.
4. El sistema recupera de la base de datos los datos estadísticos agregados según los filtros seleccionados.
5. El sistema muestra las estadísticas divididas en las siguientes secciones:

   **Información de Ventas:**
   - Ingresos totales
   - Cantidad total de ventas
   - Valor promedio por venta
   - Hora con mayores ingresos
   - Hora con mayor cantidad de ventas
   - El sistema debe mostrar gráficos temporales de:
     - Ingresos totales a lo largo del tiempo
     - Cantidad de ventas a lo largo del tiempo

   **Información de Productos:**

   **Productos Vendidos:**
   - Top 10 de productos según cantidad vendida (gráfico), incluyendo para cada producto:
     - Código de producto
     - Nombre del producto
     - Cantidad vendida

   - Top 10 de productos según ingresos generados (gráfico), incluyendo para cada producto:
     - Código de producto
     - Nombre del producto
     - Ingresos generados

   - Lista de ranking de productos según los filtros seleccionados, incluyendo para cada producto:
     - Código de producto
     - Nombre del producto
     - Cantidad vendida
     - Ingresos generados

   **Productos No Vendidos:**
   - Una lista de productos sin ventas según los filtros seleccionados, incluyendo para cada producto:
     - Código de producto
     - Nombre del producto

### Flujos Alternativos

**4.a Sin datos disponibles**  
4.a.1 El sistema detecta que no existen datos que coincidan con los filtros seleccionados.  
4.a.2 El sistema muestra el mensaje: "No hay datos disponibles para los filtros seleccionados".

**5.a Sin productos no vendidos**  
5.a.1 El sistema detecta que no existen productos que cumplan con el criterio de productos no vendidos según los filtros seleccionados.  
5.a.2 El sistema muestra un mensaje en la sección de Productos No Vendidos: "No existen productos no vendidos para los filtros seleccionados".

**5.b Filtrar lista de ranking de productos vendidos**  
5.b.1 El usuario selecciona los filtros deseados:
   - Métrica ("Cantidad vendida" o "Ingresos generados")
   - Orden ("Más vendido → menos vendido" o "Menos vendido → más vendido")
5.b.2 El sistema recupera los productos correspondientes según los filtros seleccionados.

**5.c Paginación de productos**  
5.c.1 El sistema permite navegar entre las páginas de las listas de productos (por ejemplo, página siguiente, página anterior o selección directa de página).  
5.c.2 Esto aplica tanto para la lista de ranking de productos vendidos como para la lista de productos no vendidos.

### Reglas de Negocio

- El sistema debe permitir filtrar las estadísticas por usuario ("Todos los usuarios" o un usuario específico).
- El sistema debe permitir filtrar las estadísticas por rango de fechas (fecha de inicio y fecha de fin).
- El sistema debe calcular todos los valores estadísticos únicamente en función de los filtros seleccionados.

- El sistema debe calcular:
  - Los ingresos totales como la suma de los importes de todas las ventas.
  - La cantidad total de ventas como el conteo de registros de ventas.
  - El valor promedio por venta como los ingresos totales divididos por la cantidad total de ventas.

- El sistema debe determinar:
  - La hora con mayores ingresos.
  - La hora con mayor cantidad de ventas.

- El sistema debe mostrar gráficos temporales de:
  - Ingresos totales a lo largo del tiempo.
  - Cantidad de ventas a lo largo del tiempo.

- Los gráficos deben adaptar su granularidad temporal según el rango de fechas seleccionado:
  - Hora → cuando el rango seleccionado corresponde a un único día.
  - Día → para rangos de hasta 31 días.
  - Mes → para rangos de hasta 365 días.
  - Año → para rangos superiores a 365 días.

- El sistema debe mostrar únicamente los 10 productos principales en cada gráfico de productos vendidos:
  - Top 10 de productos según cantidad vendida.
  - Top 10 de productos según ingresos generados.

- El sistema debe permitir visualizar listas detalladas en la sección de estadísticas utilizando paginación tanto para la lista de ranking de productos vendidos como para la lista de productos no vendidos:
  - El sistema debe recuperar estas listas en páginas de 20 elementos de forma predeterminada.
  - El sistema debe garantizar la paginación del lado del servidor.
  - Si no se especifica una página, el sistema debe devolver la primera página por defecto para ambas listas paginadas de productos.
  - El sistema debe permitir navegar entre páginas para ambas listas.

- El sistema debe permitir ordenar la lista de ranking de productos por:
  - Cantidad vendida.
  - Ingresos generados.

- El sistema debe permitir definir el orden de la lista de ranking de productos:
  - Más vendido → menos vendido.
  - Menos vendido → más vendido.

- El sistema debe identificar como productos no vendidos a aquellos que no registren ventas dentro del rango de fechas seleccionado.

- Para los filtros globales de estadísticas:
  - Si no se selecciona un filtro de usuario, el sistema utilizará por defecto "Todos los usuarios".
  - Si no se selecciona un rango de fechas, el sistema utilizará la fecha actual.

- Para la lista de ranking de productos vendidos:
  - Si no se selecciona una métrica, el sistema utilizará por defecto "Ingresos generados".
  - Si no se selecciona un criterio de orden, el sistema utilizará por defecto "Más vendido → menos vendido".

---

## RF-16: Cerrar Sesión

### Descripción
El sistema debe permitir al usuario cerrar sesión de la cuenta que está utilizando actualmente.

### Flujo Principal
1. El usuario hace clic en el botón "Cerrar sesión".
2. El sistema muestra un mensaje de confirmación: "¿Está seguro de que desea cerrar sesión? Los cambios no guardados se perderán."
3. Si el usuario confirma, el sistema finaliza la sesión del usuario.
4. El sistema redirige al usuario a la pantalla de inicio de sesión.

### Reglas de Negocio
- El sistema debe garantizar que la sesión sea finalizada completamente.

---

## RF-17: Registrar Usuario

### Descripción
El sistema debe permitir a los administradores registrar nuevos usuarios con el rol Operador.

### Flujo Principal
1. El administrador accede a la sección de gestión de usuarios.  
2. El administrador selecciona la opción para registrar un nuevo usuario.  
3. El administrador ingresa los datos requeridos del usuario:
   - Nombre de usuario  
   - Contraseña  
4. El sistema valida que los datos ingresados sean correctos.  
5. El sistema verifica que el nombre de usuario sea único.  
6. El sistema asigna el rol "Operador" al nuevo usuario.  
7. El sistema asigna el estado "Activo" al nuevo usuario.  
8. El sistema asigna un identificador único (ID de Usuario) al nuevo usuario.  
9. El sistema almacena el usuario en la base de datos.  
10. El sistema confirma que el usuario ha sido registrado correctamente.

### Flujos Alternativos

**5.a Nombre de usuario ya existente**  
5.a.1 El sistema detecta que el nombre de usuario ya se encuentra registrado.  
5.a.2 El sistema muestra un mensaje de error: "El nombre de usuario ya existe".

**4.a Datos de entrada inválidos**  
4.a.1 El sistema detecta datos inválidos o incompletos.  
4.a.2 El sistema muestra un mensaje de error indicando los datos inválidos.

### Reglas de Negocio
- Solo los usuarios con el rol Administrador pueden realizar esta acción.  
- El nombre de usuario debe ser único dentro del sistema.  
- El sistema debe asignar el rol "Operador" por defecto.  
- El sistema debe asignar el estado "Activo" por defecto.  
- El sistema debe asignar un identificador único al usuario al momento de su creación.

---

## RF-18: Visualizar Usuarios

### Descripción
El sistema debe permitir a los administradores consultar la lista de usuarios registrados.

### Flujo Principal
1. El administrador accede a la sección de gestión de usuarios.  
2. El sistema obtiene la lista de usuarios desde la base de datos.  
3. El sistema muestra la lista de usuarios con su información principal:
   - Nombre de usuario  
   - Rol  
   - Estado  

### Flujos Alternativos

**2.a No se encontraron usuarios**  
2.a.1 El sistema detecta que no existen usuarios registrados.  
2.a.2 El sistema muestra el mensaje: "No se encontraron usuarios".

### Reglas de Negocio
- Solo los usuarios con el rol Administrador pueden realizar esta acción.  
- El sistema debe mostrar todos los usuarios con el rol "Operador", independientemente de su estado ("Activo", "Suspendido" o "Eliminado").

---

## RF-19: Consultar Usuario

### Descripción
El sistema debe permitir a los administradores consultar la información detallada de un usuario específico.

### Flujo Principal
1. El administrador accede a la sección de gestión de usuarios.  
2. El administrador selecciona un usuario específico de la lista.  
3. El sistema obtiene la información del usuario desde la base de datos.  
4. El sistema muestra los detalles del usuario, incluyendo:
   - Nombre de usuario  
   - Rol  
   - Estado  

### Flujos Alternativos

**3.a Usuario no encontrado**  
3.a.1 El sistema detecta que el usuario seleccionado no existe.  
3.a.2 El sistema muestra el mensaje: "Usuario no encontrado".

### Reglas de Negocio
- Solo los usuarios con el rol Administrador pueden realizar esta acción.  
- El sistema no debe mostrar la contraseña del usuario.  
- El sistema debe mostrar únicamente información no sensible del usuario.
- El sistema solo debe permitir consultar usuarios con rol "Operador".

---

## RF-20: Modificar Usuario

### Descripción
El sistema debe permitir a los administradores modificar la información de los usuarios.

### Flujo Principal
1. El administrador accede a la sección de gestión de usuarios.  
2. El administrador selecciona un usuario para modificar.  
3. El sistema muestra los datos actuales del usuario, excluyendo la contraseña.  
4. El administrador ingresa o mantiene el nombre de usuario actual y, opcionalmente, una nueva contraseña.
5. El sistema valida los datos actualizados.  
6. El sistema verifica que el nombre de usuario sea único dentro del sistema.
7. El sistema actualiza la información del usuario en la base de datos.  
8. El sistema confirma que el usuario ha sido actualizado correctamente.

### Flujos Alternativos

**3.a Usuario no encontrado**  
3.a.1 El sistema detecta que el usuario seleccionado no existe.  
3.a.2 El sistema muestra el mensaje: "Usuario no encontrado".

**5.a Datos de entrada inválidos**  
5.a.1 El sistema detecta datos inválidos o incompletos.  
5.a.2 El sistema muestra un mensaje de error indicando los datos inválidos.

**6.a Nombre de usuario ya existente**  
6.a.1 El sistema detecta que el nuevo nombre de usuario ya se encuentra en uso.  
6.a.2 El sistema muestra el mensaje: "El nombre de usuario '{username}' ya existe".

### Reglas de Negocio
- Solo los usuarios con el rol Administrador pueden realizar esta acción.  
- El nombre de usuario debe mantenerse único dentro del sistema.  
- El sistema no debe mostrar la contraseña actual del usuario.  
- Si se proporciona una nueva contraseña, esta debe reemplazar a la contraseña existente.
- Solo se pueden modificar usuarios con rol "Operador".
- El nombre de usuario es obligatorio.

---

## RF-21: Cambiar Estado de Usuario

### Descripción
El sistema debe permitir a los administradores cambiar el estado de un usuario.

### Flujo Principal
1. El administrador accede a la sección de gestión de usuarios.  
2. El administrador selecciona un usuario.  
3. El sistema muestra el estado actual del usuario.  
4. El administrador selecciona un nuevo estado:
   - Activo  
   - Suspendido  
   - Eliminado  
5. El sistema actualiza el estado del usuario en la base de datos.  
6. El sistema confirma que el estado del usuario ha sido actualizado correctamente.

### Flujos Alternativos

**3.a Usuario no encontrado**  
3.a.1 El sistema detecta que el usuario seleccionado no existe.  
3.a.2 El sistema muestra el mensaje: "Usuario no encontrado".

**5.a Estado igual al actual**
5.a.1 El sistema detecta que el usuario ya tiene el estado seleccionado.
5.a.2 El sistema muestra el mensaje: "El usuario ya tiene el estado 'userStatus'".

### Reglas de Negocio
- Solo los usuarios con el rol Administrador pueden realizar esta acción.  
- El sistema solo debe permitir los siguientes estados: "Activo", "Suspendido" y "Eliminado".  
- Los usuarios con estado "Eliminado" no deben ser eliminados físicamente de la base de datos.  
- Los usuarios con estado "Suspendido" o "Eliminado" no deben poder acceder al sistema.
- Solo se pueden modificar usuarios con rol Operador.

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

## RF-23: Generar Reporte de Estadísticas de Ventas (PDF)

### Descripción
El sistema debe permitir al usuario generar un reporte PDF descargable basado en las estadísticas de ventas y el rendimiento de productos, utilizando los mismos filtros y datos que el módulo de estadísticas.

### Flujo Principal
1. El usuario accede a la sección de estadísticas.
2. El usuario selecciona los filtros deseados:
   - Usuario ("Todos los Usuarios" o un usuario específico)
   - Rango de fechas (fecha de inicio y fecha de fin)
3. El usuario solicita generar un reporte PDF.
4. El usuario selecciona qué secciones incluir en el reporte:
   - Información de Ventas
   - Información de Productos
5. El sistema genera un reporte PDF que contiene únicamente las secciones seleccionadas.
6. El sistema proporciona el archivo PDF para su descarga.

### Reglas del Contenido del Reporte

El reporte debe incluir siempre:

- Título del reporte
- Usuario que genera el reporte (usuario administrador autenticado en el sistema al momento de la generación)
- Fecha y hora de generación del reporte
- Usuario seleccionado
- Rango de fechas seleccionado

Cada sección seleccionada debe incluir toda la información correspondiente.

**Información de Ventas:**
- Ingresos totales
- Cantidad total de ventas
- Valor promedio por venta
- Hora con mayores ingresos
- Hora con mayor cantidad de ventas

- Tabla de ingresos a lo largo del tiempo, incluyendo:
  - Período de tiempo
  - Ingresos generados

- Tabla de ventas a lo largo del tiempo, incluyendo:
  - Período de tiempo
  - Cantidad de ventas

**Información de Productos:**

**Productos Vendidos:**

- Top 10 productos por cantidad vendida, incluyendo:
  - Código del producto
  - Nombre del producto
  - Cantidad vendida

- Top 10 productos por ingresos generados, incluyendo:
  - Código del producto
  - Nombre del producto
  - Ingresos generados

- Lista de ranking de productos según los filtros seleccionados, incluyendo:
  - Métrica de ranking seleccionada
  - Orden de ranking seleccionado
  - Total de productos que coinciden con los filtros seleccionados
  - Cantidad de productos incluidos en el reporte

  Para cada producto:
  - Código del producto
  - Nombre del producto
  - Cantidad vendida
  - Ingresos generados

**Productos No Vendidos:**

- Lista de productos sin ventas según los filtros seleccionados, incluyendo:
  - Total de productos que coinciden con los filtros seleccionados
  - Cantidad de productos incluidos en el reporte

  Para cada producto:
  - Código del producto
  - Nombre del producto

- Si ningún producto coincide con el criterio, el sistema debe mostrar el mensaje:
  "No existen productos no vendidos para los filtros seleccionados".

### Flujos Alternativos

**4.a Generación del reporte cancelada**  
4.a.1 El usuario cancela el proceso de generación del reporte.  
4.a.2 El sistema regresa a la vista de estadísticas sin generar el PDF.

**4.b Ninguna sección seleccionada**  
4.b.1 El usuario no selecciona ninguna sección del reporte.  
4.b.2 El sistema muestra el mensaje:
       "Debe seleccionar al menos una sección".  
4.b.3 El sistema no genera el reporte.

**5.a No hay datos disponibles**  
5.a.1 El sistema detecta que no existen datos que coincidan con los filtros seleccionados.  
5.a.2 El sistema muestra el mensaje:
       "No hay datos disponibles para los filtros seleccionados".  
5.a.3 El sistema no genera el reporte.

### Reglas de Negocio

- El título del reporte debe ser "Reporte de Estadísticas de Ventas".
- Si la cantidad de productos disponibles es menor que el límite seleccionado para el reporte, el sistema debe incluir todos los productos disponibles.
- El usuario debe seleccionar al menos una sección antes de generar el reporte PDF.
- Las listas detalladas de productos incluidas en el reporte deben indicar:
  - Total de productos que coinciden con los filtros seleccionados
  - Cantidad de productos incluidos en el reporte
- El reporte debe generarse utilizando los filtros actualmente seleccionados en la sección de estadísticas.
- El sistema debe generar el reporte bajo demanda y no debe almacenarlo en el sistema.
- El usuario debe poder seleccionar qué secciones completas incluir en el reporte:
  - Información de Ventas
  - Información de Productos

- El sistema debe incluir la fecha y hora de generación del reporte en el PDF.

- El sistema debe garantizar que cada sección sea incluida en su totalidad (sin secciones parciales).

- El sistema debe permitir configurar la cantidad de productos incluidos en las listas detalladas de productos:
  - 10 elementos
  - 20 elementos
  - 50 elementos
  - 100 elementos

  Esta configuración se aplica de forma independiente a cada lista:
  - Lista de ranking de productos
  - Lista de productos sin ventas

- El sistema debe aplicar las selecciones predeterminadas al abrir el proceso de generación del reporte:
  - Todas las secciones del reporte seleccionadas por defecto:
    - Información de Ventas
    - Información de Productos

- El sistema debe aplicar los valores predeterminados para las listas detalladas de productos:
  - 20 elementos para la Lista de Ranking de Productos
  - 20 elementos para la Lista de Productos sin Ventas

- El sistema debe permitir configurar la Lista de Ranking de Productos incluida en el reporte según:
  - Cantidad Vendida
  - Ingresos Generados

- El sistema debe permitir ordenar la Lista de Ranking de Productos incluida en el reporte:
  - Más vendidos → menos vendidos
  - Menos vendidos → más vendidos

- Si no se selecciona una métrica para la Lista de Ranking de Productos, el sistema utilizará por defecto "Ingresos Generados".

- Si no se selecciona un criterio de ordenamiento para la Lista de Ranking de Productos, el sistema utilizará por defecto "Más vendido → menos vendido".

- El sistema debe garantizar que el PDF refleje exactamente los filtros seleccionados al momento de su generación.

- Las estadísticas de ingresos a lo largo del tiempo y cantidad de ventas a lo largo del tiempo incluidas en el reporte deben presentarse en formato tabular.



---

## Reglas Generales

### Reglas de Acceso al Sistema

#### Tipos de Usuario

**Administrador**
- Tiene acceso completo a todas las funcionalidades del sistema.  
- Puede registrar productos (RF-1).  
- Puede consultar productos (RF-2).  
- Puede modificar productos (RF-3).  
- Puede desactivar productos (RF-4).  
- Puede activar productos (RF-22).  
- Puede buscar productos por código de barras (RF-5).  
- Puede registrar productos mediante código de barras (RF-6).  
- Puede consultar un producto específico (RF-12).  
- Puede registrar ventas (RF-7).  
- Puede consultar ventas (RF-8).  
- Puede agregar productos a una venta mediante código de barras (RF-9).  
- Puede generar tickets de venta (RF-10).  
- Puede consultar una venta específica (RF-13).  
- Puede autenticarse en el sistema (RF-11).  
- Puede modificar la configuración del sistema, incluyendo el nombre y la dirección del negocio (RF-14).  
- Puede consultar estadísticas de ventas (RF-15).  
- Puede cerrar sesión en el sistema (RF-16).  
- Puede registrar usuarios (RF-17).  
- Puede consultar usuarios (RF-18).  
- Puede consultar un usuario específico (RF-19).  
- Puede modificar usuarios (RF-20).  
- Puede cambiar el estado de los usuarios (RF-21).  
- Puede generar reportes PDF de estadísticas de ventas (RF-23).

**Operador (Cajero)**
- Puede autenticarse en el sistema (RF-11).  
- Puede consultar productos (RF-2).  
- Puede buscar productos por código de barras (RF-5).  
- Puede consultar un producto específico (RF-12).  
- Puede registrar ventas (RF-7).  
- Puede consultar ventas (RF-8).  
- Puede agregar productos a una venta mediante código de barras (RF-9).  
- Puede generar tickets de venta (RF-10).  
- Puede consultar una venta específica (RF-13).  
- Puede cerrar sesión en el sistema (RF-16).  
- No puede registrar productos (RF-1).  
- No puede modificar productos (RF-3).  
- No puede desactivar productos (RF-4).  
- No puede activar productos (RF-22).  
- No puede registrar productos mediante código de barras (RF-6).  
- No puede modificar el nombre ni la dirección del negocio (RF-14).  
- No puede consultar estadísticas de ventas (RF-15).  
- No puede registrar usuarios (RF-17).  
- No puede consultar usuarios (RF-18).  
- No puede consultar un usuario específico (RF-19).  
- No puede modificar usuarios (RF-20).  
- No puede cambiar el estado de los usuarios (RF-21).  
- No puede generar reportes PDF de estadísticas de ventas (RF-23).

---

### Datos Numéricos y Decimales

- Todos los valores numéricos que representen importes monetarios o cantidades de productos deben almacenarse y mostrarse con un máximo de 2 decimales y hasta 10 dígitos en la parte entera.  
  Esto incluye:  
  - Precio del producto.  
  - Precio del producto en el detalle de venta (precio de venta).  
  - Subtotal de cada detalle de venta.  
  - Importe total de la venta.  
  - Stock del producto.  
  - Cantidad de producto en cada detalle de venta.  
  - Stock mínimo del producto.  

- Para los productos cuya unidad de medida sea "Unidades", el stock, la cantidad y el stock mínimo deben ser valores enteros, aunque el sistema soporte precisión decimal.

---

### Formatos de Fecha y Hora

- Todas las fechas del sistema deben mostrarse en formato `DD/MM/YYYY`.  
- Todas las horas del sistema deben mostrarse en formato de 24 horas con segundos `HH:MM:SS`.  
- Estos formatos se aplican tanto a la interfaz de usuario como a los reportes.  
- Los milisegundos almacenados en la base de datos no deben mostrarse en la interfaz de usuario ni en los reportes.