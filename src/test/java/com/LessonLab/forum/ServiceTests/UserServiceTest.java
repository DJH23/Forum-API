package com.LessonLab.forum.ServiceTests;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Models.Enums.Role;
import com.LessonLab.forum.Repositories.UserRepository;
import com.LessonLab.forum.Services.UserService;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @After
    public void tearDown() {
        userRepository.deleteAll();
    }
    
    @Test
    public void testDeleteUser() {
        // Create a user with the "ADMIN" role
        User user = new User("testUser", Role.ADMIN);
        user.setUserId(1L);

        // Create a UserDetails instance
        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                                      .password("") // Use an empty password
                                      .roles(user.getRole().toString())
                                      .build();

        // Create a mock Authentication
        Authentication authentication = mock(Authentication.class);

        // Mock Authentication.getPrincipal to return the UserDetails
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // Create a mock SecurityContext
        SecurityContext securityContext = mock(SecurityContext.class);

        // Mock SecurityContext.getAuthentication to return the mock Authentication
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Set the mock SecurityContext in the SecurityContextHolder
        SecurityContextHolder.setContext(securityContext);

        // Mock userRepository.findByUsername to return the user
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        // Mock userRepository.existsById to return true
        when(userRepository.existsById(user.getUserId())).thenReturn(true);

        // Call deleteUser with the user's ID
        userService.deleteUser(user.getUserId());

        // Verify that userRepository.deleteById was called with the user's ID
        verify(userRepository, times(1)).deleteById(user.getUserId());
    }

    @Test
    public void testAddUser_WithValidUser_ShouldSaveUser() {
        // Arrange
        User validUser = new User("validUsername123", Role.ADMIN);
        when(userRepository.findByUsername(validUser.getUsername())).thenReturn(Optional.empty());
        when(userRepository.save(validUser)).thenReturn(validUser);

        // Act
        User savedUser = userService.addUser(validUser);

        // Assert
        assertNotNull(savedUser);
        assertEquals(validUser.getUsername(), savedUser.getUsername());
        verify(userRepository, times(1)).save(validUser);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddUser_WithNullUser_ShouldThrowException() {
        // Act
        userService.addUser(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddUser_WithNullUsername_ShouldThrowException() {
        // Arrange
        User userWithNullUsername = new User(null, Role.ADMIN);

        // Act
        userService.addUser(userWithNullUsername);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddUser_WithEmptyUsername_ShouldThrowException() {
        // Arrange
        User userWithEmptyUsername = new User("", Role.ADMIN);

        // Act
        userService.addUser(userWithEmptyUsername);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddUser_WithInvalidUsername_ShouldThrowException() {
        // Arrange
        User userWithInvalidUsername = new User("Invalid$$Username", Role.ADMIN);

        // Act
        userService.addUser(userWithInvalidUsername);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddUser_WithExistingUsername_ShouldThrowException() {
        // Arrange
        User userWithExistingUsername = new User("existingUser", Role.ADMIN);
        when(userRepository.findByUsername(userWithExistingUsername.getUsername())).thenReturn(Optional.of(new User()));

        // Act
        userService.addUser(userWithExistingUsername);
    }

}


