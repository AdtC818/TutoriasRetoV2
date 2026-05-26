package com.tutorias.reservas.application.queries;

/**
 * Query para obtener las próximas tutorías de un usuario.
 * @param usuarioId ID del estudiante o tutor
 * @param rol       "estudiante" o "tutor"
 */
public record GetProximasQuery(String usuarioId, String rol) {}
