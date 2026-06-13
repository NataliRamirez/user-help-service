package com.qvenly.userhelp.models.entity;

import com.qvenly.userhelp.models.enums.Role;

import java.util.ArrayList;
import java.util.List;

public class ManualSection {
    private String id;
    private String title;
    private Role role;
    private List<String> keywords = new ArrayList<>();
    private String content;

    public ManualSection() {
    }

    public ManualSection(String id, String title, Role role, List<String> keywords, String content) {
        this.id = id;
        this.title = title;
        this.role = role;
        this.keywords = keywords;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
