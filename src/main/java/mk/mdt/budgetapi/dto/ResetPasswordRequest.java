package mk.mdt.budgetapi.dto;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
        @NotBlank String token,
        @NotBlank String password,
        @NotBlank String confirmPassword
) {}