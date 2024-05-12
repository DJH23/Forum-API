package com.LessonLab.forum.RepositoryTests;

import com.LessonLab.forum.Models.Role;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Models.UserExtension;
import com.LessonLab.forum.Models.Enums.Account;
import com.LessonLab.forum.Models.Enums.Status;
import com.LessonLab.forum.Repositories.RoleRepository;
import com.LessonLab.forum.Repositories.UserExtensionRepository;
import com.LessonLab.forum.Repositories.UserRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserExtensionRepository userExtensionRepository;

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
        User user = userRepository.findByUsername("testUser");
        if (user != null) {
            userRepository.delete(user);
        }

        Role role = roleRepository.findByName("ADMIN");
        if (role != null) {
            roleRepository.delete(role);
        }
    }

    @Test
    public void testFindByUsername() {

        // Act
        User foundUser = userRepository.findByUsername("testUser");

        // Assert
        assertNotNull(foundUser);
        assertEquals("testUser", foundUser.getUsername());
    }

    @Test
    public void testFindByRole() {

        // Act
        List<Role> role = roleRepository.findByRole("ADMIN");

        // Assert
        assertNotNull(role);
        assertEquals("ADMIN", role.get(0).getName());
    }

    @Test
    public void testFindByStatus() {
        // Arrange
        UserExtension user = new UserExtension();
        user.setStatus(Status.OFFLINE);
        userRepository.save(user);

        // Act
        List<UserExtension> users = userExtensionRepository.findByStatus(Status.OFFLINE);

        // Assert
        assertEquals(1, users.size());
        assertEquals(Status.OFFLINE, users.get(0).getStatus());
    }

    @Test
    public void testFindByAccountStatus() {
        // Arrange
        UserExtension user = new UserExtension();
        user.setAccountStatus(Account.ACTIVE);
        userRepository.save(user);

        // Act
        List<UserExtension> users = userExtensionRepository.findByAccountStatus(Account.ACTIVE);

        // Assert
        assertEquals(1, users.size());
        assertEquals(Account.ACTIVE, users.get(0).getAccountStatus());
    }

    @Test
    public void testFindByRoleIn() {

        // Act
        List<Role> testRole = roleRepository.findByRole("ADMIN");

        // Assert
        assertEquals(1, testRole.size());
        assertEquals("ADMIN", testRole.get(0).getName());
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
        User foundUser = userRepository.findByUsername("testUser");

        // Assert
        assertNull(foundUser);
       
    }

}
