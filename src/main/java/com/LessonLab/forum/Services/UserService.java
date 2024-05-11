package com.LessonLab.forum.Services;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Models.Enums.Account;
import com.LessonLab.forum.Models.Enums.Permission;
import com.LessonLab.forum.Models.Enums.Role;
import com.LessonLab.forum.Models.Enums.Status;
import com.LessonLab.forum.Repositories.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Utility method to check if a user has a specific permission
    private boolean hasPermission(User user, Permission permission) {
        return user.getRole().getPermissions().contains(permission);
    }

    public User getCurrentUser() {
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUsername();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public User getUser(Long id) {
        /*
         * User currentUser = getCurrentUser();
         * if (!hasPermission(currentUser, Permission.READ_USER)) {
         * throw new
         * AccessDeniedException("You do not have permission to read user data.");
         * }
         */

        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
    }

    @Transactional
    public User addUser(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (!username.matches("[A-Za-z0-9_]+")) {
            throw new IllegalArgumentException("Username contains invalid characters");
        }
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists!");
        }

        User user = new User();
        user.setUsername(username);
        user.setStatus(Status.ONLINE); // Default status
        user.setAccountStatus(Account.ACTIVE); // Default account status
        user.setRole(Role.USER); // Consider having a default role or handling roles appropriately.

        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(User user) {
        /*
         * User currentUser = getCurrentUser();
         * if (!hasPermission(currentUser, Permission.WRITE_USER)) {
         * throw new
         * AccessDeniedException("You do not have permission to update users.");
         * }
         */

        if (user == null) {
            throw new IllegalArgumentException("Cannot update a null user");
        }
        if (user.getUserId() == null || !userRepository.existsById(user.getUserId())) {
            throw new IllegalArgumentException("Cannot update a non-existing user");
        }
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        return userRepository.save(user);
    }

    public List<User> getUsersByRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        List<User> users = userRepository.findByRole(role);
        if (users.isEmpty()) {
            return Collections.emptyList();
        }
        return users;
    }

    public List<User> getUsersByStatus(Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        List<User> users = userRepository.findByStatus(status);

        if (users.isEmpty()) {
            return Collections.emptyList();
        }
        return users;
    }

    public List<User> getUsersByAccountStatus(Account accountStatus) {
        if (accountStatus == null) {
            throw new IllegalArgumentException("Account status cannot be null");
        }
        List<User> users = userRepository.findByAccountStatus(accountStatus);
        if (users.isEmpty()) {
            return Collections.emptyList();
        }
        return users;
    }

    @Transactional
    public void deleteUserById(Long id) {
        /*
         * User currentUser = getCurrentUser();
         * if (!hasPermission(currentUser, Permission.DELETE_USER)) {
         * throw new
         * AccessDeniedException("You do not have permission to delete users.");
         * }
         */

        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("Cannot delete non-existing user with ID: " + id);
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public void deleteUserByUsername(String username) {
        /*
         * User currentUser = getCurrentUser();
         * if (!hasPermission(currentUser, Permission.DELETE_USER)) {
         * throw new
         * AccessDeniedException("You do not have permission to delete users.");
         * }
         */

        if (!userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Cannot delete non-existing user with username: " + username);
        }
        userRepository.deleteByUsername(username);
    }
}