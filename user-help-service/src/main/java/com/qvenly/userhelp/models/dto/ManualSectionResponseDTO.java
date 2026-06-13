package com.qvenly.userhelp.models.dto;

import com.qvenly.userhelp.models.enums.Role;

import java.util.List;

public record ManualSectionResponseDTO(
        String id,
        String title,
        Role role,
        List<String> keywords,
        String content
) {
}
