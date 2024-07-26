package com.LessonLab.forum.Services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.LessonLab.forum.Models.Role;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Models.Enums.Account;
import com.LessonLab.forum.Models.Enums.Status;
import com.LessonLab.forum.Repositories.RoleRepository;
import com.LessonLab.forum.Repositories.UserRepository;
import com.LessonLab.forum.interfaces.UserServiceInterface;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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

    private final long EXPIRATION_TIME = 30 * 60 * 1000; // 30 minutes
    /*
     * @Value("${jwt.secret}")
     * private String SECRET;
     */
    private final String SECRET = "secret"; // use a more secure secret and place it in secure storage

    public UserDetails loadUserByUsername(String username) {
        // Retrieve user with the given username
        Optional<User> user = userRepository.findByUsername(username);
        // Check if user exists
        if (user == null) {
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        } else {
            log.info("User found in the database: {}", username);
            // Create a collection of SimpleGrantedAuthority objects from the user's roles
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            user.orElseThrow(() -> new UsernameNotFoundException("User not found in the database")).getRoles()
                    .forEach(role -> {
                        authorities.add(new SimpleGrantedAuthority(role.getName()));
                    });
            // Return the user details, including the username, password, and authorities
            return new org.springframework.security.core.userdetails.User(
                    user.orElseThrow(() -> new UsernameNotFoundException("User not found in the database"))
                            .getUsername(),
                    user.orElseThrow(() -> new UsernameNotFoundException("User not found in the database"))
                            .getPassword(),
                    authorities);
        }
    }

    public String createToken(String username) {

        UserDetails user = loadUserByUsername(username);
        Algorithm algorithm = Algorithm.HMAC256(SECRET.getBytes());

        String[] roles = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toArray(String[]::new);

        return JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .withIssuer("auth0")
                .withArrayClaim("roles", roles)
                .sign(algorithm);
    }

    public User registerUser(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setAccountStatus(Account.ACTIVE);
        user.setStatus(Status.ONLINE);

        user = userRepository.save(user);

        Role userRole = roleRepository.findByName("ROLE_USER");
        if (userRole == null) {
            userRole = new Role();
            userRole.setName("ROLE_USER");
            roleRepository.save(userRole);
        }

        Collection<Role> roles = new ArrayList<>();
        roles.add(userRole);
        user.setRoles(roles);

        user = userRepository.save(user);

        return user;
    }

    @Transactional
    public User updateUser(User user) {

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

        if (!userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Cannot delete non-existing user with username: " +
                    username);
        }
        userRepository.deleteByUsername(username);
    }

    /**
     * Saves a new user to the database
     *
     * @param user the user to be saved
     * @return the saved user
     */
    @Override
    public User saveUser(User user) {
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
        Optional<User> user = userRepository.findByUsername(username);
        Role role = roleRepository.findByName(roleName);
        if (user.isPresent() && role != null) {
            User userObject = user.get();
            userObject.getRoles().add(role);
            userRepository.save(userObject);
        }
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
        return userRepository.findByUsername(username).orElse(null);
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