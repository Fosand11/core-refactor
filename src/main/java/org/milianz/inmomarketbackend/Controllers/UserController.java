package org.milianz.inmomarketbackend.Controllers;

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
public class UserController {

    private final UserService userService;

    /**
     * Obtiene el perfil del usuario autenticado actualmente.
     *
     * @return ResponseEntity con UserProfileResponse conteniendo datos del perfil
     *         200 OK: Perfil obtenido exitosamente
     *         500 Internal Server Error: Error al obtener el perfil
     */
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

    /**
     * Actualiza el perfil del usuario autenticado.
     *
     * Permite modificar nombre, email, teléfono, contraseña y foto de perfil.
     * Solo el usuario autenticado puede modificar su propio perfil.
     *
     * @param name Nombre completo del usuario
     * @param email Correo electrónico (debe ser único)
     * @param phoneNumber Número de teléfono en formato E.164
     * @param currentPassword Contraseña actual (requerida si se cambia contraseña)
     * @param newPassword Nueva contraseña (requerida si se cambia contraseña)
     * @param profilePicture Archivo de imagen para foto de perfil (opcional)
     * @param removeProfilePicture Flag para eliminar foto actual (default: false)
     *
     * @return ResponseEntity con:
     *         200 OK + UserProfileResponse: Actualización exitosa
     *         400 Bad Request: Error de validación o lógica de negocio
     *         500 Internal Server Error: Error al procesar imagen o error del servidor
     *
     * @throws RuntimeException si el email está en uso, la contraseña es incorrecta,
     *                          o faltan campos requeridos
     * @throws IOException si hay error al procesar la imagen
     */
    @PutMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateUserProfile(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam(value = "currentPassword", required = false) String currentPassword,
            @RequestParam(value = "newPassword", required = false) String newPassword,
            @RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture,
            @RequestParam(value = "removeProfilePicture", defaultValue = "false") Boolean removeProfilePicture) {

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
