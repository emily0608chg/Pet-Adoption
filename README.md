Pet Adoption API (Spring Boot)

**🗂 Estructura del Proyecto**

La aplicación está diseñada siguiendo el patrón Diseño Orientado a Capas para lograr una separación clara de responsabilidades. A continuación, se detalla la estructura general de los paquetes principales y su propósito:

* com.petadoption.model Contiene las entidades JPA que representan las tablas de la base de datos. Estas clases están anotadas con @Entity y se utilizan para mapear objetos al modelo relacional.

* com.petadoption.repository Contiene las interfaces de repositorios que extienden Spring Data JPA. Estas interfaces son responsables de interactuar con la base de datos para realizar operaciones CRUD básicas o consultas personalizadas.

* com.petadoption.service Define los servicios (lógica de negocio) de la aplicación. Aquí se implementan las reglas de negocio y se integra la lógica entre los repositorios y los controladores.

* com.petadoption.controller Contiene los endpoints REST de la API. Las clases en este paquete están anotadas con @RestController y manejan las solicitudes y respuestas HTTP.

* com.petadoption.config Contiene configuraciones personalizadas como seguridad (Spring Security), claves de JWT, manejo de cors, entre otros.

* com.petadoption.model.enums Contiene enumeraciones para representar estados o valores constantes, como el estado de las mascotas (AVAILABLE, ADOPTED, DISABLED) dentro del sistema.

* com.petadoption.customexceptions Aquí se maneja cualquier excepción personalizada o lógica para manejar errores dentro de la aplicación.

* com.petadoption.dto Contiene los Data Transfer Objects (DTOs) utilizados para transferir datos entre el cliente y el servidor durante procesos como registro, autenticación y actualización de tokens. LoginDTO: Recolecta los datos durante el inicio de sesión. RegisterDTO: Define los datos necesarios para registrar un nuevo usuario. RefreshTokenDTO: Gestiona la solicitud de renovación de tokens JWT. UserDTO: Estructura para exponer información del usuario (sin exponer datos sensibles como la contraseña).

* com.petadoption.resources Contiene recursos estáticos importantes para el funcionamiento de la aplicación. 
keys/: private_key_base64.pem: Clave privada usada para firmar tokens JWT. public_key_base64.pem: Clave pública usada para la validación de tokens.
http/: Subcarpetas que contienen solicitudes para pruebas de endpoints.

adoption/: Solicitudes y respuestas relacionadas con adopciones. auth/: Solicitudes de autenticación (login, registro, etc.). 
pet/: Solicitudes para la gestión de mascotas.
user/: Solicitudes para la gestión de los usuarios.

test/: Conjunto de pruebas automatizadas para garantizar la estabilidad y exactitud de las funcionalidades principales.
Test Endpoint Autenticación (AuthControllerTests): Validación de login y registro de usuarios. Pruebas de tokens de acceso y actualización.
Test Endpoint Adopciones (AdoptionControllerTests): Gestión de solicitudes de adopción. Pruebas con usuarios autenticados y no autenticados.
Test Endpoint Mascotas (PetControllerTests): CRUD de mascotas. Validación de filtros como el estado de la mascota (AVAILABLE, ADOPTED).
Test Endpoint Usuarios (UserControllerTests): Gestión de perfiles de usuarios.
Estas pruebas están diseñadas para ejecutarse en entornos controlados y aseguran que los endpoints cumplen con los requisitos funcionales definidos.

📊** -Diagrama Entidad-Relación (ERD)** 

A continuación, un modelo lógico para representar las relaciones entre las entidades más importantes del sistema:
[User] 1 ----- n [Adoption] n ----- 1 [Pet] n ----- 1 [TypeOfPet]
Un Usuario puede realizar múltiples Adopciones. Cada Mascota registrada tiene un único Tipo de Mascota. Una Adopción conecta un Usuario con una Mascota.


