package com.LessonLab.forum.Services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.LessonLab.forum.Models.UserExtension;
import com.LessonLab.forum.Models.Enums.Account;
import com.LessonLab.forum.Models.Enums.Permission;
import com.LessonLab.forum.Models.Enums.Status;
/* import com.LessonLab.forum.Repositories.RoleRepository;
import com.LessonLab.forum.Repositories.UserRepository; */
import com.LessonLab.forum.security.services.interfaces.UserServiceInterface;

import com.LessonLab.forum.security.models.Role;
import com.LessonLab.forum.security.models.User;

import com.LessonLab.forum.security.repositories.RoleRepository;
import com.LessonLab.forum.security.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@Slf4j
public class UserService implements UserServiceInterface, UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /*
     * // Utility method to check if a user has a specific permission
     * private boolean hasPermission(UserExtension user, Permission permission) {
     * return user.getRole().getPermissions().contains(permission);
     * }
     * 
     * public UserExtension getCurrentUser() {
     * String username = ((UserDetails)
     * SecurityContextHolder.getContext().getAuthentication().getPrincipal())
     * .getUsername();
     * return userRepository.findByUsername(username)
     * .orElseThrow(() -> new IllegalArgumentException("User not found"));
     * }
     */
    /*
     * public UserExtension getUser(Long id) {
     * 
     * User currentUser = getCurrentUser();
     * if (!hasPermission(currentUser, Permission.READ_USER)) {
     * throw new
     * AccessDeniedException("You do not have permission to read user data.");
     * }
     * 
     * 
     * if (id == null) {
     * throw new IllegalArgumentException("User ID cannot be null");
     * }
     * return userRepository.findById(id)
     * .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " +
     * id));
     * }
     */

    /*
     * @Transactional
     * public UserExtension addUser(String username) {
     * if (username == null || username.isEmpty()) {
     * throw new IllegalArgumentException("Username cannot be null or empty");
     * }
     * if (!username.matches("[A-Za-z0-9_]+")) {
     * throw new IllegalArgumentException("Username contains invalid characters");
     * }
     * if (userRepository.findByUsername(username).isPresent()) {
     * throw new IllegalArgumentException("Username already exists!");
     * }
     * 
     * UserExtension user = new UserExtension();
     * user.setUsername(username);
     * user.setStatus(Status.ONLINE); // Default status
     * user.setAccountStatus(Account.ACTIVE); // Default account status
     * user.setRole(Role.USER); // Consider having a default role or handling roles
     * appropriately.
     * 
     * return userRepository.save(user);
     * }
     */

    /*
     * @Transactional
     * public UserExtension updateUser(UserExtension user) {
     * 
     * User currentUser = getCurrentUser();
     * if (!hasPermission(currentUser, Permission.WRITE_USER)) {
     * throw new
     * AccessDeniedException("You do not have permission to update users.");
     * }
     * 
     * 
     * if (user == null) {
     * throw new IllegalArgumentException("Cannot update a null user");
     * }
     * if (user.getUserId() == null || !userRepository.existsById(user.getUserId()))
     * {
     * throw new IllegalArgumentException("Cannot update a non-existing user");
     * }
     * if (user.getUsername() == null || user.getUsername().isEmpty()) {
     * throw new IllegalArgumentException("Username cannot be null or empty");
     * }
     * return userRepository.save(user);
     * }
     * 
     * public List<User> getUsersByRole(Role role) {
     * if (role == null) {
     * throw new IllegalArgumentException("Role cannot be null");
     * }
     * List<User> users = roleRepository.findByName(role);
     * if (users.isEmpty()) {
     * return Collections.emptyList();
     * }
     * return users;
     * }
     * 
     * public List<UserExtension> getUsersByStatus(Status status) {
     * if (status == null) {
     * throw new IllegalArgumentException("Status cannot be null");
     * }
     * List<UserExtension> users = userRepository.findByStatus(status);
     * 
     * if (users.isEmpty()) {
     * return Collections.emptyList();
     * }
     * return users;
     * }
     * 
     * public List<UserExtension> getUsersByAccountStatus(Account accountStatus) {
     * if (accountStatus == null) {
     * throw new IllegalArgumentException("Account status cannot be null");
     * }
     * List<UserExtension> users =
     * userRepository.findByAccountStatus(accountStatus);
     * if (users.isEmpty()) {
     * return Collections.emptyList();
     * }
     * return users;
     * }
     */

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

    /*
     * @Transactional
     * public void deleteUserByUsername(String username) {
     * 
     * User currentUser = getCurrentUser();
     * if (!hasPermission(currentUser, Permission.DELETE_USER)) {
     * throw new
     * AccessDeniedException("You do not have permission to delete users.");
     * }
     * 
     * 
     * if (!userRepository.existsByUsername(username)) {
     * throw new
     * IllegalArgumentException("Cannot delete non-existing user with username: " +
     * username);
     * }
     * userRepository.deleteByUsername(username);
     * }
     */

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
        user.setPassword(passwordEncoder.encode(user.getPassword()));
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