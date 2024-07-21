package com.LessonLab.forum.Controllers;

import java.util.List;
import java.util.Optional;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Models.UserExtension;
import com.LessonLab.forum.Models.Enums.Account;

import com.LessonLab.forum.Models.Enums.Status;
import com.LessonLab.forum.Services.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public boolean isValidUser(String username, String password) {
        try {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
            authenticationManager.authenticate(authToken);
            return true;
        } catch (AuthenticationException e) {
            return false;
        }
    }

    @PostMapping("/login")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MODERATOR')")
    @Operation(summary = "User login", description = "Authenticate a user and return a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logged in Successfully", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "401", description = "Invalid Credentials", content = @Content(schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        if (isValidUser(username, password)) {
            String token = userService.createToken(username);
            return ResponseEntity.ok().header("Authorization", "Bearer " + token).body("Logged in Successfully");
        }
        return ResponseEntity.status(401).body("Invalid Credentials");
    }

    @PostMapping("/register-user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MODERATOR')")
    @Operation(summary = "Register user", description = "Register a new user")
    @ApiResponse(responseCode = "200", description = "User registered", content = @Content(schema = @Schema(implementation = User.class)))
    public ResponseEntity<User> registerUser(@RequestParam String username, @RequestParam String password) {
        User user = userService.registerUser(username, password);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/get-user-by-id/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @Operation(summary = "Get user by ID", description = "Retrieve a user by their ID")
    @ApiResponse(responseCode = "200", description = "User retrieved", content = @Content(schema = @Schema(implementation = User.class)))
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/get-user-by-username/{username}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @Operation(summary = "Get user by username", description = "Retrieve a user by their username")
    @ApiResponse(responseCode = "200", description = "User retrieved", content = @Content(schema = @Schema(implementation = UserDetails.class)))
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        UserDetails user = userService.loadUserByUsername(username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @Operation(summary = "Get users by status", description = "Retrieve users by their status")
    @ApiResponse(responseCode = "200", description = "Users retrieved", content = @Content(schema = @Schema(implementation = List.class)))
    public ResponseEntity<?> getUsersByStatus(@PathVariable Status status) {
        List<UserExtension> users = userService.getUsersByStatus(status);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/account-status/{accountStatus}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @Operation(summary = "Get users by account status", description = "Retrieve users by their account status")
    @ApiResponse(responseCode = "200", description = "Users retrieved", content = @Content(schema = @Schema(implementation = List.class)))
    public ResponseEntity<?> getUsersByAccountStatus(@PathVariable Account accountStatus) {
        List<UserExtension> users = userService.getUsersByAccountStatus(accountStatus);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @Operation(summary = "Delete user by ID", description = "Delete a user by their ID")
    @ApiResponse(responseCode = "204", description = "User deleted")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/username/{username}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @Operation(summary = "Delete user by username", description = "Delete a user by their username")
    @ApiResponse(responseCode = "200", description = "Deletion successful", content = @Content(schema = @Schema(implementation = String.class)))
    public ResponseEntity<?> deleteUserByUsername(@PathVariable String username) {
        userService.deleteUserByUsername(username);
        return new ResponseEntity<>("Deletion successful", HttpStatus.OK);
    }

}
