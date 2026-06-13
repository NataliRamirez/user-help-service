# User Help Service - Qvenly

Microservicio Maven Spring Boot para el Centro de Ayuda de Qvenly.

## Alcance implementado

- HU212: acceso al centro de ayuda.
- HU213: manual de usuario desde contenido estático.
- HU214: búsqueda y sugerencias.
- HU215: chatbot con Gemini y fallback local.
- HU216: redirección a soporte cuando no puede responder.
- HU218: registro de solicitudes de soporte con validación y anti-duplicado.
- HU219: ver mis solicitudes de soporte.
- HU220: consultar solicitudes como administrador.
- HU221: responder solicitudes como administrador.

## Fuera de alcance por decisión actual

- HU217: historial de conversaciones del chatbot.
- Persistencia real en base de datos.
- JPA/MySQL.
- Modificaciones en Auth Service, Gateway o Frontend.

Las solicitudes de soporte se guardan temporalmente en memoria y se pierden al reiniciar el servicio.

## Estructura aplicada

```text
src/main/java/com/qvenly/userhelp
├── config
├── controllers
├── exceptions
├── models
│   ├── dto
│   ├── enums
│   └── entity
└── services
```

Reglas aplicadas:

- Todos los DTO están en `models/dto`.
- Todos los enums están en `models/enums`.
- Las clases de dominio están en `models/entity`.
- Todos los DTO finalizan en `DTO.java`.
- No existe `SupportTicketRequestDTO`.
- `ChatRequestDTO` solo recibe `message`.
- `CreateSupportTicketDTO` solo recibe `type`, `description` y `priority`.

## Integración con Auth Service

El servicio queda preparado para integración con Auth Service/Gateway.

El frontend no debe enviar `userId`, `userEmail` ni `role` en el body ni en query params.

Por ahora, el microservicio espera que Gateway/Auth Service propague estos headers internos:

```http
X-Auth-User-Id: <id-del-usuario-autenticado>
X-Auth-User-Email: <correo-del-usuario-autenticado>
X-Auth-User-Role: ADMIN | ORGANIZER | USER
```

También puede recibirse `Authorization: Bearer <token>`, pero la validación real del token queda encapsulada para conectarse después con Auth Service.

## Variables de entorno

```powershell
$env:GEMINI_API_KEY="TU_API_KEY"
$env:GEMINI_MODEL="gemini-1.5-flash"
$env:SERVER_PORT="8085"
```

## Ejecutar

```powershell
mvn clean test
mvn spring-boot:run
```

## Endpoints principales

- `GET /api/help/home`
- `GET /api/help/categories`
- `GET /api/help/categories/{slug}`
- `GET /api/help/manual`
- `GET /api/help/manual/sections`
- `GET /api/help/manual/{sectionId}`
- `GET /api/help/search?query=perfil`
- `GET /api/help/suggestions`
- `POST /api/help/chat`
- `POST /api/help/support`
- `GET /api/help/support/my`
- `GET /api/help/admin/support`
- `GET /api/help/admin/support/{id}`
- `POST /api/help/admin/support/{id}/response`

## Ejemplo chatbot

```http
POST /api/help/chat
X-Auth-User-Id: 123
X-Auth-User-Email: usuario@qvenly.com
X-Auth-User-Role: ORGANIZER
Content-Type: application/json
```

```json
{
  "message": "¿Cómo puedo crear un evento?"
}
```

## Ejemplo soporte

```http
POST /api/help/support
X-Auth-User-Id: 123
X-Auth-User-Email: usuario@qvenly.com
X-Auth-User-Role: USER
Content-Type: application/json
```

```json
{
  "type": "TECHNICAL",
  "description": "No puedo abrir el centro de ayuda desde el menú principal",
  "priority": "HIGH"
}
```

## Nota importante

No se hizo push, no se agregó base de datos, no se implementó HU217 y no se agregaron secretos.
