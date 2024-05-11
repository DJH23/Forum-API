package com.LessonLab.forum.Controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.LessonLab.forum.Models.Role;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Models.UserExtension;
import com.LessonLab.forum.Models.Enums.Account;

import com.LessonLab.forum.Models.Enums.Status;
import com.LessonLab.forum.Services.UserService;
import com.LessonLab.forum.dtos.RoleToUserDTO;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/add-user")
    public ResponseEntity<User> addUser(@RequestParam String username) {
        User user = new User();
        user.setUsername(username);
        user = userService.saveUser(user);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/get-user-by-id/{id}")
    @PreAuthorize("hasPermission(#id, 'READ_USER')")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/get-user-by-username/{username}")
    @PreAuthorize("hasPermission(#username, 'READ_USER')")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        UserDetails user = userService.loadUserByUsername(username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<?> getUsersByRole(@PathVariable Role role) {
        List<Role> users = userService.getUsersByRole(role);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getUsersByStatus(@PathVariable Status status) {
        List<UserExtension> users = userService.getUsersByStatus(status);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/account-status/{accountStatus}")
    public ResponseEntity<?> getUsersByAccountStatus(@PathVariable Account accountStatus) {
        List<UserExtension> users = userService.getUsersByAccountStatus(accountStatus);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission(#id, 'DELETE_USER')")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/username/{username}")
    @PreAuthorize("hasPermission(#username, 'DELETE_USER')")
    public ResponseEntity<?> deleteUserByUsername(@PathVariable String username) {
        userService.deleteUserByUsername(username);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/roles")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveRole(@RequestBody Role role) {
        userService.saveRole(role);
    }

    @PostMapping("/roles/addtouser")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addRoleToUser(@RequestBody RoleToUserDTO roleToUserDTO) {
        userService.addRoleToUser(roleToUserDTO.getUsername(), roleToUserDTO.getRoleName());
    }
}
