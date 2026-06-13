package com.qvenly.userhelp.models.dto;

import java.util.List;

public record ChatWidgetDTO(
        String title,
        String status,
        String welcomeMessage,
        List<String> quickActions,
        String inputPlaceholder
) {
}
