package com.qvenly.userhelp.services;

import com.qvenly.userhelp.models.dto.SearchResultResponseDTO;
import com.qvenly.userhelp.models.entity.ManualSection;
import com.qvenly.userhelp.models.enums.Role;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class HelpSearchService {
    private final ManualContentService manualContentService;
    private final SafetyPolicyService safetyPolicyService;

    public HelpSearchService(ManualContentService manualContentService, SafetyPolicyService safetyPolicyService) {
        this.manualContentService = manualContentService;
        this.safetyPolicyService = safetyPolicyService;
    }

    public List<SearchResultResponseDTO> search(String query, Role role) {
        String normalizedQuery = safetyPolicyService.normalize(query);
        if (normalizedQuery.isBlank()) {
            return List.of();
        }

        return manualContentService.all().stream()
                .filter(section -> section.getRole() == Role.USER || section.getRole() == role)
                .map(section -> score(section, normalizedQuery))
                .filter(result -> result.score() > 0)
                .sorted(Comparator.comparing(SearchResultResponseDTO::score).reversed())
                .toList();
    }

    private SearchResultResponseDTO score(ManualSection section, String normalizedQuery) {
        int score = 0;
        String title = safetyPolicyService.normalize(section.getTitle());
        String content = safetyPolicyService.normalize(section.getContent());

        if (title.contains(normalizedQuery)) {
            score += 5;
        }
        if (content.contains(normalizedQuery)) {
            score += 3;
        }
        for (String keyword : section.getKeywords()) {
            String normalizedKeyword = safetyPolicyService.normalize(keyword);
            if (normalizedQuery.contains(normalizedKeyword) || normalizedKeyword.contains(normalizedQuery)) {
                score += 4;
            }
        }

        return new SearchResultResponseDTO(
                section.getId(),
                section.getTitle(),
                section.getRole(),
                section.getContent(),
                score
        );
    }
}
