package com.LessonLab.forum.Controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.LessonLab.forum.Models.Role;
import com.LessonLab.forum.Repositories.RoleRepository;
import com.LessonLab.forum.Services.RoleService;
import com.LessonLab.forum.Services.UserService;
import com.LessonLab.forum.dtos.RoleToUserDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleRepository roleRepository;

    @PostMapping("/add-role-type")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add new role type", description = "Save a new role type to the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Role created"),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "User or role not found", content = @Content(schema = @Schema(implementation = String.class)))
    })
    public void saveRole(@RequestBody Role role) {
        userService.saveRole(role);
    }

    @PostMapping("/roles/add-role-to-user")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Add role to user", description = "Assign a role to a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Role assigned to user"),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "User or role not found", content = @Content(schema = @Schema(implementation = String.class)))
    })
    public void addRoleToUser(@RequestBody RoleToUserDTO roleToUserDTO) {
        if (roleToUserDTO.getUsername() == null || roleToUserDTO.getUsername().isEmpty() ||
                roleToUserDTO.getRoleName() == null || roleToUserDTO.getRoleName().isEmpty()) {
            throw new IllegalArgumentException("Username and role name cannot be null or empty");
        }
        userService.addRoleToUser(roleToUserDTO.getUsername(), roleToUserDTO.getRoleName());
    }

    @GetMapping("/get-user-by-role/{role}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @Operation(summary = "Get users by role", description = "Retrieve users who have a specific role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Role not found", content = @Content(schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<?> getUsersByRole(@PathVariable String role) {
        List<Map<String, String>> users = roleService.getUsersByRole(role);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/get-all-role-types")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @Operation(summary = "Get all role types", description = "Retrieve all available role types")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Roles retrieved", content = @Content(schema = @Schema(implementation = Role.class)))
    })
    public ResponseEntity<?> getAllRoleTypes() {
        List<Role> roles = roleRepository.findAll();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }
}
