package com.qvenly.userhelp.services;

import com.qvenly.userhelp.config.AuthProperties;
import com.qvenly.userhelp.exceptions.BusinessException;
import com.qvenly.userhelp.models.dto.AuthenticatedUserDTO;
import com.qvenly.userhelp.models.enums.Role;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
public class AuthIntegrationService {
    private final AuthProperties authProperties;

    public AuthIntegrationService(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    public AuthenticatedUserDTO getAuthenticatedUser(HttpHeaders headers) {
        String userId = firstHeader(headers, authProperties.getUserIdHeader());
        String userEmail = firstHeader(headers, authProperties.getUserEmailHeader());
        String userRole = firstHeader(headers, authProperties.getUserRoleHeader());
        String authorization = headers.getFirst(HttpHeaders.AUTHORIZATION);

        if (isBlank(userId) || isBlank(userEmail) || isBlank(userRole)) {
            if (!isBlank(authorization)) {
                throw new BusinessException("Auth Service aún no está conectado: se recibió Authorization, pero Gateway/Auth no propagó usuario, correo y rol autenticado");
            }
            throw new BusinessException("Usuario no autenticado: falta información propagada por Auth Service o Gateway");
        }

        return new AuthenticatedUserDTO(userId.trim(), userEmail.trim(), Role.fromNullable(userRole));
    }

    public void validateAuthenticated(AuthenticatedUserDTO user) {
        if (user == null || isBlank(user.id()) || isBlank(user.email())) {
            throw new BusinessException("Usuario no autenticado");
        }
    }

    public void validateAdmin(AuthenticatedUserDTO user) {
        validateAuthenticated(user);
        if (user.role() != Role.ADMIN) {
            throw new BusinessException("Acceso denegado: esta operación requiere rol ADMIN");
        }
    }

    private String firstHeader(HttpHeaders headers, String name) {
        return headers.getFirst(name);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
