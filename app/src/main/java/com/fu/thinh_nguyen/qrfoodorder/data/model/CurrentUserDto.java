package com.fu.thinh_nguyen.qrfoodorder.data.model;

public class CurrentUserDto {
    private String userId;
    private String username;
    private String role;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
