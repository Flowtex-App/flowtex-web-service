# IAM bounded context

**Identity & Access Management.** Responsable de la identidad de los usuarios del sistema (sign-up, sign-in), la gestión de roles, y los tokens de autenticación.

## Aggregates

- `User` — usuario del sistema (username, email, password hash, role).

## Roles

- `Admin` — administrador.
- `User` — usuario regular.

> Ajustar la enumeración cuando el modelo de dominio se complete.

## Endpoints públicos esperados

- `POST /api/v1/authentication/sign-up`
- `POST /api/v1/authentication/sign-in`
- `GET /api/v1/users/{id}` (protegido)
- `GET /api/v1/users` (protegido, admin)

## ACL expuesta a otros contextos

- `IUsersContextFacade` — permite a otros bounded contexts validar existencia de un usuario, obtener su rol, etc., sin acoplarse al aggregate User directamente.
