package org.milianz.inmomarketbackend.Payload.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Datos de registro de nuevo usuario")
public class SingupRequest {
    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez", required = true, minLength = 3, maxLength = 100)
    @NotBlank(message = "{signup.name.notblank}")
    @Size(min = 3, max = 100, message = "{signup.name.size}")
    private String name;

    @Schema(description = "Correo electrónico único del usuario", example = "juan.perez@ejemplo.com", required = true, maxLength = 100)
    @NotBlank(message = "{signup.email.notblank}")
    @Size(max = 100, message = "{signup.email.size}")
    @Email(message = "{signup.email.invalid}")
    private String email;

    @Schema(description = "Número de teléfono en formato E.164", example = "+573001234567")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "{signup.phoneNumber.invalid}")
    private String phoneNumber;

    @Schema(description = "Contraseña del usuario", example = "Password123", required = true, minLength = 6, maxLength = 40)
    @NotBlank(message = "{signup.password.notblank}")
    @Size(min = 6, max = 40, message = "{signup.password.size}")
    private String password;

    @Schema(description = "Rol del usuario (USER o ADMIN)", example = "USER", defaultValue = "USER")
    private String role;
}