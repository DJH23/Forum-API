package com.LessonLab.forum.ModelTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.LessonLab.forum.Models.Content;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Models.Enums.Account;
import com.LessonLab.forum.Models.Enums.Role;
import com.LessonLab.forum.Models.Enums.Status;

public class UserTest {
    
@Test
public void testGettersAndSetters() {
    // Arrange
    User user = new User();
    Long userId = 1L;
    String username = "username";
    Role role = Role.USER;
    Status status = Status.ONLINE;
    Account accountStatus = Account.ACTIVE;
    List<Content> contents = new ArrayList<>();

    // Act
    user.setUserId(userId);
    user.setUsername(username);
    user.setRole(role);
    user.setStatus(status);
    user.setAccountStatus(accountStatus);
    user.setContents(contents);

    // Assert
    assertEquals(userId, user.getUserId());
    assertEquals(username, user.getUsername());
    assertEquals(role, user.getRole());
    assertEquals(status, user.getStatus());
    assertEquals(accountStatus, user.getAccountStatus());
    assertEquals(contents, user.getContents());
}

@Test
public void testGoOnline() {
    // Arrange
    User user = new User();

    // Act
    user.goOnline();

    // Assert
    assertEquals(Status.ONLINE, user.getStatus());
}

@Test
public void testGoOffline() {
    // Arrange
    User user = new User();

    // Act
    user.goOffline();

    // Assert
    assertEquals(Status.OFFLINE, user.getStatus());
}

@Test
public void testIsOnline() {
    // Arrange
    User user = new User();

    // Act
    user.goOnline();
    boolean isOnline = user.isOnline();

    // Assert
    assertTrue(isOnline);
}
}