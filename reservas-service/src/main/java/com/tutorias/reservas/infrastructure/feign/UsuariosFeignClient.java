package com.tutorias.reservas.infrastructure.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Cliente Feign hacia usuarios-service (Elías - FastAPI en puerto 8000).
 */
@FeignClient(
    name = "usuarios-service",
    url = "${servicios.usuarios-url}",
    fallback = UsuariosFeignClient.UsuariosFallback.class
)
public interface UsuariosFeignClient {

    @GetMapping("/api/usuarios/{usuarioId}/existe")
    boolean verificarUsuarioExiste(@PathVariable("usuarioId") String usuarioId);

    @GetMapping("/api/tutores/{tutorId}/tiene-materia")
    boolean verificarTutorTieneMateria(
            @PathVariable("tutorId") String tutorId,
            @RequestParam("materiaId") String materiaId);

    // Fallback por si usuarios-service no está disponible
    class UsuariosFallback implements UsuariosFeignClient {
        @Override public boolean verificarUsuarioExiste(String id) { return false; }
        @Override public boolean verificarTutorTieneMateria(String tid, String mid) { return false; }
    }
}
