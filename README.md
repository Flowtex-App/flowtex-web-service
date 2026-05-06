# flowtex-web-service

Backend del proyecto **FLOWTEX**.

> El stack todavía no está definido (lenguaje, framework, ORM, base de datos). Este repositorio fija únicamente la **arquitectura** y la organización de carpetas. Una vez decidido el stack, los archivos se crean dentro de esta estructura sin modificarla.

## Arquitectura: DDD con Bounded Contexts y CQRS

El código se organiza por **bounded context** (no por capa técnica). Cada bounded context contiene las cuatro capas en su interior, en este orden de dependencia:

```
Interfaces → Application → Domain ← Infrastructure
```

- **Domain** no depende de nadie.
- **Application** depende solo de Domain.
- **Infrastructure** implementa interfaces declaradas en Domain (y a veces en Application).
- **Interfaces** depende de Application; nunca toca Domain ni Infrastructure directamente.

## Estructura por bounded context

```
src/<BoundedContext>/
├── Application/
│   └── Internal/
│       ├── CommandServices/      # Implementan los I*CommandService del Domain. Mutan estado.
│       ├── QueryServices/        # Implementan los I*QueryService del Domain. Solo lectura.
│       └── OutboundServices/     # Wrappers a servicios externos para aislar al Domain.
├── Domain/
│   ├── Model/
│   │   ├── Aggregates/           # Aggregate roots (entidades raíz con invariantes).
│   │   ├── Commands/             # records: representan intención de mutación.
│   │   ├── Entities/             # Entidades hijas dentro de un aggregate.
│   │   ├── Events/               # Eventos de dominio que emiten los aggregates.
│   │   ├── Queries/              # records: representan solicitudes de lectura.
│   │   └── ValueObjects/         # Tipos sin identidad, inmutables.
│   ├── Repositories/             # Interfaces de repositorio (una por aggregate root).
│   └── Services/                 # Interfaces I*CommandService / I*QueryService.
├── Infrastructure/
│   ├── Hashing/                  # (opcional) Implementaciones de hashing.
│   ├── Persistence/
│   │   └── Repositories/         # Implementaciones concretas de los IRepository del Domain.
│   ├── Pipeline/Middleware/      # (opcional) Middlewares específicos del contexto.
│   └── Tokens/                   # (opcional) Implementaciones de generación/validación de tokens.
└── Interfaces/
    ├── ACL/                      # Anti-Corruption Layer: facade para que otros contextos consuman este.
    └── REST/
        ├── Controllers/          # Endpoints HTTP. Solo orquestan: traducen Resource → Command/Query.
        ├── Resources/            # DTOs de entrada/salida del API.
        └── Transform/            # Assemblers Resource ↔ Command/Query/Aggregate.
```

## Bounded contexts iniciales

- **IAM** — Identity & Access Management. Sign-up, sign-in, gestión de usuarios y roles.
- **Shared** — infraestructura transversal: `BaseRepository`, `UnitOfWork`, `AppDbContext` (o equivalente), configuración HTTP global, naming conventions.

A medida que el dominio se modele, se agregan más bounded contexts en `src/` siguiendo la misma estructura.

## Reglas CQRS

- **Commands** mutan estado y devuelven un acuse (id, status), no entidades completas.
- **Queries** solo leen y nunca pasan por aggregates: leen de read models / proyecciones optimizadas.
- Hay un servicio dedicado para commands (`<Aggregate>CommandService`) y otro para queries (`<Aggregate>QueryService`). Nunca se mezclan.
- Aggregates fuerzan invariantes: nada de setters públicos, comportamiento expuesto solo vía métodos.

## Comunicación entre bounded contexts

No se importa código directamente entre contextos. Cuando un contexto necesita información de otro:

1. El contexto consumidor define una interfaz `I<Otro>ExternalService` en su `Application/Internal/OutboundServices/`.
2. El contexto proveedor expone un `<Otro>ContextFacade` en su `Interfaces/ACL/`.
3. La inyección de dependencias en el composition root cablea el facade como implementación del external service.

Esto mantiene cada contexto reemplazable y testeable en aislamiento.

## Convenciones

- Naming: las convenciones del lenguaje del stack elegido.
- Commits: Conventional Commits (`feat:`, `fix:`, `refactor:`, `test:`, `chore:`, `docs:`).
- Ramas: trunk-based. `main` siempre desplegable, feature branches cortas.
- Tests obligatorios para: aggregates (unit), command/query handlers (unit), endpoints críticos (integration).

## Documentación

Los documentos académicos y los ADRs (Architecture Decision Records) viven en los repos de workspace, no acá:

- `calidad-flowtex-workspace`
- `desarrollo-agile-flowtex-workspace`

## Cómo arrancar

Requisitos: Java 21, Maven 3, MySQL 8 corriendo localmente.

```bash
# 1. Copiar variables de entorno
cp .env.example .env   # ajustar DB_URL, DB_USER, DB_PASS, JWT_SECRET

# 2. Instalar dependencias
mvn clean install -DskipTests

# 3. Correr en desarrollo (Flyway migra automáticamente)
mvn spring-boot:run

# 4. Tests
mvn test

# 5. Build de producción
mvn clean package
```
