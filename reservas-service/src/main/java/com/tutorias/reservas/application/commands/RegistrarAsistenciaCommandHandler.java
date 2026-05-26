package com.tutorias.reservas.application.commands;

import com.tutorias.reservas.domain.model.Reserva;
import com.tutorias.reservas.domain.ports.out.ReservaRepositoryPort;
import com.tutorias.reservas.exception.ReservaNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler del comando RegistrarAsistencia.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RegistrarAsistenciaCommandHandler {

    private final ReservaRepositoryPort repositorio;

    @Transactional
    public Reserva handle(RegistrarAsistenciaCommand cmd) {
        log.info("Procesando RegistrarAsistenciaCommand: reservaId={}, estado={}",
                cmd.reservaId(), cmd.estado());

        Reserva reserva = repositorio.findById(cmd.reservaId())
                .orElseThrow(() -> new ReservaNotFoundException(cmd.reservaId()));

        // Delegar lógica de negocio al aggregate
        reserva.registrarAsistencia(cmd.estado(), cmd.tutorId());

        Reserva actualizada = repositorio.save(reserva);
        log.info("Asistencia registrada para reserva {}.", cmd.reservaId());
        return actualizada;
    }
}
