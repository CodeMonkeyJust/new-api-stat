package com.newapi.analyzer.dto.response;

public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String displayName;
    private Long role;
    private Long status;
    private String group;

    public UserDTO() {
    }

    public UserDTO(Long id, String username, String email, String displayName, Long role, Long status, String group) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.displayName = displayName;
        this.role = role;
        this.status = status;
        this.group = group;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Long getRole() {
        return role;
    }

    public void setRole(Long role) {
        this.role = role;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
