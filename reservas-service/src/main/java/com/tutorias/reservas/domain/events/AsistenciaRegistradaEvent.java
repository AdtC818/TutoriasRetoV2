package com.tutorias.reservas.domain.events;

import com.tutorias.reservas.domain.model.EstadoReserva;

import java.time.LocalDateTime;

public record AsistenciaRegistradaEvent(
        Long reservaId,
        String tutorId,
        String estudianteId,
        EstadoReserva estado,
        LocalDateTime ocurridoEn
) {
    public AsistenciaRegistradaEvent(Long reservaId, String tutorId,
                                     String estudianteId, EstadoReserva estado) {
        this(reservaId, tutorId, estudianteId, estado, LocalDateTime.now());
    }
}
