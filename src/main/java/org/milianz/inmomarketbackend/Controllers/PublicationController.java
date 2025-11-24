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
import org.milianz.inmomarketbackend.Domain.Entities.DTOs.PublicationDefaultDTO;
import org.milianz.inmomarketbackend.Domain.Entities.DTOs.PublicationSaveDTO;
import org.milianz.inmomarketbackend.Domain.Entities.DTOs.PublicationUpdateDTO;
import org.milianz.inmomarketbackend.Payload.Response.MessageResponse;
import org.milianz.inmomarketbackend.Services.CloudinaryService;
import org.milianz.inmomarketbackend.Services.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/publications")
@Tag(name = "Publicaciones", description = "Gestión de publicaciones de propiedades inmobiliarias")
public class PublicationController {

    @Autowired
    private PublicationService publicationService;
    @Autowired
    private CloudinaryService cloudinaryService;

    @Operation(
            summary = "Crear nueva publicación",
            description = "Crea una nueva publicación de propiedad. Requiere autenticación. Permite subir múltiples imágenes.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Publicación creada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de publicación inválidos",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autenticado",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/create")
    public ResponseEntity <?> createPublication(
            @Valid @ModelAttribute PublicationSaveDTO publicationSaveDTO,
            Principal principal,
            @Parameter(description = "Imágenes de la propiedad (opcional, múltiples archivos)")
            @RequestParam(value = "files", required = false) MultipartFile[] files) throws Exception {
        return publicationService.createPublication(publicationSaveDTO, principal.getName(), files);
    }

    @Operation(
            summary = "Obtener todas las publicaciones",
            description = "Obtiene la lista completa de todas las publicaciones disponibles sin filtros"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de publicaciones obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PublicationDefaultDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("All")
    public ResponseEntity<List<PublicationDefaultDTO>> getAllPublications() {
        try {
            return ResponseEntity.ok(publicationService.getAllPublications());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @Operation(
            summary = "Buscar publicaciones con filtros",
            description = """
                    Busca publicaciones aplicando diferentes filtros. Solo se debe proporcionar UN filtro por petición.
                    Filtros disponibles: department, price range, typeName, size range, bedrooms, floors, parking, furnished
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Publicaciones filtradas exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PublicationDefaultDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Filtro inválido o múltiples filtros proporcionados")
    })
    @GetMapping
    public ResponseEntity<List<PublicationDefaultDTO>> getPublicationsFilters(
            @Parameter(description = "Filtrar por departamento") @RequestParam(required = false) String department,
            @Parameter(description = "Precio mínimo (usar junto con maxPrice)") @RequestParam(required = false) String minPrice,
            @Parameter(description = "Precio máximo (usar junto con minPrice)") @RequestParam(required = false) String maxPrice,
            @Parameter(description = "Tipo de propiedad (ej: Casa, Apartamento)") @RequestParam(required = false) String typeName,
            @Parameter(description = "Tamaño mínimo en m² (usar junto con maxSize)") @RequestParam(required = false) String minSize,
            @Parameter(description = "Tamaño máximo en m² (usar junto con minSize)") @RequestParam(required = false) String maxSize,
            @Parameter(description = "Número de habitaciones") @RequestParam(required = false) String bedrooms,
            @Parameter(description = "Número de pisos") @RequestParam(required = false) String floors,
            @Parameter(description = "Número de parqueaderos") @RequestParam(required = false) String parking,
            @Parameter(description = "Propiedad amoblada (true/false)") @RequestParam(required = false) String furnished
    ) {
        if (department != null) {
        return ResponseEntity.ok(publicationService.getPublicationsByDepartment(department));
        } else if (minPrice != null && maxPrice != null) {
            BigDecimal BDmaxPrice = new BigDecimal(maxPrice);
            BigDecimal BDminPrice = new BigDecimal(minPrice);
            return ResponseEntity.ok(publicationService.getPublicationsByPrice(BDminPrice, BDmaxPrice));
        } else if (typeName != null) {
            return ResponseEntity.ok(publicationService.getPublicationsByType(typeName));
        } else if (minSize != null && maxSize != null) {
            BigDecimal BDmaxSize = new BigDecimal(maxSize);
            BigDecimal BDminSize = new BigDecimal(minSize);
            return ResponseEntity.ok(publicationService.getPublicationsBySize(BDminSize, BDmaxSize));
        } else if (bedrooms != null) {
            return ResponseEntity.ok(publicationService.getPublicationsByBedrooms(Integer.parseInt(bedrooms)));
        } else if (floors != null) {
            return ResponseEntity.ok(publicationService.getPublicationsByFloors(Integer.parseInt(floors)));
        } else if (parking != null) {
            return ResponseEntity.ok(publicationService.getPublicationsByParking(Integer.parseInt(parking)));
        } else if (furnished != null) {
            return ResponseEntity.ok(publicationService.getPublicationsByFurnished(Boolean.parseBoolean(furnished)));
        }else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @Operation(
            summary = "Obtener publicaciones por usuario",
            description = "Obtiene todas las publicaciones creadas por un usuario específico"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Publicaciones del usuario obtenidas exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PublicationDefaultDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/userPublications")
    public ResponseEntity<List<PublicationDefaultDTO>> getUserPublications(
            @Parameter(description = "ID del usuario") @RequestParam("userID") UUID userID) {
        try {
            return ResponseEntity.ok(publicationService.getAllPublicationsbyUserId(userID));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @Operation(
            summary = "Obtener publicación por ID",
            description = "Obtiene el detalle completo de una publicación específica"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Publicación obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PublicationDefaultDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor o publicación no encontrada")
    })
    @GetMapping("/publicationById")
    public ResponseEntity<PublicationDefaultDTO> getPublicationById(
            @Parameter(description = "ID de la publicación") @RequestParam("publicationId") String publicationId) {
        UUID publication = UUID.fromString(publicationId);
        try {
            return ResponseEntity.ok(publicationService.getPublicationById(publication));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @Operation(
            summary = "Obtener últimas publicaciones",
            description = "Obtiene las publicaciones más recientes ordenadas por fecha de creación"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Últimas publicaciones obtenidas exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PublicationDefaultDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/lastPublications")
    public ResponseEntity<List<PublicationDefaultDTO>> getLastPublications() {
        try {
            return ResponseEntity.ok(publicationService.getLastPublications());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @Operation(
            summary = "Obtener publicaciones más populares",
            description = "Obtiene el top 10 de publicaciones más populares basado en número de favoritos"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Publicaciones populares obtenidas exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PublicationDefaultDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/mostPopularPublications")
    public ResponseEntity<List<PublicationDefaultDTO>> getMostPopularPublications() {
        try {
            return ResponseEntity.ok(publicationService.getTop10MostPopularPublications());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @Operation(
            summary = "Actualizar publicación",
            description = "Actualiza una publicación existente. Solo el propietario puede actualizar su publicación.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Publicación actualizada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado - No es el propietario de la publicación"),
            @ApiResponse(responseCode = "400", description = "Datos de actualización inválidos")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updatePublication(
            @Parameter(description = "ID de la publicación a actualizar") @PathVariable("id") UUID publicationId,
            @Valid @ModelAttribute PublicationUpdateDTO publicationUpdateDTO,
            Principal principal,
            @Parameter(description = "Nuevas imágenes (opcional)") @RequestParam(value = "files", required = false) MultipartFile[] files) throws Exception {
        return publicationService.updatePublication(publicationId, publicationUpdateDTO, principal.getName(), files);
    }
}
