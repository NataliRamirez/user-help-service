package com.qvenly.userhelp.services;

import com.qvenly.userhelp.config.HelpProperties;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Locale;

@Service
public class SafetyPolicyService {
    private final HelpProperties helpProperties;

    public SafetyPolicyService(HelpProperties helpProperties) {
        this.helpProperties = helpProperties;
    }

    public boolean hasBlockedWord(String message) {
        String normalized = normalize(message);
        return helpProperties.getBlockedWords().stream()
                .map(this::normalize)
                .anyMatch(normalized::contains);
    }

    public boolean isProbablyQvenlyRelated(String message) {
        String normalized = normalize(message);
        return helpProperties.getAllowedKeywords().stream()
                .map(this::normalize)
                .anyMatch(normalized::contains);
    }

    public String normalize(String value) {
        if (value == null) {
            return "";
        }
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return normalized.toLowerCase(Locale.ROOT).trim();
    }
}
