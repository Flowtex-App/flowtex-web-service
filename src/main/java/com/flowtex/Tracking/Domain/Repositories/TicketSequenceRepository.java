package com.flowtex.Tracking.Domain.Repositories;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

/**
 * Genera códigos de ticket secuenciales sin colisiones.
 * Formato: FTX-2026-00001 (FTX, año actual, padded).
 *
 * Los nombres de columna evitan keywords contextuales de TiDB (la versión
 * Serverless rechaza `name` y `last_value` en CREATE TABLE).
 */
@Repository
public class TicketSequenceRepository {

    private final EntityManager em;

    public TicketSequenceRepository(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public String nextTicketCode() {
        em.createNativeQuery(
                "UPDATE ticket_sequence SET seq_value = seq_value + 1 WHERE seq_name = 'FTX'")
                .executeUpdate();
        Number value = (Number) em.createNativeQuery(
                "SELECT seq_value FROM ticket_sequence WHERE seq_name = 'FTX'")
                .getSingleResult();
        long n = value.longValue();
        return String.format("FTX-%d-%05d", Year.now().getValue(), n);
    }
}
