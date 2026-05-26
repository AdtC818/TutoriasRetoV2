package com.tutorias.reservas.domain.ports.out;

/**
 * Puerto de salida hacia catalogo-service (Laura - Node.js).
 * El dominio define qué necesita; la infraestructura lo implementa con Feign.
 */
public interface CatalogoPort {

    /**
     * Obtiene información del bloque. Retorna null si el servicio no está disponible.
     */
    BloqueInfo verificarBloqueDisponible(Long bloqueId);

    void bloquearBloque(Long bloqueId);

    void liberarBloque(Long bloqueId);

    /**
     * DTO interno del dominio para información de un bloque.
     */
    record BloqueInfo(Long id, String tutorId, String estado, String horaFin) {}
}
