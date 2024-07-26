package com.LessonLab.forum.ControllerTests;

import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Controllers.UserController;
import com.LessonLab.forum.Models.Enums.Account;
import com.LessonLab.forum.Models.Enums.Status;
import com.LessonLab.forum.Repositories.UserRepository;
import com.LessonLab.forum.Services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserControllerTest {

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testIsValidUser_WithValidCredentials_ShouldReturnTrue() {
        // Arrange
        String username = "validUser";
        String password = "validPassword";
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        // Act
        when(authenticationManager.authenticate(authToken)).thenReturn(authToken);
        boolean result = userController.isValidUser(username, password);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testIsValidUser_WithInvalidCredentials_ShouldReturnFalse() {
        // Arrange
        String username = "invalidUser";
        String password = "invalidPassword";
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        // Act
        when(authenticationManager.authenticate(authToken))
                .thenThrow(new BadCredentialsException("Invalid credentials"));
        boolean result = userController.isValidUser(username, password);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testLogin_WithValidCredentials_ShouldReturnToken() throws Exception {
        // Arrange
        String username = "validUser";
        String password = "validPassword";
        String token = "mockToken";
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        when(authenticationManager.authenticate(authToken)).thenReturn(authToken);
        when(userService.createToken(username)).thenReturn(token);

        // Act & Assert
        mockMvc.perform(post("/api/users/login")
                .param("username", username)
                .param("password", password)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", "Bearer " + token))
                .andExpect(content().string("Logged in Successfully"));
    }

    @Test
    public void testLogin_WithInvalidCredentials_ShouldReturnUnauthorized() throws Exception {
        // Arrange
        String username = "invalidUser";
        String password = "invalidPassword";
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        when(authenticationManager.authenticate(authToken))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        mockMvc.perform(post("/api/users/login")
                .param("username", username)
                .param("password", password)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid Credentials"));
    }

    @Test
    @WithMockUser(roles = { "USER", "ADMIN", "MODERATOR" })
    public void testRegisterUser() {
        User mockUser = new User();
        mockUser.setUsername("testuser");
        mockUser.setPassword("password");

        when(userService.registerUser(anyString(), anyString())).thenReturn(mockUser);

        ResponseEntity<User> response = userController.registerUser("testuser", "password");

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("testuser", response.getBody().getUsername());
    }

    @Test
    public void testGetUserById() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");

        when(userService.getUserById(userId)).thenReturn(Optional.of(user));

        ResponseEntity<?> response = userController.getUserById(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, ((Optional<?>) response.getBody()).orElse(null));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    public void testGetUserByUsername() throws Exception {
        // Create a mock User object
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testUser");
        mockUser.setPassword("password");
        mockUser.setName("Test User");

        // Mock the userService.loadUserByUsername method
        when(userService.loadUserByUsername("testUser")).thenReturn(mockUser);

        // Perform the request and log the response
        MvcResult result = mockMvc.perform(get("/api/users/get-user-by-username/testUser")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(new ObjectMapper().writeValueAsString(mockUser)))
                .andReturn();

        // Print the response for debugging
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(roles = { "USER" })
    public void testGetUserByUsernameAccessDenied() throws Exception {
        // Attempt to access with a non-authorized role
        mockMvc.perform(get("/api/users/get-user-by-username/testUser")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    public void testGetUsersByStatus_WithValidStatus_ShouldReturnUsers() throws Exception {
        // Arrange
        Status status = Status.ONLINE;
        User user1 = new User();
        user1.setUsername("username1");
        User user2 = new User();
        user2.setUsername("username2");
        List<User> expectedUsers = Arrays.asList(user1, user2);

        // Ensure userService.getUsersByStatus() returns expectedUsers
        when(userService.getUsersByStatus(status)).thenReturn(expectedUsers);

        // Act & Assert
        mockMvc.perform(get("/api/users/status/ONLINE")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(new ObjectMapper().writeValueAsString(expectedUsers)));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    public void testGetUsersByStatus_WithNoUsers_ShouldReturnEmptyList() throws Exception {
        // Arrange
        Status status = Status.ONLINE;
        when(userService.getUsersByStatus(status)).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/users/status/ONLINE")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    public void testGetUsersByStatus_WithNullStatus_ShouldThrowException() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/users/status/null")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid status"));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    public void testGetUsersByAccountStatus_WithValidStatus_ShouldReturnUsers() throws Exception {
        // Arrange
        Account accountStatus = Account.ACTIVE;
        User user1 = new User();
        user1.setUsername("username1");
        user1.setAccountStatus(accountStatus);
        User user2 = new User();
        user2.setUsername("username2");
        user2.setAccountStatus(accountStatus);
        List<User> expectedUsers = Arrays.asList(user1, user2);

        when(userService.getUsersByAccountStatus(accountStatus)).thenReturn(expectedUsers);

        // Act & Assert
        mockMvc.perform(get("/api/users/account-status/ACTIVE")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(new ObjectMapper().writeValueAsString(expectedUsers)));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    public void testDeleteUserById() throws Exception {
        // Arrange
        Long userId = 1L;

        doNothing().when(userService).deleteUserById(userId);

        // Act and Assert
        mockMvc.perform(delete("/api/users/" + userId))
                .andExpect(status().isNoContent());

        // Verify that the deleteUserById method was called with the expected user ID
        verify(userService, times(1)).deleteUserById(userId);
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    public void testDeleteUserByUsername() {
        // Arrange
        String username = "testUser";
        doNothing().when(userService).deleteUserByUsername(username);

        // Act
        ResponseEntity<?> response = userController.deleteUserByUsername(username);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Deletion successful", response.getBody());
        verify(userService, times(1)).deleteUserByUsername(username);
    }

}
