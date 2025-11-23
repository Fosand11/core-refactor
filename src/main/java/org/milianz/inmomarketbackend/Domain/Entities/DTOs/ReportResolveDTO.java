package org.milianz.inmomarketbackend.Domain.Entities.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportResolveDTO {

    @NotBlank(message = "La acción es obligatoria")
    private String action; // "APPROVE" o "DISMISS"

    @Size(max = 1000, message = "El feedback no puede exceder 1000 caracteres")
    private String feedback;
}
