package com.LessonLab.forum.Models;

import com.LessonLab.forum.Models.Enums.Account;
import com.LessonLab.forum.Models.Enums.Status;

public class UserExtensionDTO {
    private Status status;
    private Account accountStatus;

    // Getters and Setters
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
}
