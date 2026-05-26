package com.tutorias.reservas.application.queries;

import com.tutorias.reservas.domain.model.Reserva;
import com.tutorias.reservas.domain.ports.out.ReservaRepositoryPort;
import com.tutorias.reservas.exception.ReservaNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler de la query GetById.
 */
@Component
@RequiredArgsConstructor
public class GetByIdQueryHandler {

    private final ReservaRepositoryPort repositorio;

    @Transactional(readOnly = true)
    public Reserva handle(GetByIdQuery query) {
        return repositorio.findById(query.reservaId())
                .orElseThrow(() -> new ReservaNotFoundException(query.reservaId()));
    }
}
