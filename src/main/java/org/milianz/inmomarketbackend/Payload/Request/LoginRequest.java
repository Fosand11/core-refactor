package org.milianz.inmomarketbackend.Payload.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Datos de autenticación para inicio de sesión")
public class LoginRequest {
    @Schema(description = "Correo electrónico del usuario", example = "usuario@ejemplo.com", required = true)
    @NotBlank(message = "{login.email.notblank}")
    private String email;

    @Schema(description = "Contraseña del usuario", example = "password123", required = true)
    @NotBlank(message = "{login.password.notblank}")
    private String password;

}
