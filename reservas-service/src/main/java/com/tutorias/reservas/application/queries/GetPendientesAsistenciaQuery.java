package com.tutorias.reservas.application.queries;

/**
 * Query para obtener las sesiones pasadas del tutor que aún no tienen asistencia registrada.
 */
public record GetPendientesAsistenciaQuery(String tutorId) {}
