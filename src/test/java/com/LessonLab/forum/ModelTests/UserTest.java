package com.LessonLab.forum.ModelTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.LessonLab.forum.Models.Content;
import com.LessonLab.forum.Models.Role;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Models.Enums.Account;
import com.LessonLab.forum.Models.Enums.Status;

public class UserTest {

    @Test
    public void testGettersAndSetters() {
        // Arrange
        User user = new User();
        Long userId = 1L;
        String username = "username";
        String password = "password";
        String name = "name";
        Status status = Status.ONLINE;
        Account accountStatus = Account.ACTIVE;
        List<Content> contents = new ArrayList<>();
        Role role = new Role();
        role.setName("ROLE_ADMIN");
        List<Role> roles = new ArrayList<>();
        roles.add(role);

        // Act
        user.setId(userId);
        user.setUsername(username);
        user.setPassword(password);
        user.setName(name);
        user.setStatus(status);
        user.setAccountStatus(accountStatus);
        user.setContents(contents);
        user.setRoles(roles);

        // Assert
        assertEquals(userId, user.getId());
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(name, user.getName());
        assertEquals(status, user.getStatus());
        assertEquals(accountStatus, user.getAccountStatus());
        assertEquals(contents, user.getContents());
        assertEquals(roles, user.getRoles());
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

        // Act
        user.goOffline();
        isOnline = user.isOnline();

        // Assert
        assertFalse(isOnline);
    }

    @Test
    public void testGetAuthorities() {
        // Arrange
        User user = new User();
        Role role = new Role();
        role.setName("ROLE_ADMIN");
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        user.setRoles(roles);

        // Act
        List<GrantedAuthority> authorities = new ArrayList<>(user.getAuthorities());

        // Assert
        assertEquals(1, authorities.size());
        assertEquals(new SimpleGrantedAuthority("ROLE_ADMIN"), authorities.get(0));
    }

    @Test
    public void testAccountStatusFlags() {
        // Arrange
        User user = new User();

        // Act & Assert
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());

        // Change flags
        user.setEnabled(false);
        assertFalse(user.isEnabled());
    }
}
