package org.milianz.inmomarketbackend.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.milianz.inmomarketbackend.Payload.Request.UpdateProfileRequest;
import org.milianz.inmomarketbackend.Payload.Response.MessageResponse;
import org.milianz.inmomarketbackend.Payload.Response.UserProfileResponse;
import org.milianz.inmomarketbackend.Services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

/**
 * Controlador REST para gestión de perfiles de usuario.
 *
 * Proporciona endpoints para consultar y actualizar información del perfil
 * del usuario autenticado.
 *
 * @author InmoMarket Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "Usuario", description = "Gestión de perfil de usuario")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Obtener perfil actual",
            description = "Obtiene el perfil completo del usuario autenticado actualmente"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Perfil obtenido exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserProfileResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile() {
        try {
            UserProfileResponse userProfile = userService.getCurrentUserProfile();
            return ResponseEntity.ok(userProfile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
            summary = "Actualizar perfil de usuario",
            description = """
                    Actualiza el perfil del usuario autenticado. Permite modificar nombre, email, teléfono, contraseña y foto de perfil.
                    - Para cambiar contraseña: proporcionar currentPassword y newPassword
                    - Para subir nueva foto: incluir archivo en profilePicture
                    - Para eliminar foto actual: enviar removeProfilePicture=true
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Perfil actualizado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserProfileResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Error de validación (email duplicado, contraseña incorrecta, etc.)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error al procesar imagen o error del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class)
                    )
            )
    })
    @PutMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateUserProfile(
            @Parameter(description = "Nombre completo del usuario", required = true) @RequestParam("name") String name,
            @Parameter(description = "Correo electrónico (debe ser único)", required = true) @RequestParam("email") String email,
            @Parameter(description = "Número de teléfono en formato E.164", required = true) @RequestParam("phoneNumber") String phoneNumber,
            @Parameter(description = "Contraseña actual (requerida para cambiar contraseña)") @RequestParam(value = "currentPassword", required = false) String currentPassword,
            @Parameter(description = "Nueva contraseña (mínimo 6 caracteres)") @RequestParam(value = "newPassword", required = false) String newPassword,
            @Parameter(description = "Archivo de imagen para foto de perfil") @RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture,
            @Parameter(description = "Eliminar foto de perfil actual") @RequestParam(value = "removeProfilePicture", defaultValue = "false") Boolean removeProfilePicture) {

        try {
            // Crear el UpdateProfileRequest
            UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest();
            updateProfileRequest.setName(name);
            updateProfileRequest.setEmail(email);
            updateProfileRequest.setPhoneNumber(phoneNumber);
            updateProfileRequest.setCurrentPassword(currentPassword);
            updateProfileRequest.setNewPassword(newPassword);
            updateProfileRequest.setProfilePicture(profilePicture);
            updateProfileRequest.setRemoveProfilePicture(removeProfilePicture);

            UserProfileResponse updatedProfile = userService.updateProfile(updateProfileRequest);
            return ResponseEntity.ok(updatedProfile);

        } catch (RuntimeException e) {
            // Errores de validación de negocio (email duplicado, contraseña incorrecta, etc.)
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (IOException e) {
            // Error al procesar la imagen de perfil
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al procesar la imagen"));
        } catch (Exception e) {
            // Errores no controlados
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error interno del servidor"));
        }
    }
}
