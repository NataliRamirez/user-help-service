package com.qvenly.userhelp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "qvenly.help")
public class HelpProperties {
    private String supportPersistence = "in-memory";
    private List<String> blockedWords = new ArrayList<>();
    private List<String> allowedKeywords = new ArrayList<>();

    public String getSupportPersistence() {
        return supportPersistence;
    }

    public void setSupportPersistence(String supportPersistence) {
        this.supportPersistence = supportPersistence;
    }

    public List<String> getBlockedWords() {
        return blockedWords;
    }

    public void setBlockedWords(List<String> blockedWords) {
        this.blockedWords = blockedWords;
    }

    public List<String> getAllowedKeywords() {
        return allowedKeywords;
    }

    public void setAllowedKeywords(List<String> allowedKeywords) {
        this.allowedKeywords = allowedKeywords;
    }
}
