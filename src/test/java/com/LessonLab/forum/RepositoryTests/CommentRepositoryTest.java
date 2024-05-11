package com.LessonLab.forum.RepositoryTests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import com.LessonLab.forum.Models.Comment;
import com.LessonLab.forum.Models.Post;
import com.LessonLab.forum.Models.Thread;
import com.LessonLab.forum.Models.UserExtension;
import com.LessonLab.forum.Repositories.CommentRepository;
import com.LessonLab.forum.Repositories.ContentRepository;
import com.LessonLab.forum.Repositories.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CommentRepositoryTest {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    private Comment testComment;
    private Post testPost;
    private UserExtension testUser;
    private Thread testThread;

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

        // Create a test post
        testPost = new Post();
        testPost.setContent("Test post");
        testPost.setThread(testThread);
        contentRepository.save(testPost);

        // Create a test comment
        testComment = new Comment();
        testComment.setPost(testPost);
        testComment.setUser(testUser);
        testComment.setContent("Test comment");
        contentRepository.save(testComment);
    }

    @Test
    public void testFindByPost() {
        // Act
        List<Comment> comments = commentRepository.findByPost(testPost);

        // Assert
        assertFalse(comments.isEmpty());
        comments.forEach(comment -> assertEquals(testPost, comment.getPost()));
    }

    @Test
    public void testFindRecentComments() {
        // Arrange
        PageRequest pageable = PageRequest.of(0, 10);

        // Act
        List<Comment> comments = commentRepository.findRecentComments(pageable);

        // Assert
        assertFalse(comments.isEmpty());
        // Check that the comments are ordered by creation date in descending order
        for (int i = 1; i < comments.size(); i++) {
            assertTrue(comments.get(i - 1).getCreatedAt().isAfter(comments.get(i).getCreatedAt()));
        }
    }

    @Test
    public void testCountByPostAndUserNot() {
        // Act
        long count = commentRepository.countByPostAndUserNot(testPost, testUser);

        // Assert
        assertEquals(0, count);
    }

    @AfterEach
    public void tearDown() {
        // Delete the test comment
        if (testComment != null) {
            contentRepository.delete(testComment);
        }

        // Delete the test post
        if (testPost != null) {
            contentRepository.delete(testPost);
        }

        // Delete the test thread
        if (testThread != null) {
            contentRepository.delete(testThread);
        }

        // Delete the test user
        if (testUser != null) {
            userRepository.delete(testUser);
        }
    }
}
