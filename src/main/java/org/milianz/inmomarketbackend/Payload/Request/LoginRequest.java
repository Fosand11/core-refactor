package org.milianz.inmomarketbackend.Payload.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "{login.email.notblank}")
    private String email;

    @NotBlank(message = "{login.password.notblank}")
    private String password;

}
