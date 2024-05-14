package com.LessonLab.forum.Controllers;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.LessonLab.forum.Models.Role;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Models.UserExtension;
import com.LessonLab.forum.Models.Enums.Account;

import com.LessonLab.forum.Models.Enums.Status;
import com.LessonLab.forum.Repositories.RoleRepository;
import com.LessonLab.forum.Services.UserService;
import com.LessonLab.forum.dtos.RoleToUserDTO;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    private final long EXPIRATION_TIME = 10 * 60 * 1000; // 10 minutes
    private final String SECRET = "secret"; // use a more secure secret and place it in secure storage

    private UserDetailsService userDetailsService;

    private boolean isValidUser(String username, String password) {
        try {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
            authenticationManager.authenticate(authToken);
            return true;
        } catch (AuthenticationException e) {
            return false;
        }
    }

    private String createToken(String username) {
        UserDetails user = userDetailsService.loadUserByUsername(username);
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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, String password) {
        if (isValidUser(username, password)) {
            String token = createToken(username);
            return ResponseEntity.ok().header("Authorization", "Bearer " + token).body("Logged in Successfully");
        }
        return ResponseEntity.status(401).body("Invalid Credentials");
    }

    @PostMapping("/register-user")
    public ResponseEntity<User> registerUser(@RequestParam String username, @RequestParam String password) {
        User user = new User();

        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));

        // Create a UserExtension object
        UserExtension userExtension = new UserExtension();
        userExtension.setAccountStatus(Account.ACTIVE);
        userExtension.setStatus(Status.ONLINE);

        // Set the UserExtension to the user
        user.setUserExtension(userExtension);

        // Save the user to the database
        user = userService.saveUser(user);

        // Ensure the ROLE_USER exists and is added to the user
        Role userRole = roleRepository.findByName("ROLE_USER");
        if (userRole == null) {
            userRole = new Role();
            userRole.setName("ROLE_USER");
            userService.saveRole(userRole);
        }
        userService.addRoleToUser(username, "ROLE_USER");

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
    @PreAuthorize("")
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

    @PostMapping("/add-role-type")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveRole(@RequestBody Role role) {
        userService.saveRole(role);
    }

    @PostMapping("/roles/add-role-to-user")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addRoleToUser(@RequestBody RoleToUserDTO roleToUserDTO) {
        userService.addRoleToUser(roleToUserDTO.getUsername(), roleToUserDTO.getRoleName());
    }
}
