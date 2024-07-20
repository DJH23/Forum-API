package com.LessonLab.forum.Models;

import java.util.List;

import jakarta.persistence.*;

import com.LessonLab.forum.Models.Enums.Status;
import com.LessonLab.forum.Models.Enums.Account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "userId")
@Entity
@JsonIgnoreProperties({ "id", "name", "username", "password", "roles", "authorities", "accountNonExpired",
        "accountNonLocked", "credentialsNonExpired", "enabled", "userExtension" })
public class UserExtension extends User {

    @Enumerated(EnumType.STRING)
    private Status status = Status.ONLINE;

    @Enumerated(EnumType.STRING)
    private Account accountStatus = Account.ACTIVE;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Content> contents;

    public UserExtension() {
    }
    // Getters and Setters

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

    @JsonProperty
    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @JsonProperty
    @Override
    public String getUsername() {
        return super.getUsername();
    }

    @Override
    public void setUsername(String username) {
        super.setUsername(username);
    }

}
