package org.milianz.inmomarketbackend.Payload.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * DTO para actualización de perfil de usuario.
 *
 * Encapsula toda la información que un usuario puede modificar en su perfil,
 * incluyendo datos personales, contraseña y foto de perfil.
 *
 * @author InmoMarket Team
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    /**
     * Nombre completo del usuario.
     * No puede estar vacío.
     */
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    /**
     * Correo electrónico del usuario.
     * Debe ser único en el sistema y tener formato válido.
     */
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    /**
     * Número de teléfono en formato internacional E.164.
     * Ejemplo: +50312345678
     */
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$",
            message = "El número de teléfono debe tener un formato válido")
    private String phoneNumber;

    /**
     * Contraseña actual del usuario.
     * Requerida solo si se desea cambiar la contraseña.
     * Se valida contra el hash BCrypt almacenado en la base de datos.
     */
    private String currentPassword;

    /**
     * Nueva contraseña del usuario.
     * Requerida solo si se desea cambiar la contraseña.
     * Debe tener entre 6 y 40 caracteres y ser diferente a la actual.
     */
    @Size(min = 6, max = 40, message = "La nueva contraseña debe tener entre 6 y 40 caracteres")
    private String newPassword;

    /**
     * Archivo de imagen para la foto de perfil.
     * Formatos aceptados: JPG, PNG, GIF
     * Tamaño máximo: 5MB
     */
    private MultipartFile profilePicture;

    /**
     * Flag para indicar si se debe eliminar la foto de perfil actual.
     * Si es true, se elimina la foto y se establece a null.
     */
    private Boolean removeProfilePicture = false;
}
