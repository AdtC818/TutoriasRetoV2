package com.tutorias.reservas.application.commands;

import com.tutorias.reservas.domain.model.Reserva;
import com.tutorias.reservas.domain.ports.out.CatalogoPort;
import com.tutorias.reservas.domain.ports.out.CatalogoPort.BloqueInfo;
import com.tutorias.reservas.domain.ports.out.EventPublisherPort;
import com.tutorias.reservas.domain.ports.out.ReservaRepositoryPort;
import com.tutorias.reservas.domain.ports.out.UsuariosPort;
import com.tutorias.reservas.exception.BloqueNoDisponibleException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CrearReservaCommandHandler {

    private final ReservaRepositoryPort repositorio;
    private final CatalogoPort catalogo;
    private final UsuariosPort usuarios;
    private final EventPublisherPort publisher;

    @Transactional
    public Reserva handle(CrearReservaCommand cmd) {
        log.info("Procesando CrearReservaCommand: estudiante={}, bloque={}, fecha={}",
                cmd.estudianteId(), cmd.bloqueDisponibilidadId(), cmd.fechaSesion());

        // Verificar bloque disponible en catálogo (el estado puede ser LIBRE o DISPONIBLE)
        BloqueInfo bloque = catalogo.verificarBloqueDisponible(cmd.bloqueDisponibilidadId());
        if (bloque != null && "RESERVADO".equals(bloque.estado())) {
            throw new BloqueNoDisponibleException(
                    "El bloque seleccionado ya está reservado. Elige otro horario.");
        }

        // Protección contra reservas duplicadas en la BD local
        if (repositorio.findReservaActivaEnBloque(
                cmd.bloqueDisponibilidadId(), cmd.fechaSesion()).isPresent()) {
            throw new BloqueNoDisponibleException(
                    "Ese horario acaba de ser reservado. Selecciona otro bloque.");
        }

        if (repositorio.existeReservaDuplicada(
                cmd.estudianteId(), cmd.tutorId(), cmd.fechaSesion())) {
            throw new BloqueNoDisponibleException(
                    "Ya tienes una tutoría agendada con este tutor en esa fecha.");
        }

        // Crear el aggregate — genera ReservaCreadaEvent internamente
        Reserva reserva = Reserva.crear(
                cmd.estudianteId(), cmd.tutorId(),
                cmd.bloqueDisponibilidadId(), cmd.materiaId(),
                cmd.fechaSesion(), cmd.notasEstudiante());

        Reserva guardada = repositorio.save(reserva);

        // Notificar a catálogo que bloquee el slot
        try {
            catalogo.bloquearBloque(cmd.bloqueDisponibilidadId());
        } catch (Exception e) {
            log.error("No se pudo bloquear el bloque en catálogo: {}", e.getMessage());
        }

        // Publicar eventos de dominio a RabbitMQ
        guardada.pullDomainEvents().forEach(event -> {
            try {
                publisher.publish(event);
            } catch (Exception e) {
                log.error("Error publicando evento de dominio: {}", e.getMessage());
            }
        });

        log.info("Reserva creada exitosamente con ID={}", guardada.getId());
        return guardada;
    }
}