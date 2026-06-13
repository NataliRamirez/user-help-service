package com.qvenly.userhelp.models.dto;

import com.qvenly.userhelp.models.enums.SupportPriority;
import com.qvenly.userhelp.models.enums.SupportType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateSupportTicketDTO(
        @NotNull(message = "El tipo de solicitud es obligatorio")
        SupportType type,

        @NotBlank(message = "La descripción es obligatoria")
        @Size(min = 10, max = 1000, message = "La descripción debe tener entre 10 y 1000 caracteres")
        String description,

        @NotNull(message = "La prioridad es obligatoria")
        SupportPriority priority
) {
}
