package org.milianz.inmomarketbackend.Controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.milianz.inmomarketbackend.Domain.Entities.DTOs.ReportDefaultDTO;
import org.milianz.inmomarketbackend.Domain.Entities.DTOs.ReportResolveDTO;
import org.milianz.inmomarketbackend.Domain.Entities.DTOs.ReportSaveDTO;
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
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createReport(@Valid @RequestBody ReportSaveDTO reportSaveDTO) {
        return reportService.createReport(reportSaveDTO);
    }

    @GetMapping("/my-reports")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<ReportDefaultDTO>> getMyReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("reportDate").descending());
        Page<ReportDefaultDTO> reports = reportService.getMyReports(pageable);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/my-reports-with-feedback")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<ReportDefaultDTO>> getMyReportsWithFeedback(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("resolvedDate").descending());
        Page<ReportDefaultDTO> reports = reportService.getMyReportsWithFeedback(pageable);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ReportDefaultDTO>> getAllReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("reportDate").descending());
        Page<ReportDefaultDTO> reports = reportService.getAllReports(pageable);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/publication/{publicationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ReportDefaultDTO>> getReportsByPublication(
            @PathVariable UUID publicationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("reportDate").descending());
        Page<ReportDefaultDTO> reports = reportService.getReportsByPublication(publicationId, pageable);
        return ResponseEntity.ok(reports);
    }

    @PutMapping("/admin/{reportId}/resolve-with-feedback")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> resolveReportWithFeedback(
            @PathVariable UUID reportId,
            @Valid @RequestBody ReportResolveDTO reportResolveDTO) {

        return reportService.resolveReport(reportId, reportResolveDTO);
    }
}