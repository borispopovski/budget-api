package mk.mdt.budgetapi.dto;

import mk.mdt.budgetapi.entity.Role;

public record RegisterUserResponse(
        String firstName,
        String lastName,
        String email,
        Role role

) {
}
