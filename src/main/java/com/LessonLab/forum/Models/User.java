package com.LessonLab.forum.Models;

import java.util.List;

import jakarta.persistence.*;

import com.LessonLab.forum.Models.Enums.Role;
import com.LessonLab.forum.Models.Enums.Status;
import com.LessonLab.forum.Models.Enums.Account;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    @Column(name = "user_id")
    private Long userId;

    private String username;

    @Enumerated(EnumType.STRING) 
    private Role role;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Account accountStatus;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Content> contents;  

    public User() {} // no-arg constructor for serialization and testing

    public User(String username, Role role) {
        this.username = username;
        this.role = role;
    }
    
    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Content> getContents() {
        return contents;
    }

    public void setContents(List<Content> contents) {
        this.contents = contents;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Account getAccountStatus() {
        return accountStatus;
    }
    
    public void setAccountStatus(Account accountStatus) {
        this.accountStatus = accountStatus;
    }

    // Behavioral Methods
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
