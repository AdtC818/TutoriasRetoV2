package com.tutorias.reservas.application.queries;

import com.tutorias.reservas.domain.model.Reserva;
import com.tutorias.reservas.domain.ports.out.ReservaRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Handler de la query GetHistorial.
 * Retorna todas las tutorías del estudiante (COMPLETADAS e INASISTENCIAS).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GetHistorialQueryHandler {

    private final ReservaRepositoryPort repositorio;

    @Transactional(readOnly = true)
    public List<Reserva> handle(GetHistorialQuery query) {
        log.debug("GetHistorialQuery: estudiante={}", query.estudianteId());
        return repositorio.findHistorialEstudiante(query.estudianteId());
    }
}
