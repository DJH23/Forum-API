package com.LessonLab.forum.RepositoryTests;

import com.LessonLab.forum.Models.Role;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Models.Enums.Account;
import com.LessonLab.forum.Models.Enums.Status;
import com.LessonLab.forum.Repositories.RoleRepository;
import com.LessonLab.forum.Repositories.UserRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User testUser;

    private Role testRole;

    @BeforeEach
    public void setUp() {
        // Prepare the database for testing
        // This might involve creating test data
        testUser = new User();
        testUser.setUsername("testUser");
        userRepository.save(testUser);

        testRole = new Role();
        testRole.setName("ADMIN");
        roleRepository.save(testRole);

    }

    @AfterEach
    public void tearDown() {
        // Clean up the database after testing
        // This might involve deleting test data
        Optional<User> user = userRepository.findByUsername("testUser");
        user.ifPresent(u -> userRepository.delete(u));

        Role role = roleRepository.findByName("ADMIN");
        if (role != null) {
            roleRepository.delete(role);
        }
    }

    @Test
    public void testFindByUsername() {

        // Act
        Optional<User> foundUser = userRepository.findByUsername("testUser");

        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals("testUser", foundUser.get().getUsername());
    }

    @Test
    public void testFindUsersByRole() {
        // Arrange
        Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        assertNotNull(adminRole);

        // Act
        List<User> users = roleRepository.findUsersByRolesIn(Collections.singleton(adminRole));

        // Assert
        assertNotNull(users);
        assertTrue(users.size() > 0);
        for (User user : users) {
            assertTrue(user.getRoles().contains(adminRole));
        }
    }

    @Test
    public void testFindByStatus() {
        // Arrange
        User user = new User();
        user.setStatus(Status.OFFLINE);
        userRepository.save(user);

        // Act
        List<User> users = userRepository.findByStatus(Status.OFFLINE);

        // Assert
        assertEquals(1, users.size());
        assertEquals(Status.OFFLINE, users.get(0).getStatus());
    }

    @Test
    public void testFindByAccountStatus() {
        // Arrange
        int initialActiveUserCount = userRepository.findByAccountStatus(Account.ACTIVE).size();

        User user = new User();
        user.setUsername("testActiveUser"); // Add a unique username
        user.setAccountStatus(Account.ACTIVE);
        userRepository.save(user);

        // Act
        List<User> users = userRepository.findByAccountStatus(Account.ACTIVE);

        // Assert
        assertEquals(initialActiveUserCount + 1, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getUsername().equals("testActiveUser")));
        assertEquals(Account.ACTIVE, users.stream().filter(u -> u.getUsername().equals("testActiveUser")).findFirst()
                .get().getAccountStatus());
    }

    @Test
    public void testDeleteUserById() {
        // Arrange
        Long userId = testUser.getId();

        // Act
        userRepository.deleteById(userId);
        Optional<User> foundUser = userRepository.findById(userId);

        // Assert
        assertTrue(foundUser.isEmpty());
    }

    @Test
    public void testDeleteByUsername() {
        // Act
        userRepository.deleteByUsername("testUser");
        Optional<User> foundUser = userRepository.findByUsername("testUser");
    
        // Assert
        assertTrue(foundUser.isEmpty());
    }

}
