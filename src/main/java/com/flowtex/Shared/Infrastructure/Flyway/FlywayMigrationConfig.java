package com.flowtex.Shared.Infrastructure.Flyway;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Antes de cada `migrate()`, llama a `repair()` para:
 *   - eliminar del historial las migraciones que quedaron en estado FAILED,
 *   - re-alinear checksums de migraciones aplicadas si hubieran cambiado.
 *
 * Esto hace que el arranque sea auto-curativo cuando una migración previa falló
 * a mitad de camino (ejemplo histórico: V11 contra TiDB Serverless tropezó con
 * el keyword `last_value` y dejó la fila marcada como FAILED).
 *
 * En deploys donde no hay nada que reparar, `repair()` es un no-op, así que no
 * tiene costo en condiciones normales.
 */
@Configuration
public class FlywayMigrationConfig {

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
            flyway.repair();
            flyway.migrate();
        };
    }
}
