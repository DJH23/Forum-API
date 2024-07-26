package com.LessonLab.forum.ModelTests;

import com.LessonLab.forum.Models.Comment;
import com.LessonLab.forum.Models.Post;
import com.LessonLab.forum.Models.User;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommentTest {

    @Test
    public void testGetAndSetPost() {
        // Arrange
        User user = new User();
        Post post = new Post("content", user);
        Comment comment = new Comment("comment content", user);

        // Act
        comment.setPost(post);

        // Assert
        assertEquals(post, comment.getPost());
        assertTrue(post.getComments().contains(comment));
    }
}
