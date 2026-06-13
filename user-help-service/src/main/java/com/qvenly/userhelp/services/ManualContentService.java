package com.qvenly.userhelp.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qvenly.userhelp.exceptions.ResourceNotFoundException;
import com.qvenly.userhelp.models.dto.ChatWidgetDTO;
import com.qvenly.userhelp.models.dto.HelpCategoryDTO;
import com.qvenly.userhelp.models.dto.HelpHomeResponseDTO;
import com.qvenly.userhelp.models.dto.ManualSectionResponseDTO;
import com.qvenly.userhelp.models.entity.ManualSection;
import com.qvenly.userhelp.models.enums.Role;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ManualContentService {
    private final ObjectMapper objectMapper;
    private final RolePersonalizationService rolePersonalizationService;
    private final List<ManualSection> sections = new ArrayList<>();
    private final List<HelpCategoryDTO> categories = List.of(
            new HelpCategoryDTO("primeros-pasos", "Primeros Pasos", "Aprende lo básico para comenzar a usar Qvenly", "rocket"),
            new HelpCategoryDTO("gestion-eventos", "Gestión de Eventos", "Crea y administra tus eventos exitosamente", "target"),
            new HelpCategoryDTO("usuarios-roles", "Usuarios y Roles", "Administra asistentes, personal y permisos", "users"),
            new HelpCategoryDTO("planes-facturacion", "Planes y Facturación", "Información sobre planes, pagos y facturas", "card")
    );

    public ManualContentService(ObjectMapper objectMapper, RolePersonalizationService rolePersonalizationService) {
        this.objectMapper = objectMapper;
        this.rolePersonalizationService = rolePersonalizationService;
    }

    @PostConstruct
    public void loadSections() throws IOException {
        ClassPathResource resource = new ClassPathResource("manual-sections.json");
        List<ManualSection> loaded = objectMapper.readValue(resource.getInputStream(), new TypeReference<>() {});
        sections.clear();
        sections.addAll(loaded);
    }

    public HelpHomeResponseDTO home(Role role) {
        return new HelpHomeResponseDTO(
                "Centro de Ayuda",
                "Encuentra respuestas, guías y tutoriales para aprovechar al máximo Qvenly",
                "¿En qué podemos ayudarte?",
                categories,
                new ChatWidgetDTO(
                        "Asistente Qvenly",
                        "En línea",
                        "¡Hola! 👋 Soy el Asistente Virtual de Qvenly. ¿En qué puedo ayudarte hoy?",
                        rolePersonalizationService.suggestionsFor(role),
                        "Escribe tu pregunta..."
                )
        );
    }

    public List<HelpCategoryDTO> categories() {
        return categories;
    }

    public HelpCategoryDTO categoryBySlug(String slug) {
        return categories.stream()
                .filter(category -> category.slug().equalsIgnoreCase(slug))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la categoría del centro de ayuda"));
    }

    public List<ManualSection> all() {
        return List.copyOf(sections);
    }

    public List<ManualSectionResponseDTO> findByRole(Role role) {
        return sections.stream()
                .filter(section -> section.getRole() == Role.USER || section.getRole() == role)
                .map(this::toResponse)
                .toList();
    }

    public ManualSectionResponseDTO findById(String id) {
        return sections.stream()
                .filter(section -> section.getId().equalsIgnoreCase(id))
                .findFirst()
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la sección del manual solicitada"));
    }

    public ManualSectionResponseDTO toResponse(ManualSection section) {
        return new ManualSectionResponseDTO(
                section.getId(),
                section.getTitle(),
                section.getRole(),
                section.getKeywords(),
                section.getContent()
        );
    }
}
