package com.qvenly.userhelp.models.dto;

import com.qvenly.userhelp.models.enums.Role;

public record SearchResultResponseDTO(
        String sectionId,
        String title,
        Role role,
        String snippet,
        int score
) {
}
