package com.qvenly.userhelp.models.dto;

import com.qvenly.userhelp.models.enums.Role;

public record AuthenticatedUserDTO(
        String id,
        String email,
        Role role
) {
}
