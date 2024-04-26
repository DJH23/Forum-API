package com.LessonLab.forum.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Models.Enums.Account;
import com.LessonLab.forum.Models.Enums.Role;
import com.LessonLab.forum.Models.Enums.Status;
import com.LessonLab.forum.Services.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    
    @PostMapping("/")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        User savedUser = userService.addUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission(#id, 'READ_USER')")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        // Call the getUser method from the service
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasPermission(#id, 'WRITE_USER')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        // Call the updateUser method from the service
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<?> getUsersByRole(@PathVariable Role role) {
        // Call the getUsersByRole method from the service
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getUsersByStatus(@PathVariable Status status) {
        // Call the getUsersByStatus method from the service
    }

    @GetMapping("/account-status/{accountStatus}")
    public ResponseEntity<?> getUsersByAccountStatus(@PathVariable Account accountStatus) {
        // Call the getUsersByAccountStatus method from the service
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission(#id, 'DELETE_USER')")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id) {
        // Call the deleteUserById method from the service
    }

    @DeleteMapping("/username/{username}")
    @PreAuthorize("hasPermission(#username, 'DELETE_USER')")
    public ResponseEntity<?> deleteUserByUsername(@PathVariable String username) {
        // Call the deleteUserByUsername method from the service
    }
}
