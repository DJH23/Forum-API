package com.LessonLab.forum.RepositoryTests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import com.LessonLab.forum.Models.Post;
import com.LessonLab.forum.Models.Thread;
import com.LessonLab.forum.Models.Comment;
import com.LessonLab.forum.Repositories.CommentRepository;
import com.LessonLab.forum.Repositories.PostRepository;
import com.LessonLab.forum.Repositories.ThreadRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ThreadRepository threadRepository;

    @Autowired
    private CommentRepository contentRepository;

    private Post testPost;
    private Thread testThread;

    @BeforeEach
    public void setUp() {
        // Create a test thread
        testThread = new Thread();
        testThread.setTitle("Test Thread");
        threadRepository.save(testThread);
    
        // Create some test posts and comments
        for (int i = 0; i < 5; i++) {
            Post post = new Post();
            post.setThread(testThread);
            post.setContent("Test content " + i);
            postRepository.save(post);
    
            for (int j = 0; j < i; j++) {
                Comment comment = new Comment();
                comment.setContent("Test comment " + j);
                comment.setPost(post);
                contentRepository.save(comment);
            }
        }
    }

    @Test
    public void testFindByThread() {
        // Act
        List<Post> posts = postRepository.findByThread(testThread);

        // Assert
        assertFalse(posts.isEmpty());
        posts.forEach(post -> assertEquals(testThread, post.getThread()));
    }

    @Test
    public void testFindByCommentContent() {
        // Arrange
        Comment comment = new Comment();
        comment.setContent("Test comment");
        comment.setPost(testPost);
        contentRepository.save(comment);

        // Act
        List<Post> posts = postRepository.findByCommentContent("Test");

        // Assert
        assertFalse(posts.isEmpty());
        posts.forEach(post -> assertTrue(post.getComments().stream().anyMatch(c -> c.getContent().contains("Test"))));
    }

    @Test
    public void testFindMostCommentedPosts() {
        // Arrange
        PageRequest pageable = PageRequest.of(0, 10);

        // Act
        List<Post> posts = postRepository.findMostCommentedPosts(pageable);

        // Assert
        assertFalse(posts.isEmpty());
        // Check that the posts are ordered by comment count in descending order
        for (int i = 1; i < posts.size(); i++) {
            assertTrue(posts.get(i - 1).getComments().size() >= posts.get(i).getComments().size());
        }
    }

    @AfterEach
    public void tearDown() {
        // Delete the test post if it's not null
        if (testPost != null) {
            postRepository.delete(testPost);
        }

        // Delete the test thread if it's not null
        if (testThread != null) {
            threadRepository.delete(testThread);
        }
    }
}
