package com.LessonLab.forum.ServiceTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.LessonLab.forum.Models.Role;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Models.Enums.Account;
import com.LessonLab.forum.Models.Enums.Status;
import com.LessonLab.forum.Repositories.RoleRepository;
import com.LessonLab.forum.Repositories.UserRepository;
import com.LessonLab.forum.Services.UserService;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setPassword("encodedPassword");

        Role testRole = new Role();
        testRole.setName("ADMIN");
        testUser.setRoles(Collections.singleton(testRole));
    }

    

    @Test
    public void testDeleteUserById() {
        // Arrange
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(testUser.getUsername())
                .password(testUser.getPassword())
                .authorities(testUser.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toList()))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
        when(userRepository.existsById(testUser.getId())).thenReturn(true);

        // Act
        userService.deleteUserById(testUser.getId());

        // Assert
        verify(userRepository, times(1)).deleteById(testUser.getId());
    }

    @Test
    public void testRegisterUser_WithValidCredentials_ShouldSaveUser() {
        // Arrange
        String username = "validUsername123";
        String password = "validPassword123";
        String encodedPassword = "encodedPassword";

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(encodedPassword);
        newUser.setAccountStatus(Account.ACTIVE);
        newUser.setStatus(Status.ONLINE);

        Role userRole = new Role("ROLE_USER");

        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setRoles(Collections.singleton(userRole));
            return user;
        });

        // Act
        User registeredUser = userService.registerUser(username, password);

        // Assert
        assertNotNull(registeredUser);
        assertEquals(username, registeredUser.getUsername());
        assertEquals(encodedPassword, registeredUser.getPassword());
        assertEquals(Account.ACTIVE, registeredUser.getAccountStatus());
        assertEquals(Status.ONLINE, registeredUser.getStatus());
        assertTrue(registeredUser.getRoles().contains(userRole));

        verify(userRepository, times(2)).save(any(User.class));
        verify(roleRepository, times(1)).findByName("ROLE_USER");
    }

    @Test
    public void testRegisterUser_WithNonExistentRole_ShouldCreateRoleAndSaveUser() {
        // Arrange
        String username = "validUsername123";
        String password = "validPassword123";
        String encodedPassword = "encodedPassword";
        User savedUser = new User(username, encodedPassword);
        savedUser.setAccountStatus(Account.ACTIVE);
        savedUser.setStatus(Status.ONLINE);
        Role userRole = new Role("ROLE_USER");

        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(null);
        when(roleRepository.save(any(Role.class))).thenReturn(userRole);

        // Act
        User registeredUser = userService.registerUser(username, password);

        // Assert
        assertNotNull(registeredUser);
        assertEquals(username, registeredUser.getUsername());
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    public void testRegisterUser_WithNullUsername_ShouldThrowException() {
        // Arrange
        String password = "validPassword123";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(null, password));
    }

    @Test
    public void testRegisterUser_WithEmptyUsername_ShouldThrowException() {
        // Arrange
        String password = "validPassword123";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser("", password));
    }

    @Test
    public void testRegisterUser_WithNullPassword_ShouldThrowException() {
        // Arrange
        String username = "validUsername123";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(username, null));
    }

    @Test
    public void testRegisterUser_WithEmptyPassword_ShouldThrowException() {
        // Arrange
        String username = "validUsername123";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(username, ""));
    }

    @Test
    public void testRegisterUser_WithExistingUsername_ShouldThrowException() {
        // Arrange
        String username = "existingUser";
        String password = "validPassword123";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User()));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(username, password));
    }

    @Test
    public void testGetUser_WithValidId_ShouldReturnUser() {
        // Arrange
        User expectedUser = new User("username", Role.ADMIN);
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
        User actualUser = userService.getUser(1L);

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
        User currentUser = new User("username", Role.ADMIN);
        when(userRepository.findByUsername(userDetails.getUsername())).thenReturn(Optional.of(currentUser));

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> userService.getUser(null));
    }

    @Test
    public void testGetUser_WithNonExistingId_ShouldThrowException() {
        // Arrange
        User currentUser = new User("username", Role.ADMIN);
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
        User currentUser = new User("username", Role.USER);
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
        User currentUser = new User("currentUsername", Role.ADMIN);
        when(userRepository.findByUsername(currentUser.getUsername())).thenReturn(Optional.of(currentUser));

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(currentUser.getUsername());

        User userToUpdate = new User("username", Role.USER);
        userToUpdate.setUserId(1L);
        when(userRepository.existsById(userToUpdate.getUserId())).thenReturn(true);
        when(userRepository.save(userToUpdate)).thenReturn(userToUpdate);

        // Act
        User updatedUser = userService.updateUser(userToUpdate);

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
        User currentUser = new User("username", Role.ADMIN);
        when(userRepository.findByUsername(userDetails.getUsername())).thenReturn(Optional.of(currentUser));

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(null));
    }

    @Test
    public void testUpdateUser_WithNonExistingUser_ShouldThrowException() {
        // Arrange
        User currentUser = new User("currentUsername", Role.ADMIN);
        when(userRepository.findByUsername(currentUser.getUsername())).thenReturn(Optional.of(currentUser));

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(currentUser.getUsername());

        User userToUpdate = new User("username", Role.USER);
        userToUpdate.setUserId(1L);
        when(userRepository.existsById(userToUpdate.getUserId())).thenReturn(false);

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(userToUpdate));
    }

    @Test
    public void testUpdateUser_WithNullUsername_ShouldThrowException() {
        // Arrange
        User currentUser = new User("currentUsername", Role.ADMIN);
        when(userRepository.findByUsername(currentUser.getUsername())).thenReturn(Optional.of(currentUser));

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(currentUser.getUsername());

        User userToUpdate = new User(null, Role.USER);
        userToUpdate.setUserId(1L);
        when(userRepository.existsById(userToUpdate.getUserId())).thenReturn(true);

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(userToUpdate));
    }

    @Test
    public void testUpdateUser_WithoutPermission_ShouldThrowException() {
        // Arrange
        User currentUser = new User("currentUsername", Role.USER);
        when(userRepository.findByUsername(currentUser.getUsername())).thenReturn(Optional.of(currentUser));

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(currentUser.getUsername());

        User userToUpdate = new User("username", Role.USER);
        userToUpdate.setUserId(1L);
        when(userRepository.existsById(userToUpdate.getUserId())).thenReturn(true);

        // Act and Assert
        assertThrows(AccessDeniedException.class, () -> userService.updateUser(userToUpdate));
    }

    @Test
    public void testGetUsersByStatus_WithValidStatus_ShouldReturnUsers() {
        // Arrange
        Status status = Status.ONLINE;
        User user1 = new User("username1", Role.ADMIN);
        User user2 = new User("username2", Role.ADMIN);
        List<User> expectedUsers = Arrays.asList(user1, user2);
        when(userRepository.findByStatus(status)).thenReturn(expectedUsers);

        // Act
        List<User> actualUsers = userService.getUsersByStatus(status);

        // Assert
        assertEquals(expectedUsers, actualUsers);
    }

    @Test
    public void testGetUsersByStatus_WithNoUsers_ShouldReturnEmptyList() {
        // Arrange
        Status status = Status.ONLINE;
        when(userRepository.findByStatus(status)).thenReturn(Collections.emptyList());

        // Act
        List<User> actualUsers = userService.getUsersByStatus(status);

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
        User user1 = new User("username1", Role.USER);
        user1.setAccountStatus(accountStatus);
        User user2 = new User("username2", Role.USER);
        user2.setAccountStatus(accountStatus);
        List<User> expectedUsers = Arrays.asList(user1, user2);
        when(userRepository.findByAccountStatus(accountStatus)).thenReturn(expectedUsers);

        // Act
        List<User> actualUsers = userService.getUsersByAccountStatus(accountStatus);

        // Assert
        assertEquals(expectedUsers, actualUsers);
    }

    @Test
    public void testGetUsersByAccountStatus_WithNoUsers_ShouldReturnEmptyList() {
        // Arrange
        Account accountStatus = Account.ACTIVE;
        when(userRepository.findByAccountStatus(accountStatus)).thenReturn(Collections.emptyList());

        // Act
        List<User> actualUsers = userService.getUsersByAccountStatus(accountStatus);

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
        User currentUser = new User("currentUsername", Role.ADMIN);
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
        User currentUser = new User("currentUsername", Role.ADMIN);
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
        User currentUser = new User("currentUsername", Role.ADMIN);
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
        User currentUser = new User("currentUsername", Role.USER);
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
        User currentUser = new User("currentUsername", Role.ADMIN);
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
        User currentUser = new User("currentUsername", Role.USER);
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
