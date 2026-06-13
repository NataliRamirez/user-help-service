package com.qvenly.userhelp.models.dto;

import com.qvenly.userhelp.models.enums.SupportPriority;
import com.qvenly.userhelp.models.enums.SupportStatus;
import com.qvenly.userhelp.models.enums.SupportType;

import java.time.LocalDateTime;
import java.util.UUID;

public record SupportTicketResponseDTO(
        UUID id,
        String userId,
        String userEmail,
        SupportType type,
        String description,
        SupportPriority priority,
        SupportStatus status,
        String adminResponse,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
