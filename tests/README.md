# Tests

- `UnitTests/` — tests aislados de aggregates, value objects, command services y query services. Sin I/O.
- `IntegrationTests/` — tests con DB real (o in-memory). Cubren el camino Controller → Application → Infrastructure.
- `E2ETests/` — tests sobre el API ya levantado. Validan flujos end-to-end.
