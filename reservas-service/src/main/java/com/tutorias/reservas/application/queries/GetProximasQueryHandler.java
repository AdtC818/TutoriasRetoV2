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
 * Handler de la query GetProximas.
 * Retorna las próximas tutorías activas de un estudiante o tutor.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GetProximasQueryHandler {

    private final ReservaRepositoryPort repositorio;

    @Transactional(readOnly = true)
    public List<Reserva> handle(GetProximasQuery query) {
        log.debug("GetProximasQuery: usuario={}, rol={}", query.usuarioId(), query.rol());

        return "tutor".equalsIgnoreCase(query.rol())
                ? repositorio.findProximasTutor(query.usuarioId(), LocalDate.now())
                : repositorio.findProximasEstudiante(query.usuarioId(), LocalDate.now());
    }
}
