package com.LessonLab.forum.Services;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
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

    private User getCurrentUser() {
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        return userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Transactional
    public User addUser(User user) {
        User currentUser = getCurrentUser();
        if (!hasPermission(currentUser, Permission.WRITE_USER)) {
            throw new AccessDeniedException("You do not have permission to add users.");
        }

        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("Username already exists!");
        }
        return userRepository.save(user);
    }

    public User getUser(Long id) {
        User currentUser = getCurrentUser();
        if (!hasPermission(currentUser, Permission.READ_USER)) {
            throw new AccessDeniedException("You do not have permission to read user data.");
        }

        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
    }

    @Transactional
    public User updateUser(User user) {
        User currentUser = getCurrentUser();
        if (!hasPermission(currentUser, Permission.WRITE_USER)) {
            throw new AccessDeniedException("You do not have permission to update users.");
        }

        if (user == null) {
            throw new IllegalArgumentException("Cannot update a null user");
        }
        if (user.getId() == null || !userRepository.existsById(user.getId())) {
            throw new IllegalArgumentException("Cannot update a non-existing user");
        }
        return userRepository.save(user);
    }

    public List<User> getUsersByRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        List<User> users = userRepository.findByRole(role);
        if (users.isEmpty()) {
            return Collections.emptyList();  // Consider your use case: throw exception or return empty list
        }
        return users;
    }

    public List<User> getUsersByStatus(Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        List<User> users = userRepository.findByStatus(status);
        if (users.isEmpty()) {
            return Collections.emptyList();  // Adjust based on expected application behavior
        }
        return users;
    }

    public List<User> getUsersByAccountStatus(Account accountStatus) {
        if (accountStatus == null) {
            throw new IllegalArgumentException("Account status cannot be null");
        }
        List<User> users = userRepository.findByAccountStatus(accountStatus);
        if (users.isEmpty()) {
            return Collections.emptyList();  // Adjust based on expected application behavior
        }
        return users;
    }

    @Transactional
    public void deleteUser(Long id) {
        User currentUser = getCurrentUser();
        if (!hasPermission(currentUser, Permission.DELETE_USER)) {
            throw new AccessDeniedException("You do not have permission to delete users.");
        }

        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if(!userRepository.existsById(id)) {
            throw new IllegalArgumentException("Cannot delete non-existing user with ID: " + id);
        }
        userRepository.deleteById(id);
    }
}