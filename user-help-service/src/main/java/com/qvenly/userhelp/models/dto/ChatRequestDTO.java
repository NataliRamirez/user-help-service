package com.qvenly.userhelp.models.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatRequestDTO(
        @NotBlank(message = "La consulta es obligatoria")
        @Size(max = 600, message = "La consulta no puede superar 600 caracteres")
        String message
) {
}
