package com.LessonLab.forum.Models;

import java.util.List;

import jakarta.persistence.*;

import com.LessonLab.forum.Models.Enums.Role;
import com.LessonLab.forum.Models.Enums.Status;
import com.LessonLab.forum.security.models.User;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.LessonLab.forum.Models.Enums.Account;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "userId")
@Entity
public class UserExtension extends User {

    @Enumerated(EnumType.STRING)
    private Status status = Status.ONLINE;

    @Enumerated(EnumType.STRING)
    private Account accountStatus = Account.ACTIVE;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @JsonIgnore
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
}
