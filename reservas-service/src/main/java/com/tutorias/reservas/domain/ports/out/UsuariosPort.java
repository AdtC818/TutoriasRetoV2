package com.tutorias.reservas.domain.ports.out;

/**
 * Puerto de salida hacia usuarios-service (Elías - FastAPI/Python).
 * El dominio define qué necesita; la infraestructura lo implementa con Feign.
 */
public interface UsuariosPort {

    boolean verificarUsuarioExiste(String usuarioId);

    boolean verificarTutorTieneMateria(String tutorId, String materiaId);
}
