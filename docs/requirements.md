# PrimeSale - Requerimientos del Sistema

Sistema web de gestión de ventas e inventario para negocios minoristas.

## Índice

### Descripción General del Sistema
- [1. Descripción General](#1-descripción-general)
- [2. Objetivo del Sistema](#2-objetivo-del-sistema)

### Áreas Funcionales Principales
- [3. Gestión de Productos](#3-gestión-de-productos)
- [4. Gestión de Ventas](#4-gestión-de-ventas)
- [5. Detalle de Venta](#5-detalle-de-venta)
- [6. Escaneo de Códigos de Barras](#6-escaneo-de-códigos-de-barras)
- [7. Generación de Comprobantes de Venta](#7-generación-de-comprobantes-de-venta)

### Gestión de Usuarios y Acceso
- [8. Usuarios](#8-usuarios)
  - [Gestión de Usuarios](#gestión-de-usuarios)
- [9. Acceso al Sistema](#9-acceso-al-sistema)

### Configuración del Sistema
- [10. Configuración del Sistema](#10-configuración-del-sistema)
  - [Configuración General](#configuración-general)
  - [Funcionalidades](#funcionalidades)

### Funcionalidades Adicionales
- [11. Estadísticas de Ventas](#11-estadísticas-de-ventas)
  - [11.1 Información de Ventas](#111-información-de-ventas)
  - [11.2 Información de Productos](#112-información-de-productos)
    - [11.2.1 Productos Vendidos](#1121-productos-vendidos)
    - [11.2.2 Productos No Vendidos](#1122-productos-no-vendidos)
  - [11.3 Generación de Reportes](#113-generación-de-reportes)
- [12. Cierre de Sesión](#12-cierre-de-sesión)

---

## 1. Descripción General

PrimeSale es un sistema web de gestión comercial diseñado para ayudar a los negocios minoristas a administrar sus operaciones comerciales diarias.

El sistema proporciona herramientas para la gestión de productos, el control de inventario, el registro de ventas, la generación de comprobantes, la administración de usuarios y el control de acceso, la configuración del sistema y la obtención de reportes y estadísticas del negocio a través de una interfaz intuitiva y eficiente.

## 2. Objetivo del Sistema

El objetivo de PrimeSale es permitir a un negocio:

- Gestionar y mantener la información de los productos
- Controlar el inventario disponible y los niveles de stock
- Registrar y realizar el seguimiento de las ventas efectuadas
- Generar comprobantes detallados para cada transacción
- Gestionar los usuarios del sistema y sus permisos de acceso
- Obtener reportes e información estadística sobre ventas y productos
- Centralizar las operaciones comerciales diarias en una plataforma web eficiente

## 3. Gestión de Productos

El sistema debe permitir gestionar los productos disponibles en el negocio, incluyendo su registro, mantenimiento, control de disponibilidad y consulta.

Para cada producto, se deberá registrar la siguiente información:

- Nombre del producto
- Código del producto (identificador único dentro del sistema)
- Precio del producto
- Unidad de medida (por ejemplo: unidad, kilogramo, litro)
- Stock disponible (de acuerdo con la unidad de medida del producto)
Stock mínimo utilizado para identificar productos con bajo stock (de acuerdo con la unidad de medida del producto)
- Estado del producto (activo/inactivo)

El sistema debe permitir:

- Registrar un nuevo producto
- Visualizar la lista de productos existentes
- Consultar la información detallada de un producto específico
- Actualizar la información de un producto
- Desactivar lógicamente un producto (marcarlo como inactivo)
- Reactivar un producto (marcarlo como activo)

## 4. Gestión de Ventas

El sistema debe permitir gestionar las transacciones de venta realizadas en el negocio, incluyendo su registro y consulta.

Para cada venta, se deberá almacenar la siguiente información:

- Identificador único de la venta
- Fecha de la venta
- Hora de la venta
- Importe total
- Usuario que realizó la venta (administrador autenticado en el sistema al momento de la transacción)

El sistema debe permitir:

- Registrar una nueva venta
- Visualizar la lista de ventas existentes
- Consultar la información detallada de una venta específica

## 5. Detalle de Venta

Para cada producto incluido en una venta, se deberá almacenar la siguiente información:

- Identificador único del detalle de venta
- Venta asociada
- Producto vendido
- Nombre del producto al momento de la venta
- Precio del producto al momento de la venta
- Cantidad vendida (de acuerdo con la unidad de medida del producto al momento de la venta)
- Unidad de medida al momento de la venta
- Subtotal de la línea

El nombre del producto, el precio de venta y la unidad de medida almacenados en el detalle de venta deberán conservar los valores utilizados durante la transacción, incluso si la información correspondiente del producto es modificada posteriormente.

El sistema debe permitir:

- Agregar un producto a una venta mientras la venta está siendo registrada
- Quitar un producto de una venta mientras la venta está siendo registrada

## 6. Escaneo de Códigos de Barras

El sistema debe permitir el escaneo de códigos de barras mediante un lector de códigos de barras.

Esta funcionalidad permitirá:

- Identificar productos existentes en el sistema a través del código escaneado
- Facilitar el registro de nuevos productos utilizando el código de barras
- Agilizar la selección de productos en los procesos de venta mediante el escaneo de códigos de barras

## 7. Generación de Comprobantes de Venta

Para cada venta registrada, el sistema deberá ser capaz de generar un comprobante de venta que incluya:

- Nombre del negocio
- Dirección del negocio
- Fecha de emisión del comprobante
- Hora de emisión del comprobante
- Identificador único de la venta
- Lista de productos vendidos, incluyendo el nombre del producto registrado al momento de la venta
- Cantidad de cada producto junto con la unidad de medida registrada al momento de la venta
- Precio de cada producto al momento de la venta
- Subtotal de cada producto
- Importe total de la venta

El comprobante deberá representar una constancia de la venta realizada.

Consideraciones de implementación:

- El sistema genera el comprobante en formato de ticket de impresión térmica (texto plano), optimizado para dispositivos de impresión de ancho fijo.

- Con el objetivo de simular el comportamiento de sistemas reales de punto de venta, el comprobante incluye información adicional de carácter informativo, la cual no forma parte de los datos transaccionales de la venta:

- Indicación explícita de que el comprobante es un DOCUMENTO NO FISCAL
Mensaje aclaratorio indicando que el comprobante no constituye una factura válida ni posee validez fiscal

- Estas aclaraciones no modifican la estructura funcional del comprobante ni sus datos obligatorios, sino que cumplen un rol informativo y de transparencia para el usuario final, alineándose con el comportamiento habitual de sistemas comerciales que no implementan facturación electrónica.

## 8. Usuarios

El sistema debe contar con dos tipos de acceso (roles) predefinidos:

**Administrador**
- Tiene acceso completo a todas las funcionalidades del sistema
- Puede acceder al sistema mediante autenticación
- Puede gestionar productos, incluyendo su registro, actualización, reactivación y desactivación
- Puede registrar productos mediante código de barras y buscarlos utilizando dicho código
- Puede visualizar listados de productos, información detallada de productos e información de stock
- Puede registrar nuevas ventas
- Puede visualizar listados de ventas e información detallada de cada venta, incluyendo sus detalles asociados
- Puede agregar y quitar productos de una venta mientras esta se encuentra en proceso de registro
- Puede generar comprobantes de venta para las ventas registradas
- Puede visualizar estadísticas de ventas
- Puede generar reportes estadísticos en formato PDF
- Puede actualizar la información del negocio, incluyendo su nombre y dirección
- Puede registrar, visualizar y actualizar usuarios (operadores)
- Puede modificar el estado de los usuarios (activo, suspendido o eliminado)
- Puede cerrar sesión en el sistema

**Operador (cajero)**
- Puede acceder al sistema mediante autenticación
- Puede buscar productos mediante código de barras
- Puede visualizar listados de productos, información detallada de productos e información de stock
- Puede registrar nuevas ventas
- Puede agregar y quitar productos de una venta mientras esta se encuentra en proceso de registro
- Puede generar comprobantes de venta para las ventas registradas
- Puede visualizar listados de ventas e información detallada de cada venta
- Puede cerrar sesión en el sistema
- No puede registrar productos
- No puede actualizar información de productos
- No puede desactivar ni reactivar productos
- No puede registrar productos mediante código de barras
- No puede visualizar estadísticas de ventas
- No puede generar reportes estadísticos en formato PDF
- No puede modificar la información del negocio, incluyendo su nombre y dirección
- No puede registrar, visualizar ni actualizar usuarios (operadores)
- No puede modificar el estado de los usuarios (activo, suspendido o eliminado)

El sistema debe almacenar información sobre cada usuario para gestionar el acceso y los permisos. Cada usuario deberá contar con los siguientes datos:

- ID de usuario: identificador interno único
- Nombre de usuario: nombre único utilizado para acceder al sistema
- Rol: tipo de usuario que determina los permisos dentro del sistema
- Contraseña: contraseña asociada a la cuenta del usuario, utilizada para la autenticación
- Estado: indica si el usuario se encuentra activo, suspendido o eliminado lógicamente

El estado de un usuario puede ser:

- Activo: el usuario puede acceder y operar en el sistema
- Suspendido: el usuario no puede acceder temporalmente al sistema
- Eliminado: el usuario es eliminado de forma lógica y no puede acceder al sistema, pero permanece almacenado para preservar la integridad de los datos históricos

**Nota:** Esta información se almacena en la base de datos para autenticar a los usuarios y controlar el acceso a las funcionalidades del sistema.

El sistema debe validar el estado del usuario durante el proceso de autenticación:

- Solo los usuarios con estado "Activo" pueden acceder al sistema
- Los usuarios con estado "Suspendido" o "Eliminado" deben tener el acceso denegado

### Gestión de Usuarios

El sistema debe permitir a los administradores gestionar los usuarios del sistema.

El sistema debe permitir:

- Registrar un nuevo usuario (únicamente con el rol Operador)
- Visualizar los usuarios existentes
- Actualizar la información de un usuario
- Modificar el estado de un usuario (activo, suspendido o eliminado)

Solo los usuarios con el rol Administrador pueden realizar estas acciones.

**Configuración Inicial:**

El sistema debe incluir una cuenta de administrador predefinida, creada durante la inicialización del sistema. Esta cuenta será utilizada para administrar los usuarios del sistema.

## 9. Acceso al Sistema

El sistema debe requerir autenticación para permitir el acceso a sus funcionalidades.

Para acceder al sistema, el usuario deberá ingresar:

- Nombre de usuario (identificador único asignado al usuario)
- Contraseña correspondiente

## 10. Configuración del Sistema

El sistema debe proporcionar una interfaz de usuario para la administración de la configuración general del sistema.

### Configuración General

El sistema debe permitir almacenar y actualizar la siguiente información de configuración general:

- Nombre del negocio
- Dirección del negocio

Esta configuración es compartida por todos los usuarios del sistema.

### Funcionalidades

El sistema debe permitir:

- Visualizar la configuración general actual
- Actualizar la información del negocio, incluyendo el nombre y la dirección (solo administradores)

## 11. Estadísticas de Ventas

El sistema debe proporcionar una sección de estadísticas que permita a los usuarios analizar el rendimiento de las ventas y de los productos mediante datos agregados, indicadores clave y representaciones visuales.

El sistema debe permitir al usuario obtener información estadística en función de los filtros seleccionados:

- Usuario (todos los usuarios o un usuario específico)
- Rango de fechas (fecha de inicio y fecha de fin)

### 11.1 Información de Ventas

Para los filtros seleccionados, el sistema deberá mostrar:

- Ingresos totales por ventas
- Cantidad total de ventas
- Ticket promedio (ingresos totales divididos por la cantidad de ventas)
- Hora con mayores ingresos por ventas
- Hora con mayor cantidad de ventas

El sistema deberá mostrar gráficos temporales que representen:

- Ingresos por ventas a lo largo del tiempo
- Cantidad de ventas a lo largo del tiempo

Los gráficos deberán adaptar su nivel de granularidad temporal (por ejemplo: hora, día, mes o año) de acuerdo con el rango de fechas seleccionado.

### 11.2 Información de Productos

El sistema debe proporcionar información sobre el rendimiento de los productos en función de los filtros seleccionados.

#### 11.2.1 Productos Vendidos

Para los productos que registren ventas, el sistema deberá proporcionar:

- Top 10 productos según la cantidad vendida
- Top 10 productos según los ingresos generados

Estos valores deberán presentarse mediante gráficos en la vista principal de estadísticas.

- Un listado de ranking de productos que permita:
  - Seleccionar la métrica:
    - Cantidad vendida
    - Ingresos generados
  - Seleccionar el orden:
    - De mayor a menor (Más vendido → menos vendido)
    - De menor a mayor (Menos vendido → más vendido)

El sistema debe permitir acceder a una vista detallada del ranking, donde:

- Se muestren todos los productos vendidos que coincidan con los filtros seleccionados
- Se aplique paginación para la navegación entre resultados

#### 11.2.2 Productos No Vendidos

Para los productos que no registren ventas, el sistema deberá proporcionar:

- Un listado completo de productos no vendidos

El sistema debe permitir acceder a una vista detallada donde:

- Se muestren todos los productos no vendidos que coincidan con los filtros seleccionados
- Se aplique paginación para la navegación entre resultados

### 11.3 Generación de Reportes

El sistema debe permitir generar un reporte en formato PDF basado en los filtros seleccionados.

El reporte deberá incluir siempre:

- Título del reporte
- Usuario que genera el reporte (usuario administrador autenticado en el sistema al momento de la generación)
- Fecha y hora de generación del reporte
- Usuario seleccionado
- Rango de fechas seleccionado

El sistema debe permitir al usuario seleccionar qué secciones incluir en el reporte.

Cada sección deberá incluirse de forma completa, sin permitir selecciones parciales.

Las secciones disponibles son:

- Información de ventas
- Información de productos

Cada sección seleccionada deberá incluir toda la información correspondiente definida en la sección de estadísticas.

Para las secciones relacionadas con productos, el sistema debe permitir al usuario seleccionar la cantidad de productos que se incluirán en el reporte.

Las opciones disponibles deberán ser:

- 10 productos
- 20 productos
- 50 productos
- 100 productos

Para el listado de ranking de productos incluido en el reporte, el sistema debe permitir seleccionar:

- Cantidad vendida o ingresos generados como métrica de clasificación
- Orden de clasificación:
  - De mayor a menor (Más vendido → menos vendido)
  - De menor a mayor (Menos vendido → más vendido)

El sistema deberá generar el reporte como un archivo PDF descargable.

## 12. Cierre de Sesión

El sistema debe permitir al usuario cerrar la sesión actual.

El sistema debe permitir:

- Finalizar la sesión del usuario previa confirmación
- Redirigir al usuario a la pantalla de inicio de sesión