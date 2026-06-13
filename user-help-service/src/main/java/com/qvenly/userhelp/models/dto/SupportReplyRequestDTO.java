package com.qvenly.userhelp.models.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SupportReplyRequestDTO(
        @NotBlank(message = "La respuesta es obligatoria")
        @Size(min = 5, max = 1000, message = "La respuesta debe tener entre 5 y 1000 caracteres")
        String response
) {
}
