package com.tutorias.reservas.infrastructure.feign;

import com.tutorias.reservas.domain.ports.out.UsuariosPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida: implementa UsuariosPort usando UsuariosFeignClient.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UsuariosFeignAdapter implements UsuariosPort {

    private final UsuariosFeignClient feignClient;

    @Override
    public boolean verificarUsuarioExiste(String usuarioId) {
        try {
            return feignClient.verificarUsuarioExiste(usuarioId);
        } catch (Exception e) {
            log.error("Error verificando usuario {}: {}", usuarioId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean verificarTutorTieneMateria(String tutorId, String materiaId) {
        try {
            return feignClient.verificarTutorTieneMateria(tutorId, materiaId);
        } catch (Exception e) {
            log.error("Error verificando materia del tutor {}: {}", tutorId, e.getMessage());
            return false;
        }
    }
}
