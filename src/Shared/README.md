# Shared

Infraestructura transversal del proyecto. **No es un bounded context de negocio**: solo contiene piezas reutilizables por todos los contextos.

## Contenido esperado

- `Domain/Repositories/` — interfaces base (`IBaseRepository<T>`, `IUnitOfWork`).
- `Application/Internal/OutboundServices/` — interfaces de servicios externos comunes (hashing, tokens, email, storage). Los contextos consumen estas abstracciones, no las implementaciones.
- `Infrastructure/Persistence/Configuration/` — configuración del DbContext (o equivalente), mapeos compartidos, naming conventions (snake_case + plural, etc.).
- `Infrastructure/Persistence/Repositories/` — implementación de `BaseRepository<T>` y `UnitOfWork`.
- `Interfaces/Configuration/` — convenciones HTTP globales (kebab-case en URLs, manejo de errores, etc.).

## Regla

Shared no conoce de aggregates específicos. Si un archivo acá empieza a referenciar `User`, `Order`, etc., probablemente debe vivir en su bounded context.
