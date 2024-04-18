package com.LessonLab.forum.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;
    
    @NotNull(message = "Username should not be null")
    @Size(min = 5, max = 15, message = "Username must be between 5 and 15 characters")
    private String username;

    @Enumerated(EnumType.STRING) 
    private Role role;
    
    @Enumerated(EnumType.STRING) 
    private Permission permission;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Account account;

    // Getters
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
    
    public Role getRole() {
        return role;
    }
    
    public Permission getPermission() {
        return permission;
    }

    public Account getAccount() {
        return account;
    }

    public Status getStatus() {
        return status;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    // Behavioral Methods
    public void activeAccount() {
        setAccount(Account.ACTIVE);
    }

    public void inactivateAccount() {
        setAccount(Account.INACTIVE);
    }

    public void goOnline() {
        setStatus(Status.ONLINE);
    }

    public void goOffline() {
        setStatus(Status.OFFLINE);
    }

    public boolean isOnline() {
        return getStatus() == Status.ONLINE;
    }
}
