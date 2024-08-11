package com.LessonLab.forum.ControllerTests;

import com.LessonLab.forum.Controllers.RoleController;
import com.LessonLab.forum.Models.Role;
import com.LessonLab.forum.Repositories.RoleRepository;
import com.LessonLab.forum.Repositories.UserRepository;
import com.LessonLab.forum.Services.RoleService;
import com.LessonLab.forum.Services.UserService;
import com.LessonLab.forum.dtos.RoleToUserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class RoleControllerTest {

    @MockBean
    private UserService userService;

    @MockBean
    private RoleService roleService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @Autowired
    private RoleController roleController;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MockitoAnnotations.openMocks(this);
    }

    // Successfully save a new role when valid role data is provided
    @Test
    public void test_save_role_successfully() {
        // Prepare
        Role role = new Role("ROLE_USER");

        // Mocking
        UserService userService = mock(UserService.class);
        RoleController roleController = new RoleController();
        ReflectionTestUtils.setField(roleController, "userService", userService);

        // Define behavior
        when(userService.saveRole(any(Role.class))).thenReturn(role); // Adjust the return value as needed

        // Call the method
        roleController.saveRole(role);

        // Verify
        verify(userService, times(1)).saveRole(role);
    }

    @Test
    @WithMockUser(roles = { "USER" }) // User role does not have ADMIN or MODERATOR roles
    public void testSaveRoleForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/add-role-type")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"ROLE_NAME\"}") // Add a valid Role JSON payload here
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden()); // Expecting a 403 Forbidden status
    }

    private RoleToUserDTO createValidRoleToUserDTO() {
        RoleToUserDTO dto = new RoleToUserDTO();
        dto.setUsername("validUser");
        dto.setRoleName("validRole");
        return dto;
    }

    private RoleToUserDTO createInvalidRoleToUserDTO() {
        RoleToUserDTO dto = new RoleToUserDTO();
        dto.setUsername(""); // Invalid username
        dto.setRoleName(""); // Invalid role name
        return dto;
    }

    // Successfully add a role to a user when valid username and roleName are
    // provided
    @Test
    public void test_add_role_to_user_success() {
        // Arrange
        UserService userService = mock(UserService.class);
        RoleController roleController = new RoleController();
        ReflectionTestUtils.setField(roleController, "userService", userService);

        RoleToUserDTO roleToUserDTO = new RoleToUserDTO();
        roleToUserDTO.setUsername("validUser");
        roleToUserDTO.setRoleName("validRole");

        // Act
        roleController.addRoleToUser(roleToUserDTO);

        // Assert
        verify(userService, times(1)).addRoleToUser("validUser", "validRole");
    }

    // Return HTTP 400 Bad Request when the input RoleToUserDTO is invalid
    @Test
    public void test_add_role_to_user_invalid_input() throws Exception {
        // Arrange
        UserService userService = mock(UserService.class);
        RoleController roleController = new RoleController();
        ReflectionTestUtils.setField(roleController, "userService", userService);

        RoleToUserDTO roleToUserDTO = new RoleToUserDTO();
        roleToUserDTO.setUsername("");
        roleToUserDTO.setRoleName("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            roleController.addRoleToUser(roleToUserDTO);
        });
    }

    @Test
    public void AddRoleToUser_Success_ReturnsNoContent() throws Exception {
        RoleToUserDTO dto = createValidRoleToUserDTO();
        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.post("/roles/add-role-to-user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNoContent());
    }

    @Test
    public void AddRoleToUser_InvalidInput_ReturnsBadRequest() throws Exception {
        RoleToUserDTO dto = createInvalidRoleToUserDTO();
        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.post("/roles/add-role-to-user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void AddRoleToUser_UserOrRoleNotFound_ReturnsNotFound() throws Exception {
        RoleToUserDTO dto = createValidRoleToUserDTO();
        dto.setUsername("nonExistentUser"); // Simulate non-existent user
        dto.setRoleName("nonExistentRole"); // Simulate non-existent role
        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.post("/roles/add-role-to-user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetUsersByRole() throws Exception {
        // Arrange
        String role = "ADMIN";
        List<Map<String, String>> users = new ArrayList<>();
        Map<String, String> user = new HashMap<>();
        user.put("id", "1");
        user.put("username", "testUser");
        users.add(user);

        when(roleService.getUsersByRole(role)).thenReturn(users);

        // Act and Assert
        mockMvc.perform(get("/api/roles/get-user-by-role/" + role))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(users)));

        // Verify that the getUsersByRole method was called with the expected role
        verify(roleService, times(1)).getUsersByRole(role);
    }

    @Test
    public void testGetAllRoleTypes() {
        // Arrange
        Role role1 = new Role();
        role1.setName("ADMIN");
        Role role2 = new Role();
        role2.setName("USER");

        List<Role> roles = Arrays.asList(role1, role2);
        when(roleRepository.findAll()).thenReturn(roles);

        // Act
        ResponseEntity<?> response = roleController.getAllRoleTypes();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(roles, response.getBody());
    }

}
