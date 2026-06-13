package com.qvenly.userhelp.models.dto;

import java.util.List;

public record HelpHomeResponseDTO(
        String title,
        String subtitle,
        String searchPlaceholder,
        List<HelpCategoryDTO> categories,
        ChatWidgetDTO chat
) {
}
