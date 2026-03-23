Requerimientos del Sistema

Sistema de gestión de ventas para un kiosko

1. Descripción general
Un cliente solicita el desarrollo de una aplicación web que le permita gestionar los productos y las ventas de su negocio.
El sistema deberá permitir registrar productos, realizar ventas y generar un ticket con el detalle de cada operación.

2. Objetivo del sistema
El objetivo del sistema es permitir que un negocio pueda:
	*Mantener un registro de sus productos
	*Controlar el stock disponible
	*Registrar ventas realizadas
	*Obtener un ticket detallado de cada operación

3. Gestión de productos
El sistema deberá permitir almacenar y gestionar información sobre los productos disponibles en el negocio.
De cada producto se deberán registrar los siguientes datos:
	*Nombre del producto
	*Código del producto (identificador único dentro del sistema)
	*Precio del producto
	*Unidad de medida del producto (por ejemplo: unidad, kilogramo, litro)
	*Stock disponible
	*Estado del producto (activo/inactivo)
El sistema deberá permitir:
	*Registrar un nuevo producto
	*Consultar productos existentes
	*Modificar los datos de un producto
	*Dar de baja lógica a un producto (marcarlo como inactivo)
	*Actualizar la lista de productos mostrada en pantalla, para reflejar cambios recientes en el sistema

4. Gestión de ventas
El sistema deberá permitir registrar las ventas realizadas en el negocio.
De cada venta se deberán almacenar los siguientes datos:
	*Identificador único de la venta
	*Fecha de la venta
	*Hora de la venta
	*Total de la venta
	*Usuario que realizó la venta
El sistema deberá permitir:
	*Registrar una nueva venta
	*Consultar ventas existentes
	*Modificar los datos de una venta
	*Dar de baja una venta
	*Actualizar la lista de ventas mostrada en pantalla, para reflejar cambios recientes en el sistema

5. Detalle de venta
Para cada producto incluido en una venta se deberán almacenar los siguientes datos:
	*Identificador único del detalle de venta
	*Venta asociada
	*Producto vendido
	*Precio al momento de la venta
	*Cantidad vendida
	*Subtotal de la línea de venta
El sistema deberá permitir:
	*Asociar un producto a una venta
	*Desvincular un producto de una venta
	*Modificar los datos de un detalle de venta

6. Lectura de código de barras  
El sistema deberá permitir la lectura de códigos de barras mediante un lector.

Esta funcionalidad permitirá:
	*Identificar productos existentes en el sistema mediante el código escaneado.
	*Facilitar el registro de nuevos productos a partir del código de barras.
	*Agilizar la selección de productos en procesos de venta mediante la lectura del código.

7. Generación de ticket de venta
Por cada venta registrada el sistema deberá poder generar un ticket de compra que incluya:
	*Nombre del negocio
	*Fecha de emisión del ticket
	*Hora de la emisión del ticket
	*Lista de productos vendidos
	*Cantidad de cada producto
	*Precio unitario
	*Subtotal de cada producto
	*Total de la venta
El ticket deberá representar el comprobante de la operación realizada.

8. Usuarios
El sistema deberá contar con dos tipos de acceso predefinidos(roles):
Administrador
	*Tendrá acceso completo a todas las funcionalidades del sistema.
	*Podrá consultar y gestionar productos.
	*Podrá consultar y gestionar ventas, incluidos los detalles de cada venta.
Operador(cajero)
	*Podrá registrar ventas.
	*Podrá consultar productos.
	*No podrá crear, modificar o eliminar productos.
	*No podrá modificar o eliminar ventas ni los detalles de las mismas.
El sistema deberá almacenar información sobre cada usuario para poder gestionar su acceso y permisos. Cada usuario tendrá los siguientes datos:
	*ID de usuario: identificador único interno de cada usuario.
	*Nombre de usuario: nombre único que se utiliza para iniciar sesión en el sistema.
	*Rol: tipo de usuario, que determina los permisos dentro del sistema.
	*Contraseña: contraseña asociada a la cuenta del usuario, utilizada para validar su identidad.
Nota: Esta información se almacena en la base de datos para poder autenticar a los usuarios y controlar los accesos a las diferentes funcionalidades del sistema.

9. Acceso al sistema (actualizado)
El sistema deberá requerir autenticación para permitir el acceso a sus funcionalidades.
Para acceder al sistema, el usuario deberá ingresar:
	*Nombre de usuario (username) único asignado al usuario.
	*Contraseña correspondiente a su cuenta.
Nota: Ya no es necesario que el usuario seleccione manualmente su tipo de usuario; el rol se obtiene automáticamente del registro en la base de datos.

