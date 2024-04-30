package com.LessonLab.forum.ControllerTests;

import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Models.Enums.Role;
import com.LessonLab.forum.Models.Enums.Status;
import com.LessonLab.forum.Repositories.UserRepository;
import com.LessonLab.forum.Services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

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

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testAddUser() throws Exception {

        // Arrange
        User user = new User();
        user.setUsername("testUser");
        // user.setPassword("testPassword");

        when(userService.addUser(any(User.class))).thenReturn(user);

        String requestJson = new ObjectMapper().writeValueAsString(user);

        // Act and Assert
        mockMvc.perform(post("/api/users/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().json(requestJson));

        // Verify that the addUser method was called with the expected user
        verify(userService, times(1)).addUser(any(User.class));
    }

    @Test
    public void testGetUser() throws Exception {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setUserId(userId);
        user.setUsername("testUser");
        // user.setPassword("testPassword");

        when(userService.getUser(userId)).thenReturn(user);

        // Act and Assert
        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(user)));

        // Verify that the getUser method was called with the expected user id
        verify(userService, times(1)).getUser(userId);
    }

    @Test
    public void testUpdateUser() throws Exception {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setUserId(userId);
        user.setUsername("testUser");
        // user.setPassword("testPassword");

        when(userService.updateUser(any(User.class))).thenReturn(user);

        String requestJson = new ObjectMapper().writeValueAsString(user);

        // Act and Assert
        mockMvc.perform(put("/api/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json(requestJson));

        // Verify that the updateUser method was called with the expected user
        verify(userService, times(1)).updateUser(any(User.class));
    }

    @Test
    public void testGetUsersByRole() throws Exception {
        // Arrange
        Role role = Role.USER;
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setUserId(1L);
        user.setUsername("testUser");
        // user.setPassword("testPassword");
        user.setRole(role);
        users.add(user);

        when(userService.getUsersByRole(role)).thenReturn(users);

        // Act and Assert
        mockMvc.perform(get("/api/users/role/" + role))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(users)));

        // Verify that the getUsersByRole method was called with the expected role
        verify(userService, times(1)).getUsersByRole(role);
    }

    @Test
    public void testGetUsersByStatus() throws Exception {
        // Arrange
        Status status = Status.ONLINE;
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setUserId(1L);
        user.setUsername("testUser");
        user.setStatus(status);
        users.add(user);

        // Print status and users
        System.out.println("Status: " + status);
        System.out.println("Users: " + users);

        // Define the behavior of userService.getUsersByStatus(status)
        when(userService.getUsersByStatus(status)).thenReturn(users);

        // Act and Assert
        MvcResult result = mockMvc.perform(get("/api/status/" + status))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(users)))
                .andReturn();

        // Print response
        System.out.println("Response: " + result.getResponse().getContentAsString());

        // Verify that userService.getUsersByStatus(status) was called with the correct
        // status
        verify(userService, times(1)).getUsersByStatus(status);
    }

}
