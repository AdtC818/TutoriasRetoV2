package com.tutorias.reservas.infrastructure.feign;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Cliente Feign hacia catalogo-service (Laura - Node.js en puerto 3000).
 */
@FeignClient(
    name = "catalogo-service",
    url = "${servicios.catalogo-url}",
    fallback = CatalogoFeignClient.CatalogoFallback.class
)
public interface CatalogoFeignClient {

    @GetMapping("/api/bloques/{bloqueId}/disponible")
    BloqueDisponibilidadDTO verificarBloqueDisponible(
            @PathVariable("bloqueId") Long bloqueId);

    @PutMapping("/api/bloques/{bloqueId}/bloquear")
    void bloquearBloque(@PathVariable("bloqueId") Long bloqueId);

    @PutMapping("/api/bloques/{bloqueId}/liberar")
    void liberarBloque(@PathVariable("bloqueId") Long bloqueId);

    @GetMapping("/api/materias/{materiaId}/activa")
    boolean verificarMateriaActiva(@PathVariable("materiaId") String materiaId);

    // DTO de respuesta del catálogo
    @Data
    @NoArgsConstructor
    class BloqueDisponibilidadDTO {
        private Long id;
        private String tutorId;
        private String diaSemana;
        private String horaInicio;
        private String horaFin;
        @JsonProperty("fecha_inicio")
        private String fechaInicio;
        @JsonProperty("fecha_fin")
        private String fechaFin;
        private String estado;
    }

    // Fallback por si catalogo-service no está disponible
    class CatalogoFallback implements CatalogoFeignClient {
        @Override public BloqueDisponibilidadDTO verificarBloqueDisponible(Long id) { return null; }
        @Override public void bloquearBloque(Long id) {}
        @Override public void liberarBloque(Long id) {}
        @Override public boolean verificarMateriaActiva(String id) { return false; }
    }
}
