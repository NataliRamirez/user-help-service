package com.qvenly.userhelp.services;

import com.qvenly.userhelp.models.dto.AuthenticatedUserDTO;
import com.qvenly.userhelp.models.dto.ChatRequestDTO;
import com.qvenly.userhelp.models.dto.ChatResponseDTO;
import com.qvenly.userhelp.models.dto.SearchResultResponseDTO;
import com.qvenly.userhelp.models.enums.Role;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatbotService {
    private final SafetyPolicyService safetyPolicyService;
    private final HelpSearchService helpSearchService;
    private final GeminiClient geminiClient;
    private final RolePersonalizationService rolePersonalizationService;

    public ChatbotService(SafetyPolicyService safetyPolicyService, HelpSearchService helpSearchService,
                          GeminiClient geminiClient, RolePersonalizationService rolePersonalizationService) {
        this.safetyPolicyService = safetyPolicyService;
        this.helpSearchService = helpSearchService;
        this.geminiClient = geminiClient;
        this.rolePersonalizationService = rolePersonalizationService;
    }

    public ChatResponseDTO answer(ChatRequestDTO request, AuthenticatedUserDTO user) {
        Role role = user.role();
        List<String> suggestions = rolePersonalizationService.suggestionsFor(role);

        if (safetyPolicyService.hasBlockedWord(request.message()) || !safetyPolicyService.isProbablyQvenlyRelated(request.message())) {
            return new ChatResponseDTO(
                    "Solo puedo ayudarte con temas relacionados con el uso de Qvenly. Puedes preguntarme sobre eventos, usuarios, roles, planes, soporte o navegación dentro del sistema.",
                    false,
                    false,
                    suggestions,
                    "policy"
            );
        }

        List<SearchResultResponseDTO> searchResults = helpSearchService.search(request.message(), role);
        String localContext = buildLocalContext(searchResults);
        String prompt = "Usuario autenticado: " + user.email()
                + "\nRol: " + role
                + "\nPregunta del usuario: " + request.message()
                + "\n\nContexto del centro de ayuda:\n" + localContext
                + "\n\nReglas: responde únicamente sobre Qvenly, no inventes funcionalidades y, si no hay suficiente información, indica que puede registrar soporte.";

        String geminiAnswer = geminiClient.generateAnswer(rolePersonalizationService.systemInstructionFor(role), prompt);
        if (geminiAnswer != null && !geminiAnswer.isBlank() && !safetyPolicyService.hasBlockedWord(geminiAnswer)) {
            return new ChatResponseDTO(geminiAnswer, true, false, suggestions, "gemini");
        }

        if (!searchResults.isEmpty()) {
            SearchResultResponseDTO best = searchResults.get(0);
            return new ChatResponseDTO(best.snippet(), true, false, suggestions, "manual");
        }

        return new ChatResponseDTO(
                "No encontré una respuesta segura para tu consulta. Puedes registrar una solicitud de soporte para recibir ayuda especializada.",
                false,
                true,
                List.of("Crear solicitud de soporte", "Volver al centro de ayuda"),
                "fallback"
        );
    }

    private String buildLocalContext(List<SearchResultResponseDTO> results) {
        if (results.isEmpty()) {
            return "No hay coincidencias locales.";
        }
        StringBuilder builder = new StringBuilder();
        for (SearchResultResponseDTO result : results.stream().limit(3).toList()) {
            builder.append("- ")
                    .append(result.title())
                    .append(": ")
                    .append(result.snippet())
                    .append("\n");
        }
        return builder.toString();
    }
}
