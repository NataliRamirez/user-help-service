package com.qvenly.userhelp.controllers;

import com.qvenly.userhelp.models.dto.AuthenticatedUserDTO;
import com.qvenly.userhelp.models.dto.ChatRequestDTO;
import com.qvenly.userhelp.models.dto.ChatResponseDTO;
import com.qvenly.userhelp.services.AuthUserContextService;
import com.qvenly.userhelp.services.ChatbotService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/help/chat")
public class ChatbotController {
    private final ChatbotService chatbotService;
    private final AuthUserContextService authUserContextService;

    public ChatbotController(ChatbotService chatbotService, AuthUserContextService authUserContextService) {
        this.chatbotService = chatbotService;
        this.authUserContextService = authUserContextService;
    }

    @PostMapping
    public ChatResponseDTO chat(@Valid @RequestBody ChatRequestDTO request,
                                @RequestHeader HttpHeaders headers) {
        AuthenticatedUserDTO user = authUserContextService.currentUser(headers);
        return chatbotService.answer(request, user);
    }
}
