package com.tutorias.reservas.application.queries;

import com.tutorias.reservas.domain.model.Reserva;
import com.tutorias.reservas.domain.ports.out.ReservaRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Handler de la query GetPendientesAsistencia.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GetPendientesAsistenciaQueryHandler {

    private final ReservaRepositoryPort repositorio;

    @Transactional(readOnly = true)
    public List<Reserva> handle(GetPendientesAsistenciaQuery query) {
        log.debug("GetPendientesAsistenciaQuery: tutor={}", query.tutorId());
        return repositorio.findPendientesAsistencia(query.tutorId(), LocalDate.now());
    }
}
