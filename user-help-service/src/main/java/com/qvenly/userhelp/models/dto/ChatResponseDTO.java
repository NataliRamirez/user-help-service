package com.qvenly.userhelp.models.dto;

import java.util.List;

public record ChatResponseDTO(
        String answer,
        boolean answered,
        boolean redirectToSupport,
        List<String> suggestions,
        String source
) {
}
