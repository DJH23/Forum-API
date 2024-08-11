package com.LessonLab.forum.ServiceTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.*;

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
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.LessonLab.forum.Models.Role;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Models.Enums.Account;
import com.LessonLab.forum.Models.Enums.Status;
import com.LessonLab.forum.Repositories.RoleRepository;
import com.LessonLab.forum.Repositories.UserRepository;
import com.LessonLab.forum.Services.UserService;

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
    private Role testRole;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setPassword("encodedPassword");

        testRole = new Role();
        testRole.setName("ROLE_USER");
        testUser.setRoles(Collections.singleton(testRole));
    }

    @Test
    public void testLoadUserByUsername_Success() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userService.loadUserByUsername("testUser");

        assertNotNull(userDetails);
        assertEquals("testUser", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByUsername("nonExistentUser")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("nonExistentUser");
        });
    }

    @Test
    public void testCreateToken() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));

        String token = userService.createToken("testUser");

        assertNotNull(token);
        assertTrue(token.startsWith("eyJ"));
    }

    @Test
    public void testRegisterUser() {
        User newUser = new User();
        newUser.setUsername("newUser");
        newUser.setPassword("encodedPassword");
        newUser.setAccountStatus(Account.ACTIVE);
        newUser.setStatus(Status.ONLINE);
        newUser.setRoles(Collections.singleton(testRole));

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(testRole);
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        User registeredUser = userService.registerUser("newUser", "password");

        assertNotNull(registeredUser);
        assertEquals("newUser", registeredUser.getUsername());
        assertEquals("encodedPassword", registeredUser.getPassword());
        assertEquals(Account.ACTIVE, registeredUser.getAccountStatus());
        assertEquals(Status.ONLINE, registeredUser.getStatus());
        assertTrue(registeredUser.getRoles().contains(testRole));
    }

    @Test
    public void testUpdateUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User updatedUser = userService.updateUser(testUser);

        assertNotNull(updatedUser);
        assertEquals(testUser.getUsername(), updatedUser.getUsername());
    }

    @Test
    public void testUpdateUser_NullUser() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(null);
        });
    }

    @Test
    public void testUpdateUser_NonExistingUser() {
        User nonExistingUser = new User();
        nonExistingUser.setId(999L);
        nonExistingUser.setUsername("nonExisting");

        when(userRepository.existsById(999L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(nonExistingUser);
        });
    }

    @Test
    public void testGetUsersByStatus() {
        List<User> onlineUsers = Arrays.asList(testUser);
        when(userRepository.findByStatus(Status.ONLINE)).thenReturn(onlineUsers);

        List<User> result = userService.getUsersByStatus(Status.ONLINE);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testUser", result.get(0).getUsername());
    }

    @Test
    public void testGetUsersByStatus_NullStatus() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.getUsersByStatus(null);
        });
    }

    @Test
    public void testGetUsersByAccountStatus() {
        List<User> activeUsers = Arrays.asList(testUser);
        when(userRepository.findByAccountStatus(Account.ACTIVE)).thenReturn(activeUsers);

        List<User> result = userService.getUsersByAccountStatus(Account.ACTIVE);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testUser", result.get(0).getUsername());
    }

    @Test
    public void testGetUsersByAccountStatus_NullStatus() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.getUsersByAccountStatus(null);
        });
    }

    @Test
    public void testDeleteUserById_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> userService.deleteUserById(1L));

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteUserById_NonExistingUser() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            userService.deleteUserById(999L);
        });
    }

    @Test
    public void testDeleteUserByUsername_Success() {
        when(userRepository.existsByUsername("testUser")).thenReturn(true);

        assertDoesNotThrow(() -> userService.deleteUserByUsername("testUser"));

        verify(userRepository, times(1)).deleteByUsername("testUser");
    }

    @Test
    public void testDeleteUserByUsername_NonExistingUser() {
        when(userRepository.existsByUsername("nonExistingUser")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            userService.deleteUserByUsername("nonExistingUser");
        });
    }

    @Test
    public void testSaveUser() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User savedUser = userService.saveUser(testUser);

        assertNotNull(savedUser);
        assertEquals(testUser.getUsername(), savedUser.getUsername());
    }

    @Test
    public void testSaveRole() {
        when(roleRepository.save(any(Role.class))).thenReturn(testRole);

        Role savedRole = userService.saveRole(testRole);

        assertNotNull(savedRole);
        assertEquals(testRole.getName(), savedRole.getName());
    }

    @Test
    public void testAddRoleToUser() {
        Role newRole = new Role();
        newRole.setName("ROLE_ADMIN");

        testUser.setRoles(new HashSet<>());

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(newRole);

        userService.addRoleToUser("testUser", "ROLE_ADMIN");

        verify(userRepository, times(1)).save(testUser);
        assertTrue(testUser.getRoles().contains(newRole));
    }

    @Test
    public void testGetCurrentUser() {
        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn(testUser);

        User currentUser = userService.getCurrentUser();

        assertNotNull(currentUser);
        assertEquals(testUser, currentUser);
    }

    @Test
    public void testGetUser() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));

        User user = userService.getUser("testUser");

        assertNotNull(user);
        assertEquals("testUser", user.getUsername());
    }

    @Test
    public void testGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> userOptional = userService.getUserById(1L);

        assertTrue(userOptional.isPresent());
        assertEquals("testUser", userOptional.get().getUsername());
    }

    @Test
    public void testGetUsers() {
        List<User> userList = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(userList);

        List<User> users = userService.getUsers();

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("testUser", users.get(0).getUsername());
    }
}