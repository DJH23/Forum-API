package com.LessonLab.forum.ServiceTests;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.anyString;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.LessonLab.forum.Models.UserExtension;
import com.LessonLab.forum.Models.Enums.Account;
import com.LessonLab.forum.Models.Enums.Role;
import com.LessonLab.forum.Models.Enums.Status;
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
    public void testDeleteUserById() {
        // Create a user with the "ADMIN" role
        UserExtension user = new UserExtension("testUser", Role.ADMIN);
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
        userService.deleteUserById(user.getUserId());

        // Verify that userRepository.deleteById was called with the user's ID
        verify(userRepository, times(1)).deleteById(user.getUserId());
    }

    @Test
    public void testAddUser_WithValidUser_ShouldSaveUser() {
        // Arrange
        UserExtension validUser = new UserExtension("validUsername123", Role.ADMIN);
        when(userRepository.findByUsername(validUser.getUsername())).thenReturn(Optional.empty());
        when(userRepository.save(validUser)).thenReturn(validUser);

        // Act
        UserExtension savedUser = userService.addUser(validUser);

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
        UserExtension userWithNullUsername = new UserExtension(null, Role.ADMIN);

        // Act
        userService.addUser(userWithNullUsername);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddUser_WithEmptyUsername_ShouldThrowException() {
        // Arrange
        UserExtension userWithEmptyUsername = new UserExtension("", Role.ADMIN);

        // Act
        userService.addUser(userWithEmptyUsername);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddUser_WithInvalidUsername_ShouldThrowException() {
        // Arrange
        UserExtension userWithInvalidUsername = new UserExtension("Invalid$$Username", Role.ADMIN);

        // Act
        userService.addUser(userWithInvalidUsername);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddUser_WithExistingUsername_ShouldThrowException() {
        // Arrange
        UserExtension userWithExistingUsername = new UserExtension("existingUser", Role.ADMIN);
        when(userRepository.findByUsername(userWithExistingUsername.getUsername())).thenReturn(Optional.of(new UserExtension()));

        // Act
        userService.addUser(userWithExistingUsername);
    }

    @Test
    public void testGetUser_WithValidId_ShouldReturnUser() {
        // Arrange
        UserExtension expectedUser = new UserExtension("username", Role.ADMIN);
        when(userRepository.findById(1L)).thenReturn(Optional.of(expectedUser));

        // Mock the SecurityContext and the Authentication object
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Mock the getPrincipal() method to return a UserDetails object
        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // Mock the userRepository to return a User when findByUsername is called
        when(userRepository.findByUsername(userDetails.getUsername())).thenReturn(Optional.of(expectedUser));

        // Act
        UserExtension actualUser = userService.getUser(1L);

        // Assert
        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void testGetUser_WithNullId_ShouldThrowException() {
        // Arrange
        // Mock the SecurityContext and the Authentication object
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Mock the getPrincipal() method to return a UserDetails object
        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // Mock the userRepository to return a User when findByUsername is called
        UserExtension currentUser = new UserExtension("username", Role.ADMIN);
        when(userRepository.findByUsername(userDetails.getUsername())).thenReturn(Optional.of(currentUser));

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> userService.getUser(null));
    }

    @Test
    public void testGetUser_WithNonExistingId_ShouldThrowException() {
        // Arrange
        UserExtension currentUser = new UserExtension("username", Role.ADMIN);
        when(userRepository.findByUsername(currentUser.getUsername())).thenReturn(Optional.of(currentUser));

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(currentUser.getUsername());

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        assertThrows(IllegalArgumentException.class, () -> userService.getUser(1L));
    }

    @Test
    public void testGetUser_WithoutPermission_ShouldThrowException() {
        // Arrange
        UserExtension currentUser = new UserExtension("username", Role.USER);
        when(userRepository.findByUsername(currentUser.getUsername())).thenReturn(Optional.of(currentUser));

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(currentUser.getUsername());

        // Act
        assertThrows(AccessDeniedException.class, () -> userService.getUser(1L));
    }

    @Test
    public void testUpdateUser_WithValidUser_ShouldReturnUpdatedUser() {
        // Arrange
        UserExtension currentUser = new UserExtension("currentUsername", Role.ADMIN);
        when(userRepository.findByUsername(currentUser.getUsername())).thenReturn(Optional.of(currentUser));

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(currentUser.getUsername());

        UserExtension userToUpdate = new UserExtension("username", Role.USER);
        userToUpdate.setUserId(1L);
        when(userRepository.existsById(userToUpdate.getUserId())).thenReturn(true);
        when(userRepository.save(userToUpdate)).thenReturn(userToUpdate);

        // Act
        UserExtension updatedUser = userService.updateUser(userToUpdate);

        // Assert
        assertEquals(userToUpdate, updatedUser);
    }

    @Test
    public void testUpdateUser_WithNullUser_ShouldThrowException() {
        // Arrange
        // Mock the SecurityContext and the Authentication object
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Mock the getPrincipal() method to return a UserDetails object
        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // Mock the userRepository to return a User when findByUsername is called
        UserExtension currentUser = new UserExtension("username", Role.ADMIN);
        when(userRepository.findByUsername(userDetails.getUsername())).thenReturn(Optional.of(currentUser));

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(null));
    }

    @Test
    public void testUpdateUser_WithNonExistingUser_ShouldThrowException() {
        // Arrange
        UserExtension currentUser = new UserExtension("currentUsername", Role.ADMIN);
        when(userRepository.findByUsername(currentUser.getUsername())).thenReturn(Optional.of(currentUser));

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(currentUser.getUsername());

        UserExtension userToUpdate = new UserExtension("username", Role.USER);
        userToUpdate.setUserId(1L);
        when(userRepository.existsById(userToUpdate.getUserId())).thenReturn(false);

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(userToUpdate));
    }

    @Test
    public void testUpdateUser_WithNullUsername_ShouldThrowException() {
        // Arrange
        UserExtension currentUser = new UserExtension("currentUsername", Role.ADMIN);
        when(userRepository.findByUsername(currentUser.getUsername())).thenReturn(Optional.of(currentUser));

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(currentUser.getUsername());

        UserExtension userToUpdate = new UserExtension(null, Role.USER);
        userToUpdate.setUserId(1L);
        when(userRepository.existsById(userToUpdate.getUserId())).thenReturn(true);

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(userToUpdate));
    }

    @Test
    public void testUpdateUser_WithoutPermission_ShouldThrowException() {
        // Arrange
        UserExtension currentUser = new UserExtension("currentUsername", Role.USER);
        when(userRepository.findByUsername(currentUser.getUsername())).thenReturn(Optional.of(currentUser));

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(currentUser.getUsername());

        UserExtension userToUpdate = new UserExtension("username", Role.USER);
        userToUpdate.setUserId(1L);
        when(userRepository.existsById(userToUpdate.getUserId())).thenReturn(true);

        // Act and Assert
        assertThrows(AccessDeniedException.class, () -> userService.updateUser(userToUpdate));
    }

    @Test
    public void testGetUsersByRole_WithValidRole_ShouldReturnUsers() {
        // Arrange
        Role role = Role.ADMIN;
        List<UserExtension> expectedUsers = Arrays.asList(new UserExtension("username1", role), new UserExtension("username2", role));
        when(userRepository.findByRole(role)).thenReturn(expectedUsers);

        // Act
        List<UserExtension> actualUsers = userService.getUsersByRole(role);

        // Assert
        assertEquals(expectedUsers, actualUsers);
    }

    @Test
    public void testGetUsersByRole_WithNoUsers_ShouldReturnEmptyList() {
        // Arrange
        Role role = Role.ADMIN;
        when(userRepository.findByRole(role)).thenReturn(Collections.emptyList());

        // Act
        List<UserExtension> actualUsers = userService.getUsersByRole(role);

        // Assert
        assertTrue(actualUsers.isEmpty());
    }

    @Test
    public void testGetUsersByRole_WithNullRole_ShouldThrowException() {
        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> userService.getUsersByRole(null));
    }

    @Test
    public void testGetUsersByStatus_WithValidStatus_ShouldReturnUsers() {
        // Arrange
        Status status = Status.ONLINE;
        UserExtension user1 = new UserExtension("username1", Role.ADMIN);
        UserExtension user2 = new UserExtension("username2", Role.ADMIN);
        List<UserExtension> expectedUsers = Arrays.asList(user1, user2);
        when(userRepository.findByStatus(status)).thenReturn(expectedUsers);

        // Act
        List<UserExtension> actualUsers = userService.getUsersByStatus(status);

        // Assert
        assertEquals(expectedUsers, actualUsers);
    }

    @Test
    public void testGetUsersByStatus_WithNoUsers_ShouldReturnEmptyList() {
        // Arrange
        Status status = Status.ONLINE;
        when(userRepository.findByStatus(status)).thenReturn(Collections.emptyList());

        // Act
        List<UserExtension> actualUsers = userService.getUsersByStatus(status);

        // Assert
        assertTrue(actualUsers.isEmpty());
    }

    @Test
    public void testGetUsersByStatus_WithNullStatus_ShouldThrowException() {
        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> userService.getUsersByStatus(null));
    }

    @Test
    public void testGetUsersByAccountStatus_WithValidStatus_ShouldReturnUsers() {
        // Arrange
        Account accountStatus = Account.ACTIVE;
        UserExtension user1 = new UserExtension("username1", Role.USER);
        user1.setAccountStatus(accountStatus);
        UserExtension user2 = new UserExtension("username2", Role.USER);
        user2.setAccountStatus(accountStatus);
        List<UserExtension> expectedUsers = Arrays.asList(user1, user2);
        when(userRepository.findByAccountStatus(accountStatus)).thenReturn(expectedUsers);

        // Act
        List<UserExtension> actualUsers = userService.getUsersByAccountStatus(accountStatus);

        // Assert
        assertEquals(expectedUsers, actualUsers);
    }

    @Test
    public void testGetUsersByAccountStatus_WithNoUsers_ShouldReturnEmptyList() {
        // Arrange
        Account accountStatus = Account.ACTIVE;
        when(userRepository.findByAccountStatus(accountStatus)).thenReturn(Collections.emptyList());

        // Act
        List<UserExtension> actualUsers = userService.getUsersByAccountStatus(accountStatus);

        // Assert
        assertTrue(actualUsers.isEmpty());
    }

    @Test
    public void testGetUsersByAccountStatus_WithNullStatus_ShouldThrowException() {
        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> userService.getUsersByAccountStatus(null));
    }

    @Test
    public void testDeleteUser_WithValidId_ShouldDeleteUser() {
        // Arrange
        UserExtension currentUser = new UserExtension("currentUsername", Role.ADMIN);
        when(userRepository.findByUsername(currentUser.getUsername())).thenReturn(Optional.of(currentUser));

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(currentUser.getUsername());

        Long id = 1L;
        when(userRepository.existsById(id)).thenReturn(true);

        // Act
        userService.deleteUserById(id);

        // Assert
        verify(userRepository, times(1)).deleteById(id);
    }

    @Test
    public void testDeleteUser_WithNullId_ShouldThrowException() {
        // Arrange
        UserExtension currentUser = new UserExtension("currentUsername", Role.ADMIN);
        when(userRepository.findByUsername(currentUser.getUsername())).thenReturn(Optional.of(currentUser));

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(currentUser.getUsername());

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> userService.deleteUserById(null));
    }

    @Test
    public void testDeleteUser_WithNonExistingId_ShouldThrowException() {
        // Arrange
        UserExtension currentUser = new UserExtension("currentUsername", Role.ADMIN);
        when(userRepository.findByUsername(currentUser.getUsername())).thenReturn(Optional.of(currentUser));

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(currentUser.getUsername());

        Long id = 1L;
        when(userRepository.existsById(id)).thenReturn(false);

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> userService.deleteUserById(id));
    }

    @Test
    public void testDeleteUser_WithoutPermission_ShouldThrowException() {
        // Arrange
        UserExtension currentUser = new UserExtension("currentUsername", Role.USER);
        when(userRepository.findByUsername(currentUser.getUsername())).thenReturn(Optional.of(currentUser));

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(currentUser.getUsername());

        Long id = 1L;
        when(userRepository.existsById(id)).thenReturn(true);

        // Act and Assert
        assertThrows(AccessDeniedException.class, () -> userService.deleteUserById(id));
    }

    @Test
    public void testDeleteUserByUsername() {
        // Arrange
        UserExtension currentUser = new UserExtension("currentUsername", Role.ADMIN);
        when(userRepository.findByUsername(currentUser.getUsername())).thenReturn(Optional.of(currentUser));

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(currentUser.getUsername());

        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // Act
        userService.deleteUserByUsername("testUser");

        // Assert
        verify(userRepository, times(1)).deleteByUsername("testUser");
    }

    @Test
    public void testDeleteUserByUsernameWithoutPermission() {
        // Arrange
        UserExtension currentUser = new UserExtension("currentUsername", Role.USER);
        when(userRepository.findByUsername(currentUser.getUsername())).thenReturn(Optional.of(currentUser));

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(currentUser.getUsername());

        // Act and Assert
        assertThrows(AccessDeniedException.class, () -> userService.deleteUserByUsername("testUser"));
    }

}
