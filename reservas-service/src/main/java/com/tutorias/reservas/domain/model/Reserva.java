package com.tutorias.reservas.domain.model;

import com.tutorias.reservas.domain.events.ReservaCanceladaEvent;
import com.tutorias.reservas.domain.events.ReservaCreadaEvent;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Aggregate Root del dominio de Reservas.
 * No tiene dependencias de frameworks — solo lógica de negocio pura.
 */
public class Reserva {

    private Long id;
    private String estudianteId;
    private String tutorId;
    private Long bloqueDisponibilidadId;
    private String materiaId;
    private LocalDate fechaSesion;
    private LocalDate fechaCreacion;
    private LocalDateTime fechaHoraCreacion;
    private EstadoReserva estado;
    private String notasEstudiante;

    private final List<Object> domainEvents = new ArrayList<>();

    // Constructor privado — se instancia solo desde métodos de fábrica
    private Reserva() {}

    /**
     * Método de fábrica: crea una nueva reserva con estado ACTIVA
     * y registra el evento de dominio correspondiente.
     */
    public static Reserva crear(String estudianteId, String tutorId,
                                Long bloqueDisponibilidadId, String materiaId,
                                LocalDate fechaSesion, String notasEstudiante) {
        Reserva r = new Reserva();
        r.estudianteId = estudianteId;
        r.tutorId = tutorId;
        r.bloqueDisponibilidadId = bloqueDisponibilidadId;
        r.materiaId = materiaId;
        r.fechaSesion = fechaSesion;
        r.fechaCreacion = LocalDate.now();
        r.fechaHoraCreacion = LocalDateTime.now();
        r.estado = EstadoReserva.ACTIVA;
        r.notasEstudiante = notasEstudiante;

        r.domainEvents.add(new ReservaCreadaEvent(
                estudianteId, tutorId, bloqueDisponibilidadId, fechaSesion));
        return r;
    }

    /**
     * Reconstituye una reserva existente desde persistencia (sin generar eventos).
     */
    public static Reserva reconstituir(Long id, String estudianteId, String tutorId,
                                       Long bloqueDisponibilidadId, String materiaId,
                                       LocalDate fechaSesion, LocalDate fechaCreacion,
                                       LocalDateTime fechaHoraCreacion,
                                       EstadoReserva estado, String notasEstudiante) {
        Reserva r = new Reserva();
        r.id = id;
        r.estudianteId = estudianteId;
        r.tutorId = tutorId;
        r.bloqueDisponibilidadId = bloqueDisponibilidadId;
        r.materiaId = materiaId;
        r.fechaSesion = fechaSesion;
        r.fechaCreacion = fechaCreacion;
        r.fechaHoraCreacion = fechaHoraCreacion;
        r.estado = estado;
        r.notasEstudiante = notasEstudiante;
        return r;
    }

    // ===== Comportamiento de negocio =====

    public void cancelar(String solicitanteId) {
        if (!this.estudianteId.equals(solicitanteId)) {
            throw new IllegalArgumentException("No tienes permiso para cancelar esta reserva.");
        }
        if (this.estado != EstadoReserva.ACTIVA) {
            throw new IllegalStateException(
                    "Solo se pueden cancelar reservas activas. Estado actual: " + this.estado);
        }
        if (this.fechaSesion.isBefore(LocalDate.now())) {
            throw new IllegalStateException("No se puede cancelar una tutoría que ya pasó.");
        }
        this.estado = EstadoReserva.CANCELADA;
        this.domainEvents.add(new ReservaCanceladaEvent(this.id, this.bloqueDisponibilidadId));
    }

    public void registrarAsistencia(EstadoReserva nuevoEstado, String tutorId) {
        if (nuevoEstado != EstadoReserva.COMPLETADA && nuevoEstado != EstadoReserva.INASISTENCIA) {
            throw new IllegalArgumentException("Solo se puede marcar COMPLETADA o INASISTENCIA.");
        }
        if (!this.tutorId.equals(tutorId)) {
            throw new IllegalArgumentException("No tienes permiso para modificar esta reserva.");
        }
        if (this.estado != EstadoReserva.ACTIVA) {
            throw new IllegalStateException("Solo se puede registrar asistencia en reservas activas.");
        }
        if (this.fechaSesion.isAfter(LocalDate.now())) {
            throw new IllegalStateException("La sesión aún no ha ocurrido.");
        }
        this.estado = nuevoEstado;
    }

    /**
     * Extrae y limpia los eventos de dominio pendientes de publicar.
     */
    public List<Object> pullDomainEvents() {
        List<Object> events = new ArrayList<>(this.domainEvents);
        this.domainEvents.clear();
        return events;
    }

    // ===== Getters (sin setters — el aggregate controla su estado) =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; } // solo para el adapter de persistencia
    public String getEstudianteId() { return estudianteId; }
    public String getTutorId() { return tutorId; }
    public Long getBloqueDisponibilidadId() { return bloqueDisponibilidadId; }
    public String getMateriaId() { return materiaId; }
    public LocalDate getFechaSesion() { return fechaSesion; }
    public LocalDate getFechaCreacion() { return fechaCreacion; }
    public LocalDateTime getFechaHoraCreacion() { return fechaHoraCreacion; }
    public EstadoReserva getEstado() { return estado; }
    public String getNotasEstudiante() { return notasEstudiante; }
}
