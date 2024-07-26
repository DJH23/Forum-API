package com.LessonLab.forum.ServiceTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.LessonLab.forum.Models.Role;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Repositories.RoleRepository;
import com.LessonLab.forum.Services.RoleService;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    private Role testRole;
    private User testUser;

    @BeforeEach
    public void setUp() {
        testRole = new Role();
        testRole.setName("ROLE_USER");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");

        Set<Role> roles = new HashSet<>();
        roles.add(testRole);
        testUser.setRoles(roles);
    }

    @Test
    public void testGetUsersByRole_ShouldReturnUsers() {
        // Arrange
        when(roleRepository.findByName("ROLE_USER")).thenReturn(testRole);
        when(roleRepository.findUsersByRolesIn(Collections.singleton(testRole)))
                .thenReturn(Collections.singletonList(testUser));

        // Act
        List<Map<String, String>> usersByRole = roleService.getUsersByRole("ROLE_USER");

        // Assert
        assertNotNull(usersByRole);
        assertFalse(usersByRole.isEmpty());
        assertEquals(1, usersByRole.size());

        Map<String, String> userMap = usersByRole.get(0);
        assertEquals("1", userMap.get("id"));
        assertEquals("testUser", userMap.get("username"));

        verify(roleRepository).findByName("ROLE_USER");
        verify(roleRepository).findUsersByRolesIn(Collections.singleton(testRole));
    }

    @Test
    public void testGetUsersByRole_ShouldThrowIllegalArgumentException_WhenRoleNameIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            roleService.getUsersByRole(null);
        });

        assertEquals("Role name cannot be null", exception.getMessage());
    }

    @Test
    public void testGetUsersByRole_ShouldThrowIllegalArgumentException_WhenRoleNotFound() {
        when(roleRepository.findByName("ROLE_NON_EXISTENT")).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            roleService.getUsersByRole("ROLE_NON_EXISTENT");
        });

        assertEquals("Role not found", exception.getMessage());
        verify(roleRepository).findByName("ROLE_NON_EXISTENT");
    }

    @Test
    public void testGetUsersByRole_ShouldReturnEmptyList_WhenNoUsersWithRole() {
        when(roleRepository.findByName("ROLE_USER")).thenReturn(testRole);
        when(roleRepository.findUsersByRolesIn(Collections.singleton(testRole))).thenReturn(Collections.emptyList());

        List<Map<String, String>> usersByRole = roleService.getUsersByRole("ROLE_USER");

        assertNotNull(usersByRole);
        assertTrue(usersByRole.isEmpty());

        verify(roleRepository).findByName("ROLE_USER");
        verify(roleRepository).findUsersByRolesIn(Collections.singleton(testRole));
    }
}