package com.tutorias.reservas.domain.events;

import java.time.LocalDateTime;

/**
 * Evento de dominio publicado cuando una reserva es cancelada.
 * Lleva el bloqueDisponibilidadId para que catalogo-service pueda liberar el bloque.
 */
public record ReservaCanceladaEvent(
        Long reservaId,
        Long bloqueDisponibilidadId,
        LocalDateTime ocurridoEn
) {
    public ReservaCanceladaEvent(Long reservaId, Long bloqueDisponibilidadId) {
        this(reservaId, bloqueDisponibilidadId, LocalDateTime.now());
    }
}
