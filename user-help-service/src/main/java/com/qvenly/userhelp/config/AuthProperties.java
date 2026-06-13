package com.qvenly.userhelp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "qvenly.auth")
public class AuthProperties {
    private String userIdHeader = "X-Auth-User-Id";
    private String userEmailHeader = "X-Auth-User-Email";
    private String userRoleHeader = "X-Auth-User-Role";

    public String getUserIdHeader() {
        return userIdHeader;
    }

    public void setUserIdHeader(String userIdHeader) {
        this.userIdHeader = userIdHeader;
    }

    public String getUserEmailHeader() {
        return userEmailHeader;
    }

    public void setUserEmailHeader(String userEmailHeader) {
        this.userEmailHeader = userEmailHeader;
    }

    public String getUserRoleHeader() {
        return userRoleHeader;
    }

    public void setUserRoleHeader(String userRoleHeader) {
        this.userRoleHeader = userRoleHeader;
    }
}
