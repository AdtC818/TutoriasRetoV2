package com.tutorias.reservas.application.commands;

import com.tutorias.reservas.domain.model.EstadoReserva;

/**
 * Comando para registrar asistencia (COMPLETADA o INASISTENCIA) de una sesión.
 */
public record RegistrarAsistenciaCommand(
        Long reservaId,
        String tutorId,
        EstadoReserva estado
) {}
