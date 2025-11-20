package org.milianz.inmomarketbackend.Payload.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SingupRequest {
    @NotBlank(message = "{signup.name.notblank}")
    @Size(min = 3, max = 100, message = "{signup.name.size}")
    private String name;

    @NotBlank(message = "{signup.email.notblank}")
    @Size(max = 100, message = "{signup.email.size}")
    @Email(message = "{signup.email.invalid}")
    private String email;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "{signup.phoneNumber.invalid}")
    private String phoneNumber;

    @NotBlank(message = "{signup.password.notblank}")
    @Size(min = 6, max = 40, message = "{signup.password.size}")
    private String password;

    private String role;
}