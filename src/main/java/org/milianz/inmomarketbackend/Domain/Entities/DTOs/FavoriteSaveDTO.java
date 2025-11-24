package org.milianz.inmomarketbackend.Domain.Entities.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para agregar/quitar una publicación de favoritos")
public class FavoriteSaveDTO {

    @Schema(description = "ID de la publicación", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
    @NotNull(message = "El ID de la publicación es obligatorio")
    private UUID publicationId;
}