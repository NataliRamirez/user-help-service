package com.qvenly.userhelp.services;

import com.qvenly.userhelp.models.dto.AuthenticatedUserDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
public class AuthUserContextService {
    private final AuthIntegrationService authIntegrationService;

    public AuthUserContextService(AuthIntegrationService authIntegrationService) {
        this.authIntegrationService = authIntegrationService;
    }

    public AuthenticatedUserDTO currentUser(HttpHeaders headers) {
        return authIntegrationService.getAuthenticatedUser(headers);
    }

    public void requireAdmin(AuthenticatedUserDTO user) {
        authIntegrationService.validateAdmin(user);
    }
}
