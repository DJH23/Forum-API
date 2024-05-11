package com.LessonLab.forum;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.LessonLab.forum.Models.User;
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
        User user = new User();
        user.setUsername("testUser");

        // Act
        User savedUser = userService.addUser(user);

        // Assert
        User retrievedUser = userRepository.findByUsername("testUser").orElse(null);
        assertEquals(savedUser.getUsername(), retrievedUser.getUsername());
    }
}
