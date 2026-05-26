package com.tutorias.reservas.application.commands;

/**
 * Comando para cancelar una reserva existente.
 */
public record CancelarReservaCommand(
        Long reservaId,
        String estudianteId
) {}
