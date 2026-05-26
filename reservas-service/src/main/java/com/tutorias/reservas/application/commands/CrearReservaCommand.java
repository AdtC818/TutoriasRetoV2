package com.tutorias.reservas.application.commands;

import java.time.LocalDate;

/**
 * Comando para crear una nueva reserva de tutoría.
 */
public record CrearReservaCommand(
        String estudianteId,
        String tutorId,
        Long bloqueDisponibilidadId,
        String materiaId,
        LocalDate fechaSesion,
        String notasEstudiante
) {}
