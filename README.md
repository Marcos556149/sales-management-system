# PrimeSale

Sistema web de gestión de ventas desarrollado como proyecto de portfolio.

Permite administrar productos, usuarios y ventas, además de generar comprobantes de venta, visualizar estadísticas comerciales y generar reportes PDF.

El sistema está orientado a pequeños y medianos comercios que necesitan registrar ventas y controlar su inventario mediante una interfaz moderna y sencilla.

---

## Funcionalidades

### Gestión de Productos

- Registro de productos.
- Consulta de productos con paginación.
- Consulta de detalle de producto.
- Modificación de productos.
- Desactivación lógica de productos.
- Reactivación de productos.
- Búsqueda mediante código de barras.
- Registro de productos utilizando código de barras.
- Control de stock mínimo.
- Indicadores visuales de bajo stock y productos sin stock.

### Gestión de Ventas

- Registro de ventas.
- Búsqueda de productos por nombre o código.
- Agregado de productos mediante lector de código de barras.
- Validación automática de stock.
- Control de productos duplicados dentro de una venta.
- Consulta de historial de ventas.
- Consulta detallada de una venta.
- Generación de comprobantes de venta.
- Impresión de tickets para impresoras térmicas.

### Estadísticas y Reportes

- Estadísticas de ventas por rango de fechas.
- Estadísticas por usuario.
- Ingresos totales.
- Cantidad total de ventas.
- Valor promedio por venta.
- Hora con mayores ingresos.
- Hora con mayor cantidad de ventas.
- Top 10 productos más vendidos.
- Top 10 productos con mayores ingresos.
- Ranking completo de productos vendidos.
- Listado de productos sin ventas.
- Generación de reportes PDF de estadísticas.

### Gestión de Usuarios

- Autenticación mediante sesión.
- Cierre de sesión.
- Registro de usuarios operadores.
- Consulta de usuarios.
- Consulta de detalle de usuario.
- Modificación de usuarios.
- Cambio de estado de usuarios.
- Control de acceso basado en roles.

### Configuración del Sistema

- Configuración global del negocio.
- Administración del nombre comercial.
- Administración de la dirección del negocio.

---

## Roles de Usuario

### Administrador

Acceso completo al sistema:

- Gestión de productos.
- Gestión de ventas.
- Gestión de usuarios.
- Configuración del sistema.
- Estadísticas y reportes PDF.

### Operador

Acceso operativo:

- Registro de ventas.
- Consulta de productos.
- Consulta de ventas.
- Generación de comprobantes.

---

## Stack Tecnológico

### Backend

- Java 17
- Spring Boot
- Spring Data JPA
- Hibernate
- Spring Security
- Maven
- MapStruct

### Frontend

- React
- Vite
- JavaScript
- CSS

### Base de Datos

- PostgreSQL
- Flyway

### Infraestructura

- Docker
- Docker Compose
- Nginx

---

## Características Técnicas

- Arquitectura multicapa.
- API REST.
- Persistencia mediante Spring Data JPA y Hibernate.
- Separación entre entidades y DTOs mediante MapStruct.
- Migraciones automáticas con Flyway.
- Control de acceso basado en roles.
- Autenticación mediante sesiones.
- Validación de datos en backend.
- Paginación y filtrado de resultados.
- Generación de comprobantes de venta.
- Generación de reportes PDF.
- Contenerización mediante Docker.
- Orquestación de servicios mediante Docker Compose.

---

## Arquitectura

```text
Frontend (React)
        │
        ▼
Backend (Spring Boot)
        │
        ▼
PostgreSQL
```

---

## Requisitos

- Docker
- Docker Compose

No es necesario instalar Java, Node.js o PostgreSQL localmente.

---

## Ejecución

### Clonar el repositorio

```bash
git clone https://github.com/Marcos556149/sales-management-system.git
cd sales-management-system
```

### Construir y ejecutar la aplicación

```bash
docker compose up --build
```
> La primera ejecución puede tardar algunos minutos mientras Docker descarga las imágenes necesarias y construye los servicios.

### Acceso a la aplicación

Frontend:
`http://localhost:5173`

Backend:
`http://localhost:8080`

---

## Usuarios de Prueba

> Los siguientes usuarios se encuentran disponibles únicamente si se cargan los datos de demostración mediante `database/demo-data.sql`.

### Administrador

- Usuario: `admin`
- Contraseña: `admin`

### Operador

- Usuario: `operador`
- Contraseña: `operador`

---

## Datos de Demostración

El proyecto incluye scripts opcionales para facilitar las pruebas:

- `database/demo-data.sql`: inserta productos, usuarios, configuraciones y datos históricos de ventas.
- `database/reset-demo-data.sql`: elimina los datos de demostración y reinicia las secuencias de identificadores.

El conjunto de datos de demostración incluye:

- 230 productos de ejemplo.
- Usuarios preconfigurados para pruebas.
- Historial de ventas correspondiente a los meses de marzo, abril y mayo de 2026.
- Información suficiente para probar estadísticas, rankings de productos y generación de reportes PDF.

Los usuarios de prueba documentados en este README son creados por `demo-data.sql`.

Los scripts de demostración son opcionales y no son necesarios para ejecutar la aplicación.

---

## Acceso a la Base de Datos

La base de datos PostgreSQL se encuentra disponible en:

- Host: `localhost`
- Puerto: `5433`
- Base de datos: `sales_management_db`
- Usuario: `postgres`
- Contraseña: `postgres`

Cadena JDBC:

```text
jdbc:postgresql://localhost:5433/sales_management_db
```

---

## Objetivo del Proyecto

Este proyecto fue desarrollado con fines educativos y de portfolio para demostrar conocimientos en:

- Desarrollo backend con Java y Spring Boot.
- Diseño de APIs REST.
- Persistencia de datos con JPA/Hibernate.
- Seguridad basada en sesiones y control de acceso por roles.
- Desarrollo frontend con React.
- Gestión de bases de datos PostgreSQL.
- Migraciones con Flyway.
- Contenerización mediante Docker.
- Despliegue de aplicaciones multicapa.

---

## Licencia

Proyecto desarrollado con fines educativos y de portfolio.

---