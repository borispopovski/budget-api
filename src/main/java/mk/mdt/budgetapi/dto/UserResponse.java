package mk.mdt.budgetapi.dto;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        boolean enabled,
        String role,
        LocalDateTime createDate,
        LocalDateTime updateDate,
        LocalDateTime lastLogin
) {}