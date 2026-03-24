## Índice

### Requerimientos Funcionales
- [RF-1: Registrar Producto](#rf-1-registrar-producto)
- [RF-2: Consultar Productos](#rf-2-consultar-productos)
- [RF-3: Modificar Producto](#rf-3-modificar-producto)
- [RF-4: Dar de baja producto (baja lógica)](#rf-4-dar-de-baja-producto-baja-lógica)
- [RF-5: Buscar Producto por Código de Barras](#rf-5-buscar-producto-por-código-de-barras)
- [RF-6: Registrar Producto por Código de Barras](#rf-6-registrar-producto-por-código-de-barras)
- [RF-7: Registrar Venta](#rf-7-registrar-venta)
- [RF-8: Consultar Ventas](#rf-8-consultar-ventas)
- [RF-9: Modificar Venta](#rf-9-modificar-venta)
- [RF-10: Eliminar Venta](#rf-10-eliminar-venta)
- [RF-11: Asociar Producto a Venta mediante Código de Barras](#rf-11-asociar-producto-a-venta-mediante-código-de-barras)
- [RF-12: Asociar Producto a Venta](#rf-12-asociar-producto-a-venta)
- [RF-13: Desvincular Producto de Venta](#rf-13-desvincular-producto-de-venta)
- [RF-14: Generar Ticket de Venta](#rf-14-generar-ticket-de-venta)
- [RF-15: Autenticación de Usuario](#rf-15-autenticación-de-usuario)
- [RF-16: Actualizar Lista de Productos / Ventas](#rf-16-actualizar-lista-de-productos--ventas)
- [RF-17: Consultar Producto](#rf-17-consultar-producto)
- [RF-18: Consultar Venta](#rf-18-consultar-venta)
- [RF-19: Cambiar Idioma de la Interfaz](#rf-19-cambiar-idioma-de-la-interfaz)

### Reglas Generales
- [Reglas de Acceso al Sistema](#reglas-de-acceso-al-sistema)
- [Datos de usuario almacenados](#datos-de-usuario-almacenados)

## RF-1: Registrar Producto

### Descripción
El sistema debe permitir al usuario registrar un nuevo producto.

### Flujo principal
1. El usuario accede a la sección de productos.  
2. El usuario solicita registrar un nuevo producto.  
3. El sistema solicita los siguientes datos del producto: código, nombre, precio, unidad de medida, estado y stock disponible.
4. El usuario ingresa los datos requeridos.  
5. El sistema valida la información ingresada.  
6. El sistema guarda el producto en la base de datos.  
7. El sistema confirma el registro exitoso mediante un mensaje de confirmación: "Producto registrado exitosamente".  

### Flujos alternativos

**5.a Datos inválidos**  
5.a.1 El sistema muestra un mensaje de error indicando los campos incorrectos.  
5.a.2 El usuario corrige los datos.  

**5.b Producto existente en la base de datos**  
5.b.1 El sistema detecta que ya existe un producto con el mismo código.  
5.b.2 El sistema muestra un mensaje de error: "El producto ya está registrado".  

### Reglas de negocio
- El código del producto es obligatorio y debe ser único dentro del sistema.  
- El nombre del producto es obligatorio.  
- El estado del producto es obligatorio y, por defecto, es "Activo".  
- El precio del producto es obligatorio y debe ser un número real mayor o igual a 0.  
- El stock disponible es obligatorio y debe ser un número real mayor o igual a 0.
- El sistema debe asignar, por defecto, un valor de 0 al precio y al stock al momento de crear el producto.
- El nombre del producto debe ser descriptivo y permitir diferenciarlo claramente de otros productos similares dentro del sistema.
- La unidad de medida del producto es obligatoria (por ejemplo: unidad, kilogramo, litro).
- Si la unidad de medida es "unidad", el stock debe expresarse en valores enteros.

## RF-2: Consultar Productos

### Descripción
El sistema debe permitir al usuario consultar los productos registrados en el sistema.

### Flujo principal
1. El usuario accede a la sección de productos.  
2. El usuario solicita consultar productos.  
3. El sistema recupera la lista de productos de la base de datos.  
4. El sistema muestra los productos registrados con los siguientes datos:
   - Código del producto  
   - Nombre del producto  
   - Precio  
   - Estado  
   - Stock disponible (mostrado junto a su unidad de medida, por ejemplo: "2.5 kg", "3 unidades")

### Flujos alternativos

**3.a No existen productos registrados**  
3.a.1 El sistema detecta que no hay productos en la base de datos.  
3.a.2 El sistema muestra un mensaje: "No hay productos registrados".  

**3.b Buscadores de productos**  

**3.b.1 Por nombre**  
3.b.1.1 El usuario ingresa un nombre o parte del nombre del producto.  
3.b.1.2 El sistema filtra los productos que coinciden con el nombre ingresado.  

**3.b.2 Por código**  
3.b.2.1 El usuario ingresa un código o parte del código del producto.  
3.b.2.2 El sistema filtra los productos que coinciden con el código ingresado.  

**3.c Ordenamientos de productos**  

**3.c.1 Por nombre**  
3.c.1.1 El usuario selecciona un criterio de ordenamiento por nombre (ascendente o descendente).  
3.c.1.2 El sistema ordena los productos según el criterio seleccionado.  

**3.d Filtros de productos**  

**3.d.1 Por estado**  
3.d.1.1 El usuario selecciona un estado del producto (Activo, Inactivo o Todos).  
3.d.1.2 El sistema filtra los productos según el estado seleccionado.  

**3.f Producto no encontrado**  
3.f.1 El sistema detecta que no existen productos que coincidan con el criterio de búsqueda.  
3.f.2 El sistema muestra un mensaje: "No se encontraron productos".  

### Reglas de negocio
- El sistema debe permitir consultar todos los productos registrados.  
- El sistema debe permitir buscar productos por nombre o código.  
- El sistema debe permitir ordenar los productos por nombre (ascendente o descendente).  
- El sistema debe permitir filtrar los productos por estado (Activo, Inactivo o Todos).  
- Si el usuario no selecciona ningún filtro por estado, el sistema muestra por defecto "Todos".  
- Si el usuario no selecciona ningún ordenamiento por nombre, el sistema aplica por defecto "Ascendente".  

## RF-3: Modificar Producto

### Descripción
El sistema debe permitir al usuario modificar los datos de un producto existente.

### Flujo principal
1. El usuario accede a la sección de productos.  
2. El usuario selecciona un producto.  
3. El sistema muestra los datos actuales del producto.  
4. El usuario solicita modificar el producto.  
5. El usuario modifica los siguientes datos del producto: código, nombre, precio, unidad de medida y stock disponible.
6. El sistema valida la información ingresada.  
7. El sistema actualiza los datos del producto en la base de datos.  
8. El sistema muestra un mensaje de confirmación: "Producto actualizado exitosamente".  

### Flujos alternativos

**2.a Producto no encontrado**  
2.a.1 El sistema detecta que el producto seleccionado no existe.  
2.a.2 El sistema muestra un mensaje: "El producto no existe".  

**6.a Datos inválidos**  
6.a.1 El sistema muestra un mensaje de error indicando los campos incorrectos.  
6.a.2 El usuario corrige los datos.  

**6.b Código de producto duplicado**  
6.b.1 El sistema detecta que el código ingresado ya pertenece a otro producto.  
6.b.2 El sistema muestra un mensaje de error: "El código de producto ya está en uso".  

### Reglas de negocio
- El código del producto debe ser único dentro del sistema.  
- Todos los campos editables (código, nombre, precio, unidad de medida y stock) son obligatorios.
- El precio del producto debe ser un número real mayor o igual a 0.  
- El stock disponible debe ser un número real mayor o igual a 0.
- El estado del producto no puede ser modificado desde este proceso.  
- El código del producto puede ser modificado, siempre que no esté en uso por otro producto.
- El nombre del producto debe ser descriptivo y permitir diferenciarlo claramente de otros productos similares dentro del sistema.
- La unidad de medida del producto es obligatoria.
- Si la unidad de medida es "unidad", el stock debe expresarse en valores enteros.
- Si se modifica la unidad de medida, el sistema debe validar que el stock cumpla con las restricciones de la nueva unidad.

## RF-4: Dar de baja producto (baja lógica)

### Descripción
El sistema debe permitir al usuario dar de baja lógica a un producto, marcándolo como inactivo.

### Flujo principal
1. El usuario accede a la sección de productos.  
2. El usuario selecciona un producto.  
3. El sistema muestra los datos del producto.  
4. El usuario solicita dar de baja el producto.  
5. El sistema solicita confirmación de la acción.  
6. El usuario confirma la operación.  
7. El sistema actualiza el estado del producto a "Inactivo".  
8. El sistema muestra un mensaje de confirmación: "Producto dado de baja exitosamente".  

### Flujos alternativos

**2.a Producto no encontrado**  
2.a.1 El sistema detecta que el producto seleccionado no existe.  
2.a.2 El sistema muestra un mensaje: "El producto no existe".  

**5.a Cancelación de la operación**  
5.a.1 El usuario cancela la operación.  
5.a.2 El sistema no realiza cambios en el producto.  

**7.a Producto ya inactivo**  
7.a.1 El sistema detecta que el producto ya se encuentra inactivo.  
7.a.2 El sistema muestra un mensaje: "El producto ya está inactivo".  

### Reglas de negocio
- La baja de un producto es lógica, no física.  
- El estado del producto puede ser "Activo" o "Inactivo".  
- Un producto inactivo no debe estar disponible para operaciones de venta.  

## RF-5: Buscar Producto por Código de Barras

### Descripción
El sistema debe permitir al usuario identificar un producto existente mediante la lectura de su código de barras.

### Flujo principal
1. El usuario accede a la sección de productos.  
2. El usuario escanea el código de barras del producto con el lector.  
3. El sistema reconoce el código de barras.  
4. El sistema muestra los datos del producto identificado mediante la operación de consultar producto (RF-17): código, nombre, precio, unidad de medida, estado y stock disponible.

### Flujos alternativos

**3.a Código de barras ilegible o no reconocido**  
3.a.1 El sistema detecta que el código de barras no puede ser leído.  
3.a.2 El sistema muestra un mensaje: "Código de barras no reconocido, por favor intente nuevamente".  

**4.a Producto no encontrado**  
4.a.1 El sistema detecta que el producto con ese código no existe.  
4.a.2 El sistema ofrece al usuario la opción de registrar un nuevo producto utilizando el código de barras escaneado (ver RF-6: Registrar Producto por Código de Barras).  

### Reglas de negocio
- El código de barras debe ser único dentro del sistema.  
- El producto debe existir previamente en la base de datos para ser identificado.  

## RF-6: Registrar Producto por Código de Barras

### Descripción
El sistema permite al usuario iniciar el registro de un producto utilizando un código de barras escaneado como identificador. Esta funcionalidad complementa el RF-1: Registrar Producto.

### Flujo principal
1. El usuario accede a la sección de registro de productos.  
2. El usuario escanea el código de barras del producto con el lector.  
3. El sistema redirige al RF-1: Registrar Producto para completar el registro.  

### Flujos alternativos

**3.a Código de barras ya registrado**  
3.a.1 El sistema detecta que el código de barras escaneado ya pertenece a otro producto.  
3.a.2 El sistema ofrece al usuario la opción de visualizar los datos del producto utilizando el código de barras escaneado (ver RF-5: Buscar Producto por Código de Barras).  

### Reglas de negocio
- El registro completo del producto sigue las reglas definidas en RF-1: Registrar Producto.  

## RF-7: Registrar Venta

### Descripción
El sistema debe permitir al usuario registrar las ventas realizadas en el negocio, almacenando los datos generales de la venta y gestionando los productos asociados mediante operaciones sobre los detalles de venta.

### Flujo principal
1. El usuario accede a la sección de ventas.  
2. El usuario solicita registrar una nueva venta.  
3. El sistema crea una venta en proceso.  
4. El usuario agrega productos a la venta mediante la operación de asociar producto a venta (RF-12).  
5. El sistema muestra, por cada producto agregado, su nombre, precio y cantidad junto con su unidad de medida.
6. El sistema calcula los subtotales de cada línea y el total de la venta.  
7. El sistema ingresa automáticamente la fecha y hora actual en la que se realiza la venta.  
8. El usuario confirma la venta.  
9. El sistema registra la venta con los siguientes datos:
   - Identificador único de la venta (generado automáticamente por el sistema)  
   - Fecha de la venta  
   - Hora de la venta  
   - Total de la venta
   - Usuario que realizó la venta  
10. El sistema registra los detalles de venta asociados a la venta.  
11. El sistema muestra un mensaje de confirmación: "Venta registrada exitosamente, ¿Desea imprimir el ticket de la venta?".  
12. El usuario confirma la impresión del ticket, invocando la operación de generar ticket de venta (RF-14).
13. El sistema vuelve automáticamente a la sección de ventas.

### Flujos alternativos

**4.a Error al asociar producto**  
4.a.1 El sistema detecta un error durante la asociación del producto (según RF-12).  
4.a.2 El sistema muestra el mensaje correspondiente y permite continuar la operación.

**8.a Venta cancelada**  
8.a.1 El usuario decide cancelar la venta antes de confirmarla.  
8.a.2 El sistema descarta la venta en proceso y vuelve a la sección de ventas.

**12.a Impresión de ticket cancelada**  
12.a.1 El usuario cancela la impresión del ticket de la venta.  
12.a.2 El sistema finaliza la operación de venta y vuelve a la sección de ventas sin generar el ticket.

### Reglas de negocio
- La venta debe tener como mínimo un producto asociado para poder ser registrada.  
- Los productos se agregan a la venta mediante la operación definida en RF-12.  
- El cálculo de subtotales y total de la venta se realiza automáticamente.  
- Mientras una venta se encuentra en proceso de registro, puede existir sin productos asociados.  
- Si durante el proceso de registro se eliminan todos los productos de la venta, la venta no debe eliminarse automáticamente.  
- Durante el proceso de registro, el usuario puede modificar la cantidad de los productos agregados a la venta.  
- Si durante el registro de la venta la cantidad de un producto aumenta, el sistema descontará del stock la diferencia correspondiente.  
- Si durante el registro de la venta la cantidad de un producto disminuye o se elimina un detalle, el sistema aumentará del stock la diferencia correspondiente.
- Durante el proceso de registro, el usuario puede eliminar productos (detalles de venta) de la venta mediante la operación definida en RF-13.  
- El identificador único de la venta se asigna automáticamente por el sistema.
- Cada venta queda asociada al usuario que la registró.
- La generación del ticket de venta se realiza mediante la operación definida en RF-14.

## RF-8: Consultar Ventas

### Descripción
El sistema debe permitir al usuario consultar las ventas registradas en el sistema, visualizando los datos generales de cada venta y sus detalles.

### Flujo principal
1. El usuario accede a la sección de ventas.  
2. El usuario solicita consultar las ventas.  
3. El sistema recupera la lista de ventas de la base de datos.  
4. El sistema muestra las ventas registradas con los siguientes datos:
   - Identificador único de la venta  
   - Fecha y hora de la venta
   - Nombre de usuario (user_name) del vendedor
   - Total de la venta  

### Flujos alternativos

**3.a No existen ventas registradas**  
3.a.1 El sistema detecta que no hay ventas en la base de datos.  
3.a.2 El sistema muestra un mensaje: "No hay ventas registradas".

**3.b Ordenamientos de ventas**

**3.b.1 Por día**  
3.b.1.1 El usuario selecciona ordenar las ventas por día (Ascendente o Descendente).  
3.b.1.2 El sistema ordena las ventas según el criterio seleccionado.  

**3.b.2 Por mes**  
3.b.2.1 El usuario selecciona ordenar las ventas por mes (Ascendente o Descendente).  
3.b.2.2 El sistema ordena las ventas según el criterio seleccionado.  

**3.b.3 Por año**  
3.b.3.1 El usuario selecciona ordenar las ventas por año (Ascendente o Descendente).  
3.b.3.2 El sistema ordena las ventas según el criterio seleccionado.  

**3.b.4 Por hora**  
3.b.4.1 El usuario selecciona ordenar las ventas por hora (Ascendente o Descendente).  
3.b.4.2 El sistema ordena las ventas según el criterio seleccionado.  

**3.c Filtros de ventas**

**3.c.1 Por día**  
3.c.1.1 El usuario selecciona un día específico o la opción "Todos".  
3.c.1.2 El sistema filtra las ventas según el día seleccionado.  

**3.c.2 Por mes**  
3.c.2.1 El usuario selecciona un mes específico o la opción "Todos".  
3.c.2.2 El sistema filtra las ventas según el mes seleccionado.  

**3.c.3 Por año**  
3.c.3.1 El usuario selecciona un año específico o la opción "Todos".  
3.c.3.2 El sistema filtra las ventas según el año seleccionado.  

**3.d Venta no encontrada**  
3.d.1 El sistema detecta que no existen ventas que coincidan con los criterios aplicados.  
3.d.2 El sistema muestra un mensaje: "No se encontraron ventas".

### Reglas de negocio
- El sistema debe permitir consultar todas las ventas registradas.  
- El sistema debe permitir ordenar las ventas por día, mes, año y hora, en forma ascendente o descendente.  
- El sistema debe permitir filtrar las ventas por día, mes y año.  
- En los filtros por día, mes y año debe existir la opción "Todos" para indicar que no se desea aplicar dicho filtro.  
- Si el usuario no selecciona ningún filtro por día, el sistema utiliza por defecto el día actual.  
- Si el usuario no selecciona ningún filtro por mes, el sistema utiliza por defecto el mes actual.  
- Si el usuario no selecciona ningún filtro por año, el sistema utiliza por defecto el año actual.  
- Si el usuario no selecciona ningún criterio de ordenamiento por día, el sistema ordena por defecto las ventas por día en orden descendente.  
- Si el usuario no selecciona ningún criterio de ordenamiento por mes, el sistema ordena por defecto las ventas por mes en orden descendente.  
- Si el usuario no selecciona ningún criterio de ordenamiento por año, el sistema ordena por defecto las ventas por año en orden descendente.  
- Si el usuario no selecciona ningún criterio de ordenamiento por hora, el sistema ordena por defecto las ventas por hora en orden descendente.  

## RF-9: Modificar Venta

### Descripción
El sistema debe permitir al usuario modificar una venta existente mediante la actualización de los productos incluidos en la venta y sus cantidades.

### Flujo principal
1. El usuario accede a la sección de ventas.  
2. El usuario selecciona una venta existente.  
3. El sistema muestra los datos actuales de la venta y sus detalles, incluyendo la cantidad de cada producto junto con su unidad de medida.
4. El usuario modifica la cantidad de uno o más productos de la venta, respetando las reglas de cantidad según la unidad de medida del producto.
5. El sistema recalcula automáticamente los subtotales de cada línea y el total de la venta.  
6. El usuario confirma los cambios.  
7. El sistema actualiza los datos de la venta en la base de datos.  
8. El sistema actualiza el stock de los productos afectados.  
9. El sistema muestra un mensaje de confirmación: "Venta actualizada exitosamente".

### Flujos alternativos

**2.a Venta no encontrada**  
2.a.1 El sistema detecta que la venta seleccionada no existe.  
2.a.2 El sistema muestra un mensaje: "La venta no existe".

**4.a Cantidad inválida**  
4.a.1 El sistema detecta que la cantidad ingresada es menor o igual a 0 o mayor al stock disponible (en caso de aumento).  
4.a.2 El sistema muestra un mensaje de error indicando la corrección necesaria.

**4.b Detalle de venta no encontrado**  
4.b.1 El sistema detecta que el producto seleccionado para modificar ya no existe en la venta.  
4.b.2 El sistema muestra un mensaje: "El producto no está asociado a esta venta" y no realiza cambios para ese producto.

**6.a Modificación cancelada**  
6.a.1 El usuario cancela la modificación de la venta.  
6.a.2 El sistema no realiza cambios y vuelve a la sección de ventas.

### Reglas de negocio
- Solo se permite modificar la cantidad de los productos en la venta.  
- No se pueden modificar manualmente el identificador, la fecha, la hora ni el total de la venta.  
- El precio del producto en la venta no debe modificarse.  
- El subtotal de cada línea se calcula automáticamente.  
- El total de la venta se recalcula automáticamente en base a los cambios realizados.  
- No se puede asignar una cantidad mayor al stock disponible cuando la modificación implique un aumento.  
- La cantidad debe ser un número real mayor a 0. 
- El stock de los productos debe actualizarse en función de los cambios realizados.  
- Si la cantidad de un producto en la venta aumenta, el sistema debe descontar del stock la diferencia correspondiente.  
- Si la cantidad de un producto en la venta disminuye, el sistema debe aumentar en el stock la diferencia correspondiente.  
- Si la cantidad de un producto en la venta se mantiene sin cambios, el sistema no debe modificar el stock del producto.
- La cantidad de cada producto modificada debe ser válida según la unidad de medida del producto asociado.
- Si la unidad de medida del producto es "unidad", la cantidad no debe contener decimales.  

## RF-10: Eliminar Venta

### Descripción
El sistema debe permitir al usuario eliminar una venta existente, eliminando también todos los detalles de venta asociados.

### Flujo principal
1. El usuario accede a la sección de ventas.  
2. El usuario selecciona una venta existente.  
3. El sistema muestra los datos de la venta y sus detalles.  
4. El usuario solicita eliminar la venta.  
5. El sistema solicita confirmación de la acción.  
6. El usuario confirma la eliminación.  
7. El sistema elimina la venta y todos sus detalles asociados de la base de datos.  
8. El sistema muestra un mensaje de confirmación: "Venta eliminada exitosamente".

### Flujos alternativos

**2.a Venta no encontrada**  
2.a.1 El sistema detecta que la venta seleccionada no existe.  
2.a.2 El sistema muestra un mensaje: "La venta no existe".

**5.a Cancelación de la eliminación**  
5.a.1 El usuario cancela la operación.  
5.a.2 El sistema no realiza cambios y vuelve a la sección de ventas.

### Reglas de negocio
- La eliminación de una venta implica la eliminación de todos sus detalles asociados.  
- La eliminación de la venta es física (se elimina de la base de datos).  
- La eliminación de una venta ajustará automáticamente el stock de los productos involucrados, aumentando la cantidad correspondiente según los detalles de venta eliminados.

## RF-11: Asociar Producto a Venta mediante Código de Barras

### Descripción
El sistema debe permitir agilizar la selección de productos en el proceso de venta mediante la lectura del código de barras, agregando productos a la venta actual o iniciando una nueva venta automáticamente.  
Esta operación utiliza RF-12 para asociar el producto a la venta una vez identificado.

### Flujo principal
1. El usuario accede a la sección de ventas.  
2. El usuario utiliza un lector de código de barras para escanear un producto.  
3. El sistema identifica el producto correspondiente al código leído.  
4. Se utiliza la operación definida en RF-12 para agregar el producto a la venta, incluyendo la cantidad y la actualización de stock.  

### Flujos alternativos

**2.a Producto no encontrado**  
2.a.1 El sistema no encuentra un producto asociado al código de barras leído.  
2.a.2 El sistema muestra un mensaje: "Producto no encontrado".

**3.a Venta no iniciada**  
3.a.1 El sistema detecta que no existe una venta en curso.  
3.a.2 El sistema inicia automáticamente una nueva venta.  
3.a.3 El sistema continúa con el paso 4 del flujo principal.  

### Reglas de negocio
- La lectura del código de barras debe permitir identificar un único producto.  
- La adición del producto a la venta se realiza mediante la operación RF-12, respetando todas sus reglas de negocio (cantidad mínima, stock disponible, incremento de cantidad si ya existe, asignación de cantidad por defecto, identificador de detalle generado automáticamente).  
- Si no existe una venta en curso, el sistema debe iniciar una nueva automáticamente.  

## RF-12: Asociar Producto a Venta

### Descripción
El sistema debe permitir asociar un producto a una venta, especificando la cantidad deseada, independientemente de si la venta se encuentra en proceso de registro o ya ha sido registrada.

### Flujo principal
1. El usuario proporciona una venta.  
2. El usuario selecciona un producto.  
3. El sistema muestra la información del producto.  
4. El sistema solicita al usuario la cantidad de unidades a agregar, mostrando la unidad de medida del producto para mayor claridad.
5. El usuario ingresa la cantidad deseada.  
6. El sistema valida la cantidad ingresada, asegurándose de que cumpla las reglas de la unidad de medida del producto.
7. El sistema asocia el producto a la venta.  
8. El sistema actualiza el stock del producto en función de la cantidad agregada.  
9. El sistema registra el detalle de venta con los siguientes datos:  
   - Identificador único del detalle de venta (generado automáticamente por el sistema)  
   - Producto asociado  
   - Venta asociada  
   - Cantidad agregada  
   - Precio unitario  
   *Nota:* El subtotal del detalle se calcula automáticamente como `cantidad × precio unitario` y *no se almacena*.  
10. El sistema refleja el producto en el detalle de la venta.

### Flujos alternativos

**1.a Venta no válida**  
1.a.1 El sistema detecta que la venta no existe o no es válida.  
1.a.2 El sistema muestra un mensaje: "Venta no válida".

**2.a Producto no encontrado**  
2.a.1 El sistema no encuentra el producto seleccionado.  
2.a.2 El sistema muestra un mensaje: "Producto no encontrado".

**5.a Cantidad inválida**
5.a.1 El sistema detecta que la cantidad ingresada es menor o igual a 0, mayor al stock disponible, o no es compatible con la unidad de medida del producto (por ejemplo, un número decimal para productos vendidos por unidad).  
5.a.2 El sistema muestra un mensaje de error indicando la corrección necesaria.

**7.a Producto ya asociado a la venta**  
7.a.1 El sistema detecta que el producto ya se encuentra asociado a la venta.  
7.a.2 El sistema incrementa la cantidad del producto en la venta.  
7.a.3 El sistema continúa con el flujo principal.

### Reglas de negocio
- La venta debe existir como entidad válida en el sistema (en proceso o registrada).  
- El producto debe existir en el sistema.   
- No se puede agregar una cantidad mayor al stock disponible.  
- Si el producto ya está asociado a la venta, se debe incrementar su cantidad en lugar de duplicar el registro.  
- Los productos con estado "Inactivo" no podrán ser añadidos a la venta.  
- Por defecto, al asociar un producto a la venta, el sistema asigna una cantidad inicial de 1 unidad, la cual el usuario puede modificar antes de confirmar.  
- El identificador único del detalle de venta es asignado automáticamente por el sistema.
- La cantidad debe ser un número mayor a 0 y compatible con la unidad de medida del producto.
- Si la unidad de medida del producto es "unidad", la cantidad no debe contener decimales.

## RF-13: Desvincular Producto de Venta

### Descripción
El sistema debe permitir desvincular un producto de una venta, eliminando el detalle de venta correspondiente.

### Flujo principal
1. El usuario proporciona una venta.  
2. El usuario selecciona un producto asociado a la venta.  
3. El sistema identifica el detalle de venta correspondiente.  
4. El usuario solicita eliminar el producto de la venta.  
5. El sistema elimina el detalle de venta.  
6. El sistema actualiza la información de la venta.  
7. El sistema muestra la venta actualizada.

### Flujos alternativos

**1.a Venta no válida**  
1.a.1 El sistema detecta que la venta no existe o no es válida.  
1.a.2 El sistema muestra un mensaje: "Venta no válida".

**2.a Producto no asociado**  
2.a.1 El sistema detecta que el producto no está asociado a la venta.  
2.a.2 El sistema muestra un mensaje: "El producto no está asociado a la venta".

**4.a Operación cancelada**  
4.a.1 El usuario cancela la operación.  
4.a.2 El sistema no realiza cambios.

### Reglas de negocio
- La venta debe existir como entidad válida en el sistema (en proceso o registrada).  
- El producto debe estar previamente asociado a la venta.  
- Si la venta está en proceso de registro, la eliminación de un detalle de venta ajustará automáticamente el stock aumentando la cantidad correspondiente.  
- Si la venta ya ha sido registrada, la eliminación de un detalle de venta también ajustará automáticamente el stock aumentando la cantidad correspondiente.
- Si la venta se encuentra en proceso de registro y se eliminan todos sus productos, la venta no debe eliminarse automáticamente.  
- Si la venta ya ha sido registrada y se eliminan todos sus productos, la venta debe eliminarse automáticamente.

## RF-14: Generar Ticket de Venta

### Descripción
El sistema debe permitir generar un ticket de compra para cada venta registrada, el cual represente el comprobante de la operación realizada e incluya la información correspondiente a la venta y sus productos asociados.

### Flujo principal
1. El sistema recibe una venta registrada.  
2. El sistema obtiene los datos generales de la venta.  
3. El sistema obtiene los detalles de venta asociados.  
4. El sistema genera el ticket de venta con la siguiente información:
   - Nombre del negocio  
   - Fecha de emisión del ticket  
   - Hora de emisión del ticket  
   - Nombre de cada producto vendido
   - Cantidad de cada producto junto con su unidad de medida (por ejemplo: "2.5 kg", "1 unidad") 
   - Precio unitario  
   - Subtotal de cada producto  
   - Total de la venta  
5. El sistema muestra el ticket generado o lo envía al medio de salida correspondiente (pantalla o impresora).

### Flujos alternativos

**1.a Venta no válida**  
1.a.1 El sistema detecta que la venta no existe o no ha sido registrada.  
1.a.2 El sistema muestra un mensaje: "Venta no válida".

**5.a Error en la generación del ticket**  
5.a.1 El sistema detecta un error al generar o imprimir el ticket.  
5.a.2 El sistema muestra un mensaje: "No se pudo generar el ticket".

### Reglas de negocio
- El ticket solo puede generarse para ventas previamente registradas.  
- La información del ticket debe reflejar fielmente los datos de la venta y sus detalles.  
- El subtotal de cada detalle de venta se calcula automáticamente por el sistema.  
- El ticket debe incluir todos los productos asociados a la venta.  
- El ticket constituye un comprobante de la operación realizada.

## RF-15: Autenticación de Usuario

### Descripción
El sistema debe permitir a los usuarios acceder a sus funcionalidades mediante un proceso de autenticación basado en **nombre de usuario (username) único** y contraseña.

### Flujo principal
1. El usuario accede a la pantalla de inicio de sesión.  
2. El usuario ingresa su nombre de usuario (username) único.  
3. El usuario ingresa su contraseña.  
4. El sistema valida que las credenciales ingresadas sean correctas.  
5. El sistema determina el rol del usuario (`Administrador` o `Operador`) y permite el acceso a las funcionalidades según los permisos asociados.

### Flujos alternativos

**4.a Credenciales inválidas**  
4.a.1 El sistema detecta que el nombre de usuario no existe o que la contraseña es incorrecta.  
4.a.2 El sistema muestra un mensaje: "Datos inválidos".  

### Reglas de negocio
- El acceso al sistema requiere autenticación previa.  
- La contraseña ingresada debe ser correcta para el username proporcionado.  
- El sistema determina el rol automáticamente desde el registro del usuario y restringe el acceso a las funcionalidades según dicho rol.

## RF-16: Actualizar Lista de Productos / Ventas

### Descripción
El sistema debe permitir al usuario actualizar la lista de productos o ventas mostrada en pantalla, reflejando cualquier cambio que haya ocurrido en el sistema desde la última visualización.

### Flujo principal
1. El usuario accede a la sección de Productos o Ventas.  
2. El usuario presiona el botón "Actualizar" o "Refrescar".  
3. El sistema recupera la información más reciente de la base de datos.  
4. El sistema muestra la lista actualizada de productos o ventas en pantalla.  

### Flujos alternativos

**3.a Error al recuperar datos**  
3.a.1 El sistema detecta un error al consultar la información.  
3.a.2 El sistema muestra un mensaje: "No se pudo actualizar la lista. Intente nuevamente."  

### Reglas de negocio
- La actualización solo debe refrescar la información mostrada, sin modificar datos existentes.  
- El sistema debe mostrar los cambios realizados por otros usuarios o procesos desde la última actualización.  
- El botón de actualización debe estar disponible para todos los usuarios con permiso de consulta en la sección correspondiente (Productos o Ventas).

## RF-17: Consultar Producto

### Descripción
El sistema debe permitir al usuario consultar la información detallada de un producto específico registrado en el sistema.

### Flujo principal
1. El usuario accede a la sección de productos.  
2. El usuario selecciona un producto específico.  
3. El sistema recibe el identificador del producto.  
4. El sistema recupera los datos del producto desde la base de datos.  
5. El sistema muestra la información del producto con los siguientes datos:
   - Código del producto  
   - Nombre del producto  
   - Precio
   - Unidad de medida
   - Estado  
   - Stock disponible  

### Flujos alternativos

**3.a Producto no encontrado**  
3.a.1 El sistema detecta que el producto no existe.  
3.a.2 El sistema muestra un mensaje: "Producto no encontrado".  

### Reglas de negocio
- El sistema debe permitir consultar un producto específico mediante su identificador.  

## RF-18: Consultar Venta

### Descripción
El sistema debe permitir al usuario consultar la información detallada de una venta específica registrada en el sistema.

### Flujo principal
1. El usuario accede a la sección de ventas.  
2. El usuario selecciona una venta específica.  
3. El sistema recibe el identificador de la venta.  
4. El sistema recupera los datos de la venta desde la base de datos.  
5. El sistema muestra la información de la venta con los siguientes datos:
   - Identificador único de la venta  
   - Fecha de la venta  
   - Hora de la venta  
   - Nombre de usuario (user_name) del vendedor  
   - Total de la venta  
   - Detalle de cada producto vendido: código, nombre, cantidad junto con su unidad de medida, precio al momento de la venta y subtotal

### Flujos alternativos

**3.a Venta no encontrada**  
3.a.1 El sistema detecta que la venta no existe.  
3.a.2 El sistema muestra un mensaje: "Venta no encontrada".  

### Reglas de negocio
- El sistema debe permitir consultar una venta específica mediante su identificador.  
- La información mostrada debe reflejar fielmente los datos almacenados de la venta y sus detalles.
- La cantidad de cada producto se muestra junto con su unidad de medida para reflejar correctamente la venta.

## RF-19: Cambiar Idioma de la Interfaz

### Descripción
El sistema debe permitir al usuario cambiar el idioma de la interfaz entre español e inglés.

### Flujo principal
1. El usuario accede a la configuración de la interfaz.  
2. El usuario selecciona el idioma deseado (español o inglés).  
3. El sistema aplica el cambio de idioma de manera inmediata en todos los textos visibles.  
4. El sistema confirma el cambio mostrando la interfaz en el idioma seleccionado.

### Reglas de negocio
- El cambio de idioma no debe afectar los datos registrados en el sistema.  
- El idioma seleccionado se mantiene durante toda la sesión del usuario.  
- Todos los elementos de la interfaz (menús, botones, mensajes, notificaciones) deben mostrarse en el idioma seleccionado.

## Reglas Generales

### Reglas de Acceso al Sistema

#### Tipos de usuario

#### Tipos de usuario

**Administrador**
- Acceso completo a todas las funcionalidades del sistema.  
- Puede registrar productos (RF-1).  
- Puede consultar productos (RF-2).  
- Puede modificar productos (RF-3).  
- Puede dar de baja productos (RF-4).  
- Puede buscar productos por código de barras (RF-5).  
- Puede registrar productos por código de barras (RF-6).  
- Puede consultar producto específico (RF-17).  
- Puede registrar ventas (RF-7).  
- Puede consultar ventas (RF-8).  
- Puede modificar ventas (RF-9).  
- Puede eliminar ventas (RF-10).  
- Puede asociar productos a ventas (RF-11, RF-12).  
- Puede desvincular productos de ventas (RF-13).  
- Puede generar ticket de venta (RF-14).  
- Puede consultar venta específica (RF-18).  
- Puede actualizar lista de productos y ventas (RF-16).  
- Puede autenticarse en el sistema (RF-15).  
- Puede cambiar el idioma de la interfaz (RF-19).  

**Operador (cajero)**
- Puede autenticarse en el sistema (RF-15).  
- Puede consultar productos (RF-2).  
- Puede consultar producto específico (RF-17).  
- Puede buscar productos por código de barras (RF-5).  
- Puede consultar ventas (RF-8).  
- Puede consultar venta específica (RF-18).  
- Puede registrar ventas (RF-7).  
- Puede asociar productos a ventas (RF-11, RF-12).  
- Puede desvincular productos de ventas durante el registro de una venta (RF-13).  
- Puede generar ticket de venta (RF-14).  
- Puede actualizar lista de productos y ventas (RF-16).  
- Puede cambiar el idioma de la interfaz (RF-19).  
- No puede registrar productos (RF-1).  
- No puede modificar productos (RF-3).  
- No puede dar de baja productos (RF-4).
- No puede registrar productos por código de barras (RF-6).    
- No puede modificar ventas (RF-9).  
- No puede eliminar ventas (RF-10).
- No puede desvincular productos de ventas una vez registrada la venta en el sistema (RF-13).


### Datos de usuario almacenados

- El sistema debe almacenar información de cada usuario para gestionar su acceso y permisos.  
- Cada usuario tendrá:
  - ID de usuario: identificador único interno.  
  - Nombre de usuario: nombre único para identificar al usuario en el sistema.  
  - Rol: tipo de usuario que determina los permisos dentro del sistema.  
  - Contraseña: asociada a la cuenta del usuario.  

### Datos numéricos y decimales

- Todos los valores numéricos que representen montos monetarios o cantidades de productos deben almacenarse y mostrarse con un máximo de 2 decimales.  
  Esto incluye:  
  - Precio del producto.  
  - Precio del producto en detalle de venta (precio de venta).  
  - Subtotal de cada detalle de venta.  
  - Total de la venta.  
  - Stock del producto.  
  - Cantidad del producto en cada detalle de venta.

### Formatos de fecha y hora

- Todas las fechas en el sistema deben mostrarse en formato `DD/MM/YYYY`.  
- Todas las horas en el sistema deben mostrarse en formato de 24 horas con segundos `HH:MM:SS`.  
- Los formatos se aplican en la interfaz de usuario y en reportes.  
