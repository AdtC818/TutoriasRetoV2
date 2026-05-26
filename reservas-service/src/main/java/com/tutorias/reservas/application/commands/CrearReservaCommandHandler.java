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

/**
 * Handler del comando CrearReserva.
 * Orquesta validaciones, creación del aggregate y publicación de eventos.
 */
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

        // 1. Verificar que el estudiante existe
        if (!usuarios.verificarUsuarioExiste(cmd.estudianteId())) {
            throw new IllegalArgumentException(
                    "El estudiante con ID " + cmd.estudianteId() + " no existe.");
        }

        // 2. Verificar que el tutor tiene la materia
        if (!usuarios.verificarTutorTieneMateria(cmd.tutorId(), cmd.materiaId())) {
            throw new IllegalArgumentException(
                    "El tutor no tiene autorización para dictar esa materia.");
        }

        // 3. Verificar que el bloque está LIBRE en catálogo
        BloqueInfo bloque = catalogo.verificarBloqueDisponible(cmd.bloqueDisponibilidadId());
        if (bloque == null) {
            throw new BloqueNoDisponibleException(
                    "El bloque no existe o el servicio de catálogo no está disponible.");
        }
        if (!"LIBRE".equals(bloque.estado())) {
            throw new BloqueNoDisponibleException(
                    "El bloque seleccionado ya está reservado. Elige otro horario.");
        }

        // 4. Protección contra race conditions en la BD local
        if (repositorio.findReservaActivaEnBloque(
                cmd.bloqueDisponibilidadId(), cmd.fechaSesion()).isPresent()) {
            throw new BloqueNoDisponibleException(
                    "Ese horario acaba de ser reservado. Selecciona otro bloque.");
        }

        // 5. Verificar reserva duplicada del mismo estudiante con el mismo tutor ese día
        if (repositorio.existeReservaDuplicada(
                cmd.estudianteId(), cmd.tutorId(), cmd.fechaSesion())) {
            throw new BloqueNoDisponibleException(
                    "Ya tienes una tutoría agendada con este tutor en esa fecha.");
        }

        // 6. Crear el aggregate (genera el evento ReservaCreadaEvent internamente)
        Reserva reserva = Reserva.crear(
                cmd.estudianteId(), cmd.tutorId(),
                cmd.bloqueDisponibilidadId(), cmd.materiaId(),
                cmd.fechaSesion(), cmd.notasEstudiante());

        Reserva guardada = repositorio.save(reserva);

        // 7. Notificar a catálogo que bloquee el slot
        try {
            catalogo.bloquearBloque(cmd.bloqueDisponibilidadId());
        } catch (Exception e) {
            log.error("No se pudo bloquear el bloque en catálogo: {}", e.getMessage());
        }

        // 8. Publicar eventos de dominio a RabbitMQ
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
