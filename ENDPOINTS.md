# Endpoints de Medily

Base URL: `http://localhost:8080/api`

Las peticiones del frontend se centralizan en `src/main/resources/static/js/api.js` y se realizan mediante `fetch`. Al iniciar sesión, el backend crea la cookie `medily_session`; el navegador la envía automáticamente en las peticiones del mismo origen.

## Autenticacion

| Metodo | Ruta | Proteccion | Uso |
|---|---|---|---|
| POST | `/auth/login` | Publico | Consulta usuario/correo y contrasena. Crea la cookie de sesion. |
| GET | `/auth/session` | Cookie opcional | Consulta si existe una sesion valida. |
| DELETE | `/auth/logout` | Cookie opcional | Invalida la sesion y elimina la cookie. |

Ejemplo de login:

```json
{
  "identity": "admin@medily.com",
  "password": "Medily2026*"
}
```

## Contrato POST de dispositivos/productos

En Medily, el recurso `devices` representa los productos del catalogo. Por
eso, el contrato POST de productos se consume mediante `/devices`.

### POST `/devices`

Requiere la cookie `medily_session` obtenida en `/auth/login`.

Headers:

```http
Content-Type: application/json
Cookie: medily_session=<token-de-sesion>
```

Request body:

```json
{
  "name": "Dispositivo Postman",
  "brand": "Medily",
  "price": 899000,
  "image": "https://example.com/medily.jpg",
  "description": "Producto creado mediante la API REST",
  "releaseDate": "2026-09-18",
  "category": "Gama media",
  "features": {
    "storage": "256 GB",
    "display": "6.7 pulgadas"
  }
}
```

Respuesta `201 Created`:

```json
{
  "id": 7,
  "name": "Dispositivo Postman",
  "brand": "Medily",
  "price": 899000,
  "image": "https://example.com/medily.jpg",
  "description": "Producto creado mediante la API REST",
  "releaseDate": "2026-09-18",
  "category": "Gama media",
  "features": {
    "storage": "256 GB",
    "display": "6.7 pulgadas"
  }
}
```

Errores posibles:

- `401 Unauthorized`: no existe una sesion administrativa valida.
- `400 Bad Request`: faltan campos obligatorios, el precio es invalido o la
  categoria no existe.
- `409 Conflict`: se viola una restriccion de integridad de la base de datos.

## CRUD de dispositivos/productos

| Metodo | Ruta | Proteccion | Uso |
|---|---|---|---|
| GET | `/devices` | Publico | Lista dispositivos y permite `search`, `brand` y `category`. |
| GET | `/devices/{id}` | Publico | Consulta un dispositivo por identificador. |
| POST | `/devices` | Cookie de sesion | Crea un dispositivo. |
| PUT | `/devices/{id}` | Cookie de sesion | Edita un dispositivo. |
| DELETE | `/devices/{id}` | Cookie de sesion | Elimina un dispositivo. |
| GET | `/devices/options` | Publico | Consulta marcas y categorias disponibles para filtros. |

## Consumo de API REST de terceros

| Metodo | Ruta | Proteccion | Uso |
|---|---|---|---|
| GET | `/external/products` | Publico | Consume `https://dummyjson.com/products?limit=6`; si falla, responde JSON con `fallback: true` y datos vacios. |

## CRUD de categorias

| Metodo | Ruta | Proteccion | Uso |
|---|---|---|---|
| GET | `/admin/categories` | Cookie de sesion | Lista categorias. |
| POST | `/admin/categories` | Cookie de sesion | Crea una categoria. |
| PUT | `/admin/categories/{id}` | Cookie de sesion | Edita una categoria. |
| DELETE | `/admin/categories/{id}` | Cookie de sesion | Elimina una categoria si no tiene dispositivos asociados. |

## Contrato POST de categorias

### POST `/admin/categories`

Requiere la cookie `medily_session` obtenida en `/auth/login`.

Headers:

```http
Content-Type: application/json
Cookie: medily_session=<token-de-sesion>
```

Request body:

```json
{
  "name": "Gama economica",
  "description": "Dispositivos funcionales para presupuestos ajustados"
}
```

Respuesta `201 Created`:

```json
{
  "id": 3,
  "name": "Gama economica",
  "description": "Dispositivos funcionales para presupuestos ajustados"
}
```

Errores posibles:

- `400 Bad Request`: el nombre esta vacio o el JSON no es valido.
- `401 Unauthorized`: no existe una sesion administrativa valida.
- `409 Conflict`: ya existe una categoria con el mismo nombre.

> Nota: `/external/products` es un consumo de terceros de solo lectura. El
> proveedor externo se consulta con GET; los productos propios de Medily se
> crean con el POST `/devices` documentado anteriormente.

## Salud de la base de datos

| Metodo | Ruta | Proteccion | Uso |
|---|---|---|---|
| GET | `/health/database` | Publico | Verifica la conexion con MySQL. Se conserva para pruebas tecnicas; ya no se muestra en el index. |

## Respuestas comunes

- `200 OK`: consulta, edicion, login o cierre de sesion exitosos.
- `201 Created`: registro creado correctamente.
- `204 No Content`: registro eliminado correctamente.
- `401 Unauthorized`: falta una cookie de sesion valida.
- `404 Not Found`: recurso inexistente.
- `409 Conflict`: no se puede eliminar una categoria con dispositivos asociados.
