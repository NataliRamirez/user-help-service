package com.qvenly.userhelp.models.dto;

import java.time.LocalDateTime;

public record SupportResponseDTO(
        String message,
        LocalDateTime respondedAt,
        String respondedBy
) {
}
