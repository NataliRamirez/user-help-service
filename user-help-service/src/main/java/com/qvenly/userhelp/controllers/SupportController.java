package com.qvenly.userhelp.controllers;

import com.qvenly.userhelp.models.dto.AuthenticatedUserDTO;
import com.qvenly.userhelp.models.dto.CreateSupportTicketDTO;
import com.qvenly.userhelp.models.dto.SupportReplyRequestDTO;
import com.qvenly.userhelp.models.dto.SupportResponseDTO;
import com.qvenly.userhelp.models.dto.SupportTicketResponseDTO;
import com.qvenly.userhelp.services.AuthUserContextService;
import com.qvenly.userhelp.services.SupportTicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/help")
public class SupportController {
    private final SupportTicketService supportTicketService;
    private final AuthUserContextService authUserContextService;

    public SupportController(SupportTicketService supportTicketService, AuthUserContextService authUserContextService) {
        this.supportTicketService = supportTicketService;
        this.authUserContextService = authUserContextService;
    }

    @PostMapping("/support")
    public SupportTicketResponseDTO create(@Valid @RequestBody CreateSupportTicketDTO request,
                                           @RequestHeader HttpHeaders headers) {
        AuthenticatedUserDTO user = authUserContextService.currentUser(headers);
        return supportTicketService.create(request, user);
    }

    @GetMapping("/support/my")
    public List<SupportTicketResponseDTO> findMine(@RequestHeader HttpHeaders headers) {
        AuthenticatedUserDTO user = authUserContextService.currentUser(headers);
        return supportTicketService.findMine(user);
    }

    @GetMapping("/admin/support")
    public List<SupportTicketResponseDTO> findAll(@RequestHeader HttpHeaders headers) {
        AuthenticatedUserDTO user = authUserContextService.currentUser(headers);
        authUserContextService.requireAdmin(user);
        return supportTicketService.findAll();
    }

    @GetMapping("/admin/support/{id}")
    public SupportTicketResponseDTO findById(@PathVariable UUID id, @RequestHeader HttpHeaders headers) {
        AuthenticatedUserDTO user = authUserContextService.currentUser(headers);
        authUserContextService.requireAdmin(user);
        return supportTicketService.findById(id);
    }

    @GetMapping("/support/{id}/responses")
    public List<SupportResponseDTO> getResponses(@PathVariable UUID id, @RequestHeader HttpHeaders headers) {
        AuthenticatedUserDTO user = authUserContextService.currentUser(headers);
        return supportTicketService.getResponses(id, user);
    }

    @PostMapping("/admin/support/{id}/response")
    public SupportTicketResponseDTO reply(@PathVariable UUID id,
                                          @Valid @RequestBody SupportReplyRequestDTO request,
                                          @RequestHeader HttpHeaders headers) {
        AuthenticatedUserDTO user = authUserContextService.currentUser(headers);
        authUserContextService.requireAdmin(user);
        return supportTicketService.reply(id, request);
    }
}
