# API REST

## Convenciones

- Base de la API: `/api`.
- Formato de entrada y salida: `application/json`.
- Puerto local por defecto: `8080`.
- Los identificadores viajan en la ruta.
- Los códigos HTTP comunican el resultado de la operación.

## Rutas implementadas

| Método | Ruta | Descripción | Respuesta exitosa | Errores esperados |
|---|---|---|---|---|
| `GET` | `/api/clientes` | Lista todos los clientes | `200 OK` | `500` |
| `GET` | `/api/clientes/{id}` | Busca un cliente por ID | `200 OK` | `400`, `404` |
| `POST` | `/api/clientes` | Crea un cliente | `201 Created` | `400`, `500` |
| `PUT` | `/api/clientes/{id}` | Actualiza un cliente | `200 OK` | `400`, `404`, `500` |
| `DELETE` | `/api/clientes/{id}` | Elimina un cliente | `204 No Content` | `400`, `404`, `500` |

## Modelo de cliente

### Crear o actualizar

El cuerpo de creación no incluye el `id`:

```json
{
  "nombre": "Ana",
  "apellido": "López",
  "membresia": 100
}
```

### Respuesta

```json
{
  "id": 1,
  "nombre": "Ana",
  "apellido": "López",
  "membresia": 100
}
```

## `GET /api/clientes`

Devuelve un arreglo JSON. Si no existen clientes, el arreglo debe estar vacío.

```json
[
  {
    "id": 1,
    "nombre": "Ana",
    "apellido": "López",
    "membresia": 100
  },
  {
    "id": 2,
    "nombre": "Luis",
    "apellido": "Pérez",
    "membresia": 200
  }
]
```

## `GET /api/clientes/{id}`

Busca un cliente existente.

Respuesta `200 OK`:

```json
{
  "id": 1,
  "nombre": "Ana",
  "apellido": "López",
  "membresia": 100
}
```

Si el ID no existe, la respuesta debe ser `404 Not Found`.

## `POST /api/clientes`

Crea un cliente a partir de un `ClienteRequest`.

- Content-Type: `application/json`.
- Respuesta: `201 Created`.
- El cuerpo de respuesta debe incluir el `id` generado.

La operación no debe aceptar un `id` en el cuerpo de creación.

Respuesta `201 Created`:

```json
{
  "id": 1,
  "nombre": "Ana",
  "apellido": "López",
  "membresia": 100
}
```

Una solicitud con `nombre` vacío o `membresia` menor o igual a cero devuelve `400 Bad Request`.

## `PUT /api/clientes/{id}`

Actualiza los datos modificables de un cliente existente.

El `id` de la ruta identifica el registro que se actualiza. Si no existe, la API responde `404 Not Found`.

Solicitud:

```json
{
  "nombre": "Ana María",
  "apellido": "López",
  "membresia": 120
}
```

Respuesta `200 OK` con el cliente actualizado.

## `DELETE /api/clientes/{id}`

Elimina un cliente existente.

Una eliminación exitosa no necesita devolver un cuerpo:

```http
204 No Content
```

Si el cliente no existe, se responde `404 Not Found`.

## Validación

La API debe rechazar datos inválidos antes de llegar a la base de datos. Las reglas iniciales son:

| Campo | Regla |
|---|---|
| `nombre` | Obligatorio, no vacío |
| `apellido` | Obligatorio, no vacío |
| `membresia` | Obligatorio, entero mayor a cero |

Una solicitud inválida debe devolver `400 Bad Request`.

## Formato de error

Cuando una solicitud no supera la validación, `GlobalExceptionHandler` devuelve `400 Bad Request` con el detalle de cada campo:

```json
{
  "status": 400,
  "errores": {
    "nombre": "El nombre es obligatorio",
    "membresia": "La membresía debe ser mayor a cero"
  }
}
```

Cuando el recurso solicitado no existe, los endpoints `GET`, `PUT` y `DELETE` devuelven `404 Not Found` sin cuerpo.

Un `id` que no sea un número válido devuelve `400 Bad Request`.

## CORS y frontend

React se ejecutará en un origen diferente al backend durante el desarrollo local. Se debe utilizar un proxy de desarrollo o una configuración de CORS limitada a los orígenes permitidos.

No se recomienda habilitar CORS abierto en producción.

## Estado actual

Los endpoints `GET`, `POST`, `PUT` y `DELETE` de `/api/clientes` están implementados, validados y verificados contra MySQL. El manejo de errores de validación está centralizado en `GlobalExceptionHandler` y la suite de pruebas automatizadas cubre las rutas, los casos de validación, los recursos inexistentes y la persistencia con H2. La interfaz React queda pendiente.
