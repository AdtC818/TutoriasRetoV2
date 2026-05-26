package com.tutorias.reservas.infrastructure.feign;

import com.tutorias.reservas.domain.ports.out.CatalogoPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida: implementa CatalogoPort usando CatalogoFeignClient.
 * Traduce entre el DTO de infraestructura y el record del dominio.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CatalogoFeignAdapter implements CatalogoPort {

    private final CatalogoFeignClient feignClient;

    @Override
    public BloqueInfo verificarBloqueDisponible(Long bloqueId) {
        try {
            CatalogoFeignClient.BloqueDisponibilidadDTO dto =
                    feignClient.verificarBloqueDisponible(bloqueId);
            if (dto == null) return null;
            return new BloqueInfo(dto.getId(), dto.getTutorId(), dto.getEstado(), dto.getHoraFin());
        } catch (Exception e) {
            log.error("Error consultando bloque {} en catálogo: {}", bloqueId, e.getMessage());
            return null;
        }
    }

    @Override
    public void bloquearBloque(Long bloqueId) {
        feignClient.bloquearBloque(bloqueId);
    }

    @Override
    public void liberarBloque(Long bloqueId) {
        feignClient.liberarBloque(bloqueId);
    }
}
