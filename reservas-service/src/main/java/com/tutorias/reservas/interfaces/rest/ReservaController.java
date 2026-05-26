package com.tutorias.reservas.interfaces.rest;

import com.tutorias.reservas.application.commands.*;
import com.tutorias.reservas.application.queries.*;
import com.tutorias.reservas.domain.model.Reserva;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST de Reservas.
 * Sin lógica de negocio — solo transforma HTTP en Commands/Queries y devuelve DTOs.
 */
@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "Gestión de reservas de tutorías (RF07, RF09, RF10, RF11, RF12)")
public class ReservaController {

    private final CrearReservaCommandHandler       crearHandler;
    private final CancelarReservaCommandHandler    cancelarHandler;
    private final RegistrarAsistenciaCommandHandler asistenciaHandler;
    private final GetByIdQueryHandler              getByIdHandler;
    private final GetProximasQueryHandler          proximasHandler;
    private final GetHistorialQueryHandler         historialHandler;
    private final GetPendientesAsistenciaQueryHandler pendientesHandler;

    // ===== COMMANDS =====

    @Operation(summary = "RF07 - Agendar tutoría",
               description = "Crea una nueva reserva y publica evento ReservaCreadaEvent a RabbitMQ.")
    @PostMapping
    public ResponseEntity<ReservaDTO.ApiResponse<ReservaDTO.ReservaResponse>> crearReserva(
            @Valid @RequestBody ReservaDTO.CrearReservaRequest request) {

        Reserva reserva = crearHandler.handle(new CrearReservaCommand(
                request.getEstudianteId(),
                request.getTutorId(),
                request.getBloqueDisponibilidadId(),
                request.getMateriaId(),
                request.getFechaSesion(),
                request.getNotasEstudiante()));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ReservaDTO.ApiResponse.ok("Tutoría agendada exitosamente.", toResponse(reserva)));
    }

    @Operation(summary = "RF10 - Cancelar reserva",
               description = "Cancela una reserva activa y publica evento ReservaCanceladaEvent a RabbitMQ.")
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ReservaDTO.ApiResponse<ReservaDTO.ReservaResponse>> cancelarReserva(
            @PathVariable Long id,
            @RequestParam String estudianteId) {

        Reserva reserva = cancelarHandler.handle(new CancelarReservaCommand(id, estudianteId));
        return ResponseEntity.ok(ReservaDTO.ApiResponse.ok(
                "Tutoría cancelada. El bloque ha sido liberado.", toResponse(reserva)));
    }

    @Operation(summary = "RF11 - Registrar asistencia",
               description = "El tutor marca la sesión como COMPLETADA o INASISTENCIA.")
    @PatchMapping("/{id}/asistencia")
    public ResponseEntity<ReservaDTO.ApiResponse<ReservaDTO.ReservaResponse>> registrarAsistencia(
            @PathVariable Long id,
            @RequestParam String tutorId,
            @Valid @RequestBody ReservaDTO.RegistrarAsistenciaRequest request) {

        Reserva reserva = asistenciaHandler.handle(
                new RegistrarAsistenciaCommand(id, tutorId, request.getEstado()));
        return ResponseEntity.ok(ReservaDTO.ApiResponse.ok("Asistencia registrada.", toResponse(reserva)));
    }

    // ===== QUERIES =====

    @Operation(summary = "Obtener reserva por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ReservaDTO.ApiResponse<ReservaDTO.ReservaResponse>> getReserva(
            @PathVariable Long id) {
        return ResponseEntity.ok(ReservaDTO.ApiResponse.ok(
                "Reserva encontrada.", toResponse(getByIdHandler.handle(new GetByIdQuery(id)))));
    }

    @Operation(summary = "RF09 - Próximas tutorías del estudiante")
    @GetMapping("/estudiante/{estudianteId}/proximas")
    public ResponseEntity<ReservaDTO.ApiResponse<List<ReservaDTO.ReservaResponse>>> getProximasEstudiante(
            @PathVariable String estudianteId) {

        List<ReservaDTO.ReservaResponse> lista = proximasHandler
                .handle(new GetProximasQuery(estudianteId, "estudiante"))
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ReservaDTO.ApiResponse.ok("Próximas tutorías.", lista));
    }

    @Operation(summary = "RF09 - Próximas tutorías del tutor")
    @GetMapping("/tutor/{tutorId}/proximas")
    public ResponseEntity<ReservaDTO.ApiResponse<List<ReservaDTO.ReservaResponse>>> getProximasTutor(
            @PathVariable String tutorId) {

        List<ReservaDTO.ReservaResponse> lista = proximasHandler
                .handle(new GetProximasQuery(tutorId, "tutor"))
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ReservaDTO.ApiResponse.ok("Próximas tutorías del tutor.", lista));
    }

    @Operation(summary = "RF12 - Historial del estudiante",
               description = "Devuelve todas las tutorías pasadas (COMPLETADAS e INASISTENCIAS).")
    @GetMapping("/estudiante/{estudianteId}/historial")
    public ResponseEntity<ReservaDTO.ApiResponse<List<ReservaDTO.ReservaResponse>>> getHistorial(
            @PathVariable String estudianteId) {

        List<ReservaDTO.ReservaResponse> lista = historialHandler
                .handle(new GetHistorialQuery(estudianteId))
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ReservaDTO.ApiResponse.ok("Historial.", lista));
    }

    @Operation(summary = "RF11 - Sesiones pendientes de asistencia del tutor")
    @GetMapping("/tutor/{tutorId}/pendientes-asistencia")
    public ResponseEntity<ReservaDTO.ApiResponse<List<ReservaDTO.ReservaResponse>>> getPendientes(
            @PathVariable String tutorId) {

        List<ReservaDTO.ReservaResponse> lista = pendientesHandler
                .handle(new GetPendientesAsistenciaQuery(tutorId))
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ReservaDTO.ApiResponse.ok("Sesiones pendientes.", lista));
    }

    @Operation(summary = "Health check")
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("reservas-service OK");
    }

    // ===== Mapper dominio → DTO =====

    private ReservaDTO.ReservaResponse toResponse(Reserva r) {
        return ReservaDTO.ReservaResponse.builder()
                .id(r.getId())
                .estudianteId(r.getEstudianteId())
                .tutorId(r.getTutorId())
                .bloqueDisponibilidadId(r.getBloqueDisponibilidadId())
                .materiaId(r.getMateriaId())
                .fechaSesion(r.getFechaSesion())
                .fechaCreacion(r.getFechaCreacion())
                .fechaHoraCreacion(r.getFechaHoraCreacion())
                .estado(r.getEstado())
                .notasEstudiante(r.getNotasEstudiante())
                .build();
    }
}
