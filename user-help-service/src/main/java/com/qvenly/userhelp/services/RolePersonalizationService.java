package com.qvenly.userhelp.services;

import com.qvenly.userhelp.models.enums.Role;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RolePersonalizationService {
    private static final Map<Role, List<String>> ROLE_SUGGESTIONS = Map.of(
            Role.ADMIN, List.of(
                    "Consultar solicitudes de soporte",
                    "Responder una petición de soporte",
                    "Gestionar usuarios y planes"
            ),
            Role.ORGANIZER, List.of(
                    "Crear evento",
                    "Gestionar actividades",
                    "Administrar participantes"
            ),
            Role.USER, List.of(
                    "Ver mi perfil",
                    "Consultar manual",
                    "Crear solicitud de soporte"
            )
    );

    public List<String> suggestionsFor(Role role) {
        return ROLE_SUGGESTIONS.getOrDefault(role, ROLE_SUGGESTIONS.get(Role.USER));
    }

    public String systemInstructionFor(Role role) {
        return switch (role) {
            case ADMIN -> "Responde como asistente del administrador de Qvenly. Enfócate en gestión de usuarios, planes, solicitudes de soporte y seguimiento administrativo.";
            case ORGANIZER -> "Responde como asistente del organizador de Qvenly. Enfócate en eventos, actividades, participantes, invitaciones y operación del evento.";
            case USER -> "Responde como asistente del usuario general de Qvenly. Enfócate en perfil, navegación, manual, soporte y uso básico de la plataforma.";
        };
    }
}
