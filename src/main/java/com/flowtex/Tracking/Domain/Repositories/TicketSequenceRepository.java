package com.flowtex.Tracking.Domain.Repositories;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

/**
 * Genera códigos de ticket secuenciales sin colisiones.
 * Formato: FTX-2026-00001 (FTX, año actual, padded).
 *
 * Implementación atómica: UPDATE last_value = last_value + 1 dentro de una
 * transacción separada (REQUIRES_NEW), luego SELECT del valor.
 */
@Repository
public class TicketSequenceRepository {

    private final EntityManager em;

    public TicketSequenceRepository(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public String nextTicketCode() {
        em.createNativeQuery("UPDATE ticket_sequence SET last_value = last_value + 1 WHERE name = 'FTX'")
                .executeUpdate();
        Number value = (Number) em.createNativeQuery("SELECT last_value FROM ticket_sequence WHERE name = 'FTX'")
                .getSingleResult();
        long n = value.longValue();
        return String.format("FTX-%d-%05d", Year.now().getValue(), n);
    }
}
