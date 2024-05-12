package com.LessonLab.forum.Services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.LessonLab.forum.Models.Role;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Models.UserExtension;
import com.LessonLab.forum.Models.Enums.Account;
import com.LessonLab.forum.Models.Enums.Status;
import com.LessonLab.forum.Repositories.RoleRepository;
import com.LessonLab.forum.Repositories.UserExtensionRepository;
import com.LessonLab.forum.Repositories.UserRepository;
import com.LessonLab.forum.interfaces.UserServiceInterface;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

@Service
@Slf4j
public class UserService implements UserServiceInterface, UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserExtensionRepository userExtensionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
        if (user.getId() == null || !userRepository.existsById(user.getId())) {
            throw new IllegalArgumentException("Cannot update a non-existing user");
        }
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        return userRepository.save(user);
    }

    public List<Role> getUsersByRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        List<Role> users = roleRepository.findByRole(role.getName());
        if (users.isEmpty()) {
            return Collections.emptyList();
        }
        return users;
    }

    public List<UserExtension> getUsersByStatus(Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        List<UserExtension> users = userExtensionRepository.findByStatus(status);

        if (users.isEmpty()) {
            return Collections.emptyList();
        }
        return users;
    }

    public List<UserExtension> getUsersByAccountStatus(Account accountStatus) {
        if (accountStatus == null) {
            throw new IllegalArgumentException("Account status cannot be null");
        }
        List<UserExtension> users = userExtensionRepository.findByAccountStatus(accountStatus);
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
            throw new IllegalArgumentException("Cannot delete non-existing user with username: " +
                    username);
        }
        userRepository.deleteByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Retrieve user with the given username
        User user = userRepository.findByUsername(username);
        // Check if user exists
        if (user == null) {
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        } else {
            log.info("User found in the database: {}", username);
            // Create a collection of SimpleGrantedAuthority objects from the user's roles
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            user.getRoles().forEach(role -> {
                authorities.add(new SimpleGrantedAuthority(role.getName()));
            });
            // Return the user details, including the username, password, and authorities
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                    authorities);
        }
    }

    /**
     * Saves a new user to the database
     *
     * @param user the user to be saved
     * @return the saved user
     */
    @Override
    public User saveUser(User user) {
        log.info("Saving new user {} to the database", user.getName());
        // Encode the user's password for security before saving
       // user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        return userRepository.save(user);
    }

    /**
     * Saves a new role to the database
     *
     * @param role the role to be saved
     * @return the saved role
     */

    @Override
    public Role saveRole(Role role) {
        log.info("Saving new role {} to the database", role.getName());
        return roleRepository.save(role);
    }

    /**
     * Adds a role to the user with the given username
     *
     * @param username the username of the user to add the role to
     * @param roleName the name of the role to be added
     */
    @Override
    public void addRoleToUser(String username, String roleName) {
        log.info("Adding role {} to user {}", roleName, username);

        // Retrieve the user and role objects from the repository
        User user = userRepository.findByUsername(username);
        Role role = roleRepository.findByName(roleName);

        // Add the role to the user's role collection
        user.getRoles().add(role);

        // Save the user to persist the changes
        userRepository.save(user);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return (User) authentication.getPrincipal();
    }

    /**
     * Retrieves the user with the given username
     *
     * @param username the username to search for
     * @return the user with the given username
     */
    @Override
    public User getUser(String username) {
        log.info("Fetching user {}", username);
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserById(Long id) {
        log.info("Fetching user with id {}", id);
        return userRepository.findById(id);
    }

    /**
     * Retrieves all users from the database
     *
     * @return a list of all users
     */
    @Override
    public List<User> getUsers() {
        log.info("Fetching all users");
        return userRepository.findAll();
    }

}