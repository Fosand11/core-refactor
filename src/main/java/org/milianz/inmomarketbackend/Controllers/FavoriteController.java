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
import org.milianz.inmomarketbackend.Domain.Entities.DTOs.FavoriteDefaultDTO;
import org.milianz.inmomarketbackend.Domain.Entities.DTOs.FavoriteSaveDTO;
import org.milianz.inmomarketbackend.Payload.Response.MessageResponse;
import org.milianz.inmomarketbackend.Services.FavoriteService;
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
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@Tag(name = "Favoritos", description = "Gestión de publicaciones favoritas del usuario")
@SecurityRequirement(name = "Bearer Authentication")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(
            summary = "Alternar favorito",
            description = "Agrega o remueve una publicación de favoritos. Si ya existe, la elimina; si no existe, la agrega."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Favorito agregado o eliminado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping("/toggle")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> toggleFavorite(@Valid @RequestBody FavoriteSaveDTO favoriteSaveDTO) {
        return favoriteService.toggleFavorite(favoriteSaveDTO);
    }

    @Operation(
            summary = "Obtener mis favoritos",
            description = "Obtiene la lista paginada de publicaciones marcadas como favoritas por el usuario actual"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de favoritos obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/my-favorites")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<FavoriteDefaultDTO>> getMyFavorites(
            @Parameter(description = "Número de página (inicia en 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("savedAt").descending());
        Page<FavoriteDefaultDTO> favorites = favoriteService.getMyFavorites(pageable);
        return ResponseEntity.ok(favorites);
    }

    @Operation(
            summary = "Verificar si es favorito",
            description = "Verifica si una publicación está marcada como favorita por el usuario actual"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Verificación completada exitosamente",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/check/{publicationId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> checkIsFavorite(
            @Parameter(description = "ID de la publicación") @PathVariable UUID publicationId) {
        return favoriteService.checkIsFavorite(publicationId);
    }

    @Operation(
            summary = "Eliminar favorito",
            description = "Remueve una publicación de la lista de favoritos del usuario"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Favorito eliminado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Favorito no encontrado")
    })
    @DeleteMapping("/remove/{publicationId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> removeFavorite(
            @Parameter(description = "ID de la publicación") @PathVariable UUID publicationId) {
        return favoriteService.removeFavorite(publicationId);
    }

    @Operation(
            summary = "Obtener estadísticas de favoritos",
            description = "Obtiene estadísticas sobre los favoritos del usuario (cantidad total, etc.)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Estadísticas obtenidas exitosamente",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/stats")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getFavoriteStats() {
        return favoriteService.getFavoriteStats();
    }
}