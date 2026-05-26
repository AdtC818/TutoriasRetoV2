package com.tutorias.reservas.application.commands;

import com.tutorias.reservas.domain.model.Reserva;
import com.tutorias.reservas.domain.ports.out.CatalogoPort;
import com.tutorias.reservas.domain.ports.out.EventPublisherPort;
import com.tutorias.reservas.domain.ports.out.ReservaRepositoryPort;
import com.tutorias.reservas.exception.ReservaNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler del comando CancelarReserva.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CancelarReservaCommandHandler {

    private final ReservaRepositoryPort repositorio;
    private final CatalogoPort catalogo;
    private final EventPublisherPort publisher;

    @Transactional
    public Reserva handle(CancelarReservaCommand cmd) {
        log.info("Procesando CancelarReservaCommand: reservaId={}, estudiante={}",
                cmd.reservaId(), cmd.estudianteId());

        // 1. Cargar el aggregate
        Reserva reserva = repositorio.findById(cmd.reservaId())
                .orElseThrow(() -> new ReservaNotFoundException(cmd.reservaId()));

        // 2. Delegar la lógica de negocio al aggregate
        reserva.cancelar(cmd.estudianteId());

        // 3. Persistir cambio
        Reserva actualizada = repositorio.save(reserva);

        // 4. Notificar a catálogo que libere el bloque
        try {
            catalogo.liberarBloque(reserva.getBloqueDisponibilidadId());
        } catch (Exception e) {
            log.error("No se pudo liberar el bloque en catálogo: {}", e.getMessage());
        }

        // 5. Publicar eventos de dominio a RabbitMQ
        actualizada.pullDomainEvents().forEach(event -> {
            try {
                publisher.publish(event);
            } catch (Exception e) {
                log.error("Error publicando evento de dominio: {}", e.getMessage());
            }
        });

        log.info("Reserva {} cancelada exitosamente.", cmd.reservaId());
        return actualizada;
    }
}
