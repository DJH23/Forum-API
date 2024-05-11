package com.LessonLab.forum;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.LessonLab.forum.Models.UserExtension;
import com.LessonLab.forum.Repositories.UserRepository;
import com.LessonLab.forum.Services.UserService;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testAddUser() {
        // Arrange
        UserExtension user = new UserExtension();
        user.setUsername("testUser");

        // Act
        UserExtension savedUser = userService.addUser(user);

        // Assert
        UserExtension retrievedUser = userRepository.findByUsername("testUser").orElse(null);
        assertEquals(savedUser.getUsername(), retrievedUser.getUsername());
    }
}
