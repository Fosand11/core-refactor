package org.milianz.inmomarketbackend.Domain.Entities.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear un reporte sobre una publicación")
public class ReportSaveDTO {

    @Schema(description = "ID de la publicación a reportar", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
    @NotNull(message = "El ID de la publicación es obligatorio")
    private UUID publicationId;

    @Schema(description = "Motivo del reporte", example = "Información incorrecta", required = true, minLength = 3, maxLength = 100)
    @NotBlank(message = "El motivo del reporte es obligatorio")
    @Size(min = 3, max = 100, message = "El motivo debe tener entre 3 y 100 caracteres")
    private String reason;

    @Schema(description = "Descripción detallada del problema", example = "La dirección de la propiedad no coincide con la realidad", maxLength = 500)
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String description;
}