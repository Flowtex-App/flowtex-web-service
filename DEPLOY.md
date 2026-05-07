# Deploy del backend FLOWTEX

## Stack

- Java 21 + Spring Boot 3.3
- MySQL 8 (prod) / H2 in-memory (dev)
- Flyway para migraciones (con seed de 6 usuarios y 6 formularios)
- JWT para autenticacion
- Groq API para sugerencias de campos con IA

## Variables de entorno

| Variable | Requerido | Default | Descripcion |
|---|---|---|---|
| `SPRING_PROFILES_ACTIVE` | si | `dev` | `dev` (H2) o `prod` (MySQL) |
| `PORT` | no | `8080` | Puerto del servidor |
| `SPRING_DATASOURCE_URL` | prod | — | JDBC URL de MySQL |
| `SPRING_DATASOURCE_USERNAME` | prod | — | Usuario DB |
| `SPRING_DATASOURCE_PASSWORD` | prod | — | Password DB |
| `JWT_SECRET` | si | placeholder | Min 32 bytes |
| `JWT_EXPIRATION_MS` | no | `86400000` | 24 horas |
| `CORS_ORIGINS` | si | localhost | Origins permitidos (separados por coma) |
| `GROQ_API_KEY` | si (IA) | vacio | Token de console.groq.com |
| `GROQ_MODEL` | no | `llama-3.1-8b-instant` | Modelo de Groq |

> Si `GROQ_API_KEY` no se configura, el backend usa un fallback heuristico local
> que igualmente devuelve sugerencias para que la app funcione end-to-end.

## Local (Dev)

```bash
# Ejecutar con H2 en memoria
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run

# Health check
curl http://localhost:8080/api/v1/health

# Sign-in con usuario demo
curl -X POST http://localhost:8080/api/v1/authentication/sign-in \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"Flowtex2026!"}'
```

Swagger UI: http://localhost:8080/swagger-ui.html

## Render (Docker)

1. Push del repo al GitHub
2. En Render: New Web Service, conectar repo, seleccionar `Dockerfile` build
3. Configurar variables de entorno (ver tabla)
4. Apuntar `SPRING_DATASOURCE_URL` a tu MySQL de Railway / Aiven / PlanetScale
5. Health check path: `/api/v1/health`

El archivo `render.yaml` incluido permite Blueprint deploy con un clic.

## Railway

1. New Project, deploy from GitHub repo
2. Railway detectara `railway.json` + `Dockerfile`
3. Anadir plugin MySQL: copiar `MYSQL_URL` a `SPRING_DATASOURCE_URL`
4. Anadir variables: `JWT_SECRET`, `GROQ_API_KEY`, `CORS_ORIGINS`

## Database (MySQL gratuito)

Opciones gratis:
- **Railway** plan free: 500MB MySQL
- **Aiven** plan free: 1mes trial
- **PlanetScale** Hobby: 5GB

Las migraciones de Flyway corren automaticamente al primer arranque y crean:
- 4 roles
- 6 usuarios (passwords: `Flowtex2026!`)
- 6 formularios con sus campos

## Endpoints clave

```
POST /api/v1/authentication/sign-up
POST /api/v1/authentication/sign-in        -> JWT
GET  /api/v1/users/me
GET  /api/v1/forms                         -> lista
POST /api/v1/forms                         -> crear
PUT  /api/v1/forms/{id}                    -> actualizar
POST /api/v1/forms/{id}/publish            -> publicar
DELETE /api/v1/forms/{id}                  -> eliminar
POST /api/v1/forms/suggestions/fields      -> IA: sugerir campos
```
