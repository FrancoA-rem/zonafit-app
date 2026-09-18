# PowerFit (Gym)

Sistema de gestión de clientes para un gimnasio. CRUD de clientes (listar, buscar, agregar, modificar, eliminar) con JDBC y MySQL.

## Stack
- Java 21+
- Maven
- MySQL (JDBC)

## Cómo correrlo
1. Crear la base de datos `zona_fit_db` y la tabla `cliente`.
2. Configurar usuario/contraseña en `Conexion.java`.
3. `mvn compile exec:java`

## Estructura
- `zona_fit.dominio` — entidad Cliente
- `zona_fit.datos` — capa de acceso a datos (DAO)
- `zona_fit.conexion` — conexión JDBC
- `zona_fit.presentacion` — menú de consola

## Estado
Proyecto en desarrollo (curso).