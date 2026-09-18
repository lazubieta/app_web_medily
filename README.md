MEDILY - API REST Y TIENDA ONLINE
================================

Proyecto entregable para la actividad de arquitectura REST, consumo de API de
terceros, pruebas con Postman, control de errores y despliegue. Medily es una
tienda/catalogo de dispositivos moviles con frontend web, panel administrativo
y API JSON.

1. TECNOLOGIAS Y ARQUITECTURA
------------------------------
- Java 17, Spring Boot 3.4.5, Spring MVC y Spring Data JPA.
- H2 para ejecucion local rapida y MySQL 8 para despliegue.
- Bootstrap 5 y JavaScript vanilla en el frontend.
- Arquitectura MVC por capas: controller, service, repository y model.
- JSON como formato de entrada y salida.
- BCrypt para la contrasena del usuario administrador.

La tabla principal expuesta por la API es `devices`. Sus caracteristicas se
guardan en `device_features` mediante una relacion JPA `@ElementCollection`.
La columna `devices.category` referencia `categories.name` en el respaldo SQL,
conservando el contrato JSON que ya usa el frontend y evitando categorias
huérfanas.

2. REQUISITOS
-------------
- JDK 17 o superior.
- Maven 3.9 o superior para ejecucion local.
- Navegador actualizado.
- Para MySQL: Docker Desktop y Docker Compose.

3. EJECUCION LOCAL CON H2
--------------------------
Desde esta carpeta:

    mvn spring-boot:run

Abrir:

    http://localhost:8080/
    http://localhost:8080/admin.html

La base H2 queda en `data/medily.mv.db` y se inicializa con datos de ejemplo.
Para generar y ejecutar el JAR:

    mvn clean package
    java -jar target/medily-backend-1.0.0.jar

4. EJECUCION CON MYSQL Y DOCKER
--------------------------------
El compose levanta MySQL, importa `database/medily_backup.sql` en el primer
arranque del volumen y levanta la API con el perfil `mysql`:

    docker compose up --build

Abrir `http://localhost:8080/`. El puerto de MySQL es `3307` en el equipo
host y `3306` entre contenedores.

5. USUARIO DE DEMOSTRACION
---------------------------
Usuario: admin
Correo: admin@medily.com
Contrasena: Medily2026*

El login crea la cookie HttpOnly `medily_session`. Postman la conserva en su
cookie jar; el navegador la envia automaticamente. En produccion se debe
cambiar la contrasena, usar HTTPS y almacenar sesiones en un proveedor
persistente.

6. API REST
-----------
Base URL: `http://localhost:8080/api`

Publicos:
GET    /devices?search=&brand=&category=   Lista dispositivos en JSON.
GET    /devices/{id}                       Consulta un dispositivo.
GET    /devices/options                    Marcas y categorias para filtros.
POST   /auth/login                         Inicia sesion.
GET    /auth/session                       Valida la sesion actual.
DELETE /auth/logout                        Cierra sesion.
GET    /external/products                  Consume DummyJSON y maneja fallback.

Protegidos por cookie de sesion:
POST   /devices                            Crea un dispositivo.
PUT    /devices/{id}                       Actualiza todos sus campos.
DELETE /devices/{id}                       Elimina el dispositivo y sus features.
GET    /admin/categories                   Lista categorias.
POST   /admin/categories                   Crea una categoria.
PUT    /admin/categories/{id}              Actualiza una categoria.
DELETE /admin/categories/{id}              Elimina si no tiene dispositivos.

Las respuestas exitosas usan 200, 201 o 204. Los errores devuelven JSON con
`timestamp`, `status`, `error`, `message` y `path` para facilitar diagnostico.

Ejemplo de POST/PUT:

    {
      "name": "Equipo de ejemplo",
      "brand": "Marca",
      "price": 1999000,
      "image": "https://sitio/imagen.jpg",
      "description": "Descripcion del equipo",
      "releaseDate": "2026-01-15",
      "category": "Gama media",
      "features": {"display": "6.5 pulgadas", "storage": "128 GB"}
    }

7. PRUEBAS CON POSTMAN
----------------------
Importar:

    postman/Medily-API.postman_collection.json
    postman/Medily-API.postman_environment.json

Ejecutar la coleccion en orden. Incluye GET, POST, PUT, DELETE, login,
consumo de API externa y un error 404 estructurado. Los scripts de Postman
validan automaticamente los estados HTTP, el JSON, la cookie y la variable
`deviceId`. Las instrucciones de captura estan en
`postman/INSTRUCCIONES_EVIDENCIAS.md`.

8. RESPALDO Y ESTRUCTURA
------------------------
`database/medily_backup.sql` contiene la copia logica MySQL con las tablas
`admin_users`, `categories`, `devices` y `device_features`, restricciones,
relaciones y datos de prueba.

src/main/java/co/medily/model          Entidades JPA y validaciones
src/main/java/co/medily/repository     Acceso a datos
src/main/java/co/medily/service        Reglas de negocio e integracion externa
src/main/java/co/medily/controller     Endpoints REST
src/main/java/co/medily/exception      Respuestas de error consistentes
src/main/resources/static               Frontend y estilos
src/test                                Pruebas de integracion CRUD con MockMvc

9. REFERENCIAS BIBLIOGRAFICAS
------------------------------
- Pavon, J. y Llarena, E. (2016). Creacion de un sitio web con PHP y MySQL.
  ISBN 978-958-762-517-2. https://crai.ucompensar.edu.co
- Eslava Munoz, V. (2018). El nuevo PHP: conceptos avanzados. Bubok.
- Cabezas Granado, L. M. y Gonzalez Lozano, F. J. (2021). Curso de PHP 8 y
  MySQL 8. Anaya Multimedia.
- Lopez, M. et al. (2015). Programacion web en el entorno cliente.
- Lopez, M. et al. (2016). Programacion web en el entorno servidor.
