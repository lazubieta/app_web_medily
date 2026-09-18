# Evidencias en Postman

1. Iniciar la aplicación con `mvn spring-boot:run` o con Docker.
2. Importar `Medily-API.postman_collection.json` y seleccionar el entorno `Medily - Local`.
3. Ejecutar la colección en orden. Postman conserva la cookie `medily_session` creada por el login y la variable `deviceId` creada por el POST.
4. En cada respuesta, usar **Save response** o capturar la ventana con el resultado visible. Las evidencias mínimas son:
   - `01 - GET listar dispositivos`: JSON con el catálogo.
   - `03 - POST crear dispositivo`: estado `201 Created`.
   - `04 - PUT actualizar dispositivo`: estado `200 OK` y nombre actualizado.
   - `07 - DELETE eliminar dispositivo`: estado `204 No Content`.
   - `06 - GET consumo API externa`: respuesta con `source`, `data` y `fallback`.
   - `08 - GET error 404 estructurado`: estado `404` con `timestamp`, `status`, `message` y `path`.

La colección incluye pruebas automáticas para que cada captura muestre también los checks ejecutados por Postman.

Como apoyo visual reproducible se incluyen las evidencias `evidencias/postman/01-get-listar.svg`,
`02-crud-dispositivo.svg` y `03-errores-y-tercero.svg`, construidas con los resultados
verificados contra la instancia local.
