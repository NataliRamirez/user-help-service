package com.qvenly.userhelp.models.entity;

import com.qvenly.userhelp.models.enums.SupportPriority;
import com.qvenly.userhelp.models.enums.SupportStatus;
import com.qvenly.userhelp.models.enums.SupportType;

import java.time.LocalDateTime;
import java.util.UUID;

public class SupportTicket {
    private UUID id;
    private String userId;
    private String userEmail;
    private SupportType type;
    private String description;
    private SupportPriority priority;
    private SupportStatus status;
    private String adminResponse;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SupportTicket() {
    }

    public SupportTicket(UUID id, String userId, String userEmail, SupportType type, String description,
            SupportPriority priority, SupportStatus status, String adminResponse,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.userEmail = userEmail;
        this.type = type;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.adminResponse = adminResponse;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public SupportType getType() {
        return type;
    }

    public void setType(SupportType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SupportPriority getPriority() {
        return priority;
    }

    public void setPriority(SupportPriority priority) {
        this.priority = priority;
    }

    public SupportStatus getStatus() {
        return status;
    }

    public void setStatus(SupportStatus status) {
        this.status = status;
    }

    public String getAdminResponse() {
        return adminResponse;
    }

    public void setAdminResponse(String adminResponse) {
        this.adminResponse = adminResponse;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
