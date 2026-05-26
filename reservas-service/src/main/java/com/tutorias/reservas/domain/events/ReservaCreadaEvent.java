package com.tutorias.reservas.domain.events;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Evento de dominio publicado cuando una nueva reserva es creada exitosamente.
 */
public record ReservaCreadaEvent(
        String estudianteId,
        String tutorId,
        Long bloqueDisponibilidadId,
        LocalDate fechaSesion,
        LocalDateTime ocurridoEn
) {
    public ReservaCreadaEvent(String estudianteId, String tutorId,
                               Long bloqueDisponibilidadId, LocalDate fechaSesion) {
        this(estudianteId, tutorId, bloqueDisponibilidadId, fechaSesion, LocalDateTime.now());
    }
}
