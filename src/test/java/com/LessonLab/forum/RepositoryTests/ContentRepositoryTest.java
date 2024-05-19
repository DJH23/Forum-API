package com.LessonLab.forum.RepositoryTests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.LessonLab.forum.Models.Content;
import com.LessonLab.forum.Models.Post;
import com.LessonLab.forum.Models.UserExtension;
import com.LessonLab.forum.Models.Thread;
import com.LessonLab.forum.Repositories.ContentRepository;
import com.LessonLab.forum.Repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ContentRepositoryTest {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private UserRepository userRepository;

    private UserExtension testUser;

    @BeforeEach
    public void setUp() {
        // Create a test user
        testUser = new UserExtension();
        testUser.setUsername("testUser");
        userRepository.save(testUser);

        // Create a test thread
        Thread testThread = new Thread();
        testThread.setTitle("Test Thread"); // Set the title, not the name
        contentRepository.save(testThread);

        // Create some test posts
        for (int i = 0; i < 5; i++) {
            Post content = new Post();
            content.setUser(testUser);
            content.setThread(testThread); // Set the thread
            content.setContent("test content " + i);
            content.setCreatedAt(LocalDateTime.now().minusHours(i));
            contentRepository.save(content);
        }
    }

    @Test
    public void testFindByCreatedAtBetween() {
        // Arrange
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();

        // Act
        List<Content> contents = contentRepository.findByCreatedAtBetween(start, end);

        // Assert
        assertFalse(contents.isEmpty());
        contents.forEach(content -> {
            assertTrue(content.getCreatedAt().isAfter(start));
            assertTrue(content.getCreatedAt().isBefore(end));
        });
    }

    @Test
    public void testFindByUserId() {
        // Arrange
        Long userId = testUser.getId();
        PageRequest pageable = PageRequest.of(0, 10);

        // Act
        Page<Content> contents = contentRepository.findByUserId(userId, pageable);

        // Assert
        assertFalse(contents.isEmpty());
        contents.forEach(content -> assertEquals(userId, content.getUser().getId()));
    }

    @Test
    public void testFindByContentContaining() {
        // Arrange
        String text = "test";

        // Act
        List<Content> contents = contentRepository.findByContentContaining(text);

        // Assert
        assertFalse(contents.isEmpty());
        contents.forEach(content -> assertTrue(content.getContent().contains(text)));
    }

    @AfterEach
    public void tearDown() {
        // Delete all test contents
        contentRepository.deleteAll();

        // Delete the test user
        userRepository.delete(testUser);
    }
}
