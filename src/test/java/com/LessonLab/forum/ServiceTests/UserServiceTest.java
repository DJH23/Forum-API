package com.LessonLab.forum.ServiceTests;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

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
    
    @Test
    public void testDeleteUser() {
        // Create a user with the "ADMIN" role
        User user = new User("testUser", Role.ADMIN);
        user.setId(1L);

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
        when(userRepository.existsById(user.getId())).thenReturn(true);

        // Call deleteUser with the user's ID
        userService.deleteUser(user.getId());

        // Verify that userRepository.deleteById was called with the user's ID
        verify(userRepository, times(1)).deleteById(user.getId());
    }

    @Test
    public void testAddUser() {
        // Create a user with the "ADMIN" role
        User user = new User("testUser", Role.ADMIN);
    
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
        
        // Call getCurrentUser to get the current user
        User currentUser = userService.getCurrentUser();
        
        // Mock userRepository.findByUsername to return an empty Optional
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        
        // Call addUser with the user
        User addedUser = userService.addUser(user);
    
        // Verify that userRepository.save was called with the user
        verify(userRepository, times(1)).save(user);
    
        // Assert that currentUser is not null
        assertNotNull(currentUser);
    
        // Assert that the returned user is the same as the user
        assertEquals(user, addedUser);
    }
}


