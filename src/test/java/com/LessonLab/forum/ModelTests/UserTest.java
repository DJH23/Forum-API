package com.LessonLab.forum.ModelTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.LessonLab.forum.Models.Content;
import com.LessonLab.forum.Models.Role;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Models.UserExtension;
import com.LessonLab.forum.Models.Enums.Account;
import com.LessonLab.forum.Models.Enums.Status;

public class UserTest {

    @Test
    public void testGettersAndSetters() {
        // Arrange
        User user = new User();
        UserExtension userExtension = new UserExtension();
        Long userId = 1L;
        String username = "username";
        Role role = new Role();
        Status status = Status.ONLINE;
        Account accountStatus = Account.ACTIVE;
        List<Content> contents = new ArrayList<>();

        // Act
        user.setId(userId);
        user.setUsername(username);
        role.setId(userId);
        role.setName("ADMIN");
        userExtension.setStatus(status);
        userExtension.setAccountStatus(accountStatus);
        userExtension.setContents(contents);

        // Assert
        assertEquals(userId, user.getId());
        assertEquals(username, user.getUsername());
        assertEquals(userId, role.getId());
        assertEquals("ADMIN", role.getName());
        assertEquals(status, userExtension.getStatus());
        assertEquals(accountStatus, userExtension.getAccountStatus());
        assertEquals(contents, userExtension.getContents());
    }

    @Test
    public void testGoOnline() {
        // Arrange
        UserExtension user = new UserExtension();

        // Act
        user.goOnline();

        // Assert
        assertEquals(Status.ONLINE, user.getStatus());
    }

    @Test
    public void testGoOffline() {
        // Arrange
        UserExtension user = new UserExtension();

        // Act
        user.goOffline();

        // Assert
        assertEquals(Status.OFFLINE, user.getStatus());
    }

    @Test
    public void testIsOnline() {
        // Arrange
        UserExtension user = new UserExtension();

        // Act
        user.goOnline();
        boolean isOnline = user.isOnline();

        // Assert
        assertTrue(isOnline);
    }
}