package com.LessonLab.forum.RepositoryTests;

import com.LessonLab.forum.Models.UserExtension;
import com.LessonLab.forum.Models.Enums.Account;
import com.LessonLab.forum.Models.Enums.Role;
import com.LessonLab.forum.Models.Enums.Status;
import com.LessonLab.forum.Repositories.UserRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private UserExtension testUser;

    @BeforeEach
    public void setUp() {
        // Prepare the database for testing
        // This might involve creating test data
        testUser = new UserExtension();
        testUser.setUsername("testUser");
        userRepository.save(testUser);
    }

    @AfterEach
    public void tearDown() {
        // Clean up the database after testing
        // This might involve deleting test data
        userRepository.findByUsername("testUser").ifPresent(userRepository::delete);
    }

    @Test
    public void testFindByUsername() {
        // Arrange
        UserExtension user = new UserExtension();
        user.setUsername("testUser");
        userRepository.save(user);

        // Act
        Optional<UserExtension> foundUser = userRepository.findByUsername("testUser");

        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals("testUser", foundUser.get().getUsername());
    }

    @Test
    public void testFindByRole() {
        // Arrange
        UserExtension user = new UserExtension();
        user.setRole(Role.ADMIN);
        userRepository.save(user);

        // Act
        List<UserExtension> users = userRepository.findByRole(Role.ADMIN);

        // Assert
        assertFalse(users.isEmpty());
        assertEquals(Role.ADMIN, users.get(0).getRole());
    }

    @Test
    public void testFindByStatus() {
        // Arrange
        UserExtension user = new UserExtension();
        user.setStatus(Status.OFFLINE);
        userRepository.save(user);

        // Act
        List<UserExtension> users = userRepository.findByStatus(Status.OFFLINE);

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
        List<UserExtension> users = userRepository.findByAccountStatus(Account.ACTIVE);

        // Assert
        assertEquals(1, users.size());
        assertEquals(Account.ACTIVE, users.get(0).getAccountStatus());
    }

    @Test
    public void testFindByRoleIn() {
        // Arrange
        UserExtension user = new UserExtension();
        user.setRole(Role.USER);
        userRepository.save(user);

        // Act
        List<UserExtension> users = userRepository.findByRole(Role.USER);

        // Assert
        assertEquals(1, users.size());
        assertEquals(Role.USER, users.get(0).getRole());
    }

    @Test
    public void testDeleteUserById() {
        // Arrange
        UserExtension user = new UserExtension();
        user.setUsername("testUser");
        userRepository.save(user);
        Long userId = user.getUserId();

        // Act
        userRepository.deleteById(userId);
        Optional<UserExtension> foundUser = userRepository.findById(userId);

        // Assert
        assertTrue(foundUser.isEmpty());
    }

    @Test
    public void testDeleteByUsername() {
        // Arrange
        UserExtension user = new UserExtension();
        user.setUsername("testUser");
        userRepository.save(user);

        // Act
        userRepository.deleteByUsername("testUser");
        Optional<UserExtension> foundUser = userRepository.findByUsername("testUser");

        // Assert
        assertTrue(foundUser.isEmpty());
    }

}
