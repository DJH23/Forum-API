package com.LessonLab.forum.ModelTests;

import com.LessonLab.forum.Models.Comment;
import com.LessonLab.forum.Models.Post;
import com.LessonLab.forum.Models.Thread;
import com.LessonLab.forum.Models.UserExtension;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class PostTest {

    @Test
    public void testGetAndSetThread() {
        // Arrange
        UserExtension user = new UserExtension();
        Thread thread = new Thread();
        Post post = new Post("content", user);

        // Act
        post.setThread(thread);

        // Assert
        assertEquals(thread, post.getThread());
        assertTrue(thread.getPosts().contains(post));
    }

    @Test
    public void testGetAndSetComments() {
        // Arrange
        UserExtension user = new UserExtension();
        Thread thread = new Thread();
        Post post = new Post("content", user, thread);
        Comment comment1 = new Comment("comment content 1", user);
        Comment comment2 = new Comment("comment content 2", user);
        List<Comment> comments = new ArrayList<>();
        comments.add(comment1);
        comments.add(comment2);

        // Act
        post.setComments(comments);

        // Assert
        assertEquals(comments, post.getComments());
    }

    @Test
    public void testAddComment() {
        // Arrange
        UserExtension user = new UserExtension();
        Thread thread = new Thread();
        Post post = new Post("content", user, thread);
        Comment comment = new Comment("comment content", user);

        // Act
        post.addComment(comment);

        // Assert
        assertTrue(post.getComments().contains(comment));
        assertEquals(post, comment.getPost());
    }

    @Test
    public void testRemoveComment() {
        // Arrange
        UserExtension user = new UserExtension();
        Thread thread = new Thread();
        Post post = new Post("content", user, thread);
        Comment comment = new Comment("comment content", user);
        post.addComment(comment);

        // Act
        post.removeComment(comment);

        // Assert
        assertFalse(post.getComments().contains(comment));
        assertNull(comment.getPost());
    }
}
