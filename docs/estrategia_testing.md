# Estrategia de pruebas

## Propósito

La estrategia de pruebas garantiza que la API cumple el contrato definido, conserva las reglas de negocio y se integra correctamente con MySQL.

## Estado actual

Actualmente el proyecto cuenta con una prueba de carga de contexto de Spring Boot. Todavía no existe una suite completa de pruebas unitarias, de controlador o de integración. Por tanto, el MVP no debe considerarse completamente validado hasta completar los casos descritos en este documento.

## Niveles de prueba

| Nivel | Componente | Tecnología objetivo | Alcance |
|---|---|---|---|
| Unidad | `ClienteServicio` | JUnit 5 y Mockito | Reglas y delegación al repositorio |
| Controlador | `ClienteController` | `@WebMvcTest` y MockMvc | Rutas, JSON, validaciones y códigos HTTP |
| Integración | API y MySQL | Spring Boot y Testcontainers | Flujo HTTP completo y persistencia |
| Frontend | Componentes React | Testing Library | Formulario, tabla y estados de la interfaz |
| Manual | API | Postman o Thunder Client | Verificación exploratoria del contrato |

## Casos críticos de la API

### Listar clientes

- Cuando existen clientes, devuelve todos los registros.
- Cuando no existen clientes, devuelve una lista vacía.
- Cada entidad se convierte correctamente en `ClienteResponse`.

### Buscar por ID

- Un ID existente devuelve el cliente correspondiente.
- Un ID inexistente devuelve `404`.
- Un ID con formato inválido devuelve `400`.

### Crear cliente

- Un cuerpo válido devuelve `201` y el identificador generado.
- Un nombre vacío devuelve `400`.
- Un apellido vacío devuelve `400`.
- Una membresía inválida devuelve `400`.
- El cliente creado puede consultarse posteriormente.

### Actualizar cliente

- Un cliente existente se modifica correctamente.
- Los campos modificados se conservan después de consultar la API.
- Un ID inexistente devuelve `404`.
- Un cuerpo inválido devuelve `400`.

### Eliminar cliente

- Un cliente existente se elimina y devuelve `204`.
- El cliente eliminado ya no aparece en el listado.
- Un ID inexistente devuelve `404`.

## Pruebas de la capa de servicio

Las pruebas de `ClienteServicio` deben verificar, como mínimo:

| Caso | Verificación |
|---|---|
| Listar | Se delega en `findAll()` y se devuelve la lista |
| Buscar existente | Se delega en `findById()` y se devuelve el cliente |
| Buscar inexistente | Se devuelve `null` u `Optional.empty()`, según el contrato elegido |
| Guardar | Se delega en `save()` |
| Eliminar | Se delega en `delete()` |

Para estas pruebas no se necesita una base de datos real; el repositorio se puede simular con Mockito.

## Pruebas de integración

Las pruebas de integración deben validar:

1. Que la aplicación inicia correctamente con su configuración de prueba.
2. Que la tabla y el esquema son compatibles con la entidad.
3. Que un cliente se persiste y se recupera.
4. Que una modificación se mantiene después de una nueva consulta.
5. Que una eliminación no permite recuperar el registro.
6. Que los datos de una prueba no se mezclan con los de otra.

Testcontainers con MySQL es la opción más fiel para el proyecto. H2 puede utilizarse para pruebas rápidas, siempre que se configure el dialecto de forma compatible.

## Casos de prueba en formato Given/When/Then

### Creación válida

```text
Dado que se dispone de datos válidos
Cuando se envía POST /api/clientes
Entonces la API responde 201
Y el cliente queda persistido
```

### Cliente inexistente

```text
Dado que el ID solicitado no existe
Cuando se ejecuta GET /api/clientes/{id}
Entonces la API responde 404
Y no se devuelve información de otro cliente
```

### Error de validación

```text
Dado que se envía un nombre vacío
Cuando se procesa la solicitud
Entonces la API responde 400
Y no se crea ningún registro
```

## Comandos

Ejecutar todas las pruebas:

```bash
mvn test
```

Ejecutar una clase específica:

```bash
mvn -Dtest=ClienteServicioTest test
```

Ejecutar una prueba específica:

```bash
mvn -Dtest=ClienteControllerTest#crearCliente test
```

## Criterios de calidad

Antes de considerar estable el MVP:

- [ ] Todas las rutas CRUD tienen pruebas.
- [ ] Los casos de validación tienen pruebas automatizadas.
- [ ] Los casos de cliente inexistente tienen pruebas.
- [ ] Existe al menos una prueba de integración con MySQL.
- [ ] Las pruebas no dependen de datos del entorno local.
- [ ] El resultado de `mvn test` es exitoso.
- [ ] Se documentan los errores esperados de la API.

## Estrategia para el frontend

Cuando React se incorpore, se añadirán pruebas para:

- Renderizado de la tabla de clientes.
- Envío del formulario de alta.
- Carga de datos de edición.
- Confirmación de eliminación.
- Mensajes de éxito y error.
- Estado de carga y estado vacío.
