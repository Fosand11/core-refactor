package org.milianz.inmomarketbackend.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.milianz.inmomarketbackend.Domain.Entities.DTOs.ReportDefaultDTO;
import org.milianz.inmomarketbackend.Domain.Entities.DTOs.ReportResolveDTO;
import org.milianz.inmomarketbackend.Domain.Entities.DTOs.ReportSaveDTO;
import org.milianz.inmomarketbackend.Payload.Response.MessageResponse;
import org.milianz.inmomarketbackend.Services.ReportService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "Sistema de reportes de publicaciones y gestión administrativa")
@SecurityRequirement(name = "Bearer Authentication")
public class ReportController {

    private final ReportService reportService;

    @Operation(
            summary = "Crear reporte",
            description = "Crea un nuevo reporte sobre una publicación"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reporte creado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "400", description = "Datos de reporte inválidos")
    })
    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createReport(@Valid @RequestBody ReportSaveDTO reportSaveDTO) {
        return reportService.createReport(reportSaveDTO);
    }

    @Operation(
            summary = "Obtener mis reportes",
            description = "Obtiene todos los reportes creados por el usuario actual de forma paginada"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de reportes obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/my-reports")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<ReportDefaultDTO>> getMyReports(
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("reportDate").descending());
        Page<ReportDefaultDTO> reports = reportService.getMyReports(pageable);
        return ResponseEntity.ok(reports);
    }

    @Operation(
            summary = "Obtener reportes con feedback",
            description = "Obtiene los reportes del usuario que tienen respuesta del administrador"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de reportes con feedback obtenida",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/my-reports-with-feedback")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<ReportDefaultDTO>> getMyReportsWithFeedback(
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("resolvedDate").descending());
        Page<ReportDefaultDTO> reports = reportService.getMyReportsWithFeedback(pageable);
        return ResponseEntity.ok(reports);
    }

    @Operation(
            summary = "Contar feedback pendientes",
            description = "Obtiene el número de reportes con feedback no leído por el usuario"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cantidad de feedback obtenida exitosamente",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/my-feedback-count")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Long> getMyFeedbackCount() {
        long count = reportService.getMyFeedbackCount();
        return ResponseEntity.ok(count);
    }

    @Operation(
            summary = "Marcar feedback como leído",
            description = "Marca el feedback de un reporte como leído por el usuario"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Feedback marcado como leído",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Reporte no encontrado")
    })
    @PutMapping("/mark-feedback-read")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> markFeedbackAsRead(
            @Parameter(description = "ID del reporte") @RequestParam UUID reportId) {
        return reportService.markFeedbackAsRead(reportId);
    }

    @Operation(
            summary = "[ADMIN] Obtener todos los reportes",
            description = "Obtiene todos los reportes del sistema con filtrado opcional por estado. Solo accesible para administradores."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de reportes obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Se requiere rol ADMIN"),
            @ApiResponse(responseCode = "400", description = "Estado inválido")
    })
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ReportDefaultDTO>> getAllReports(
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Filtrar por estado (PENDING, RESOLVED, REJECTED)") @RequestParam(required = false) String status) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("reportDate").descending());

        // Convertir el string a enum si se proporciona
        org.milianz.inmomarketbackend.Domain.Entities.Report.ReportStatus reportStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                reportStatus = org.milianz.inmomarketbackend.Domain.Entities.Report.ReportStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Si el estado no es válido, retornar un error
                return ResponseEntity.badRequest().build();
            }
        }

        Page<ReportDefaultDTO> reports = reportService.getAllReports(reportStatus, pageable);
        return ResponseEntity.ok(reports);
    }

    @Operation(
            summary = "[ADMIN] Obtener reportes por publicación",
            description = "Obtiene todos los reportes asociados a una publicación específica. Solo accesible para administradores."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reportes de la publicación obtenidos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Se requiere rol ADMIN")
    })
    @GetMapping("/publication/{publicationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ReportDefaultDTO>> getReportsByPublication(
            @Parameter(description = "ID de la publicación") @PathVariable UUID publicationId,
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("reportDate").descending());
        Page<ReportDefaultDTO> reports = reportService.getReportsByPublication(publicationId, pageable);
        return ResponseEntity.ok(reports);
    }

    @Operation(
            summary = "[ADMIN] Resolver reporte con feedback",
            description = "Resuelve un reporte proporcionando feedback al usuario que lo creó. Solo accesible para administradores."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reporte resuelto exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Se requiere rol ADMIN"),
            @ApiResponse(responseCode = "404", description = "Reporte no encontrado")
    })
    @PutMapping("/admin/{reportId}/resolve-with-feedback")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> resolveReportWithFeedback(
            @Parameter(description = "ID del reporte") @PathVariable UUID reportId,
            @Valid @RequestBody ReportResolveDTO reportResolveDTO) {

        return reportService.resolveReport(reportId, reportResolveDTO);
    }
}