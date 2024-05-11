package com.LessonLab.forum.ModelTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;

import org.junit.Test;

import com.LessonLab.forum.Models.Content;
import com.LessonLab.forum.Models.UserExtension;
import com.LessonLab.forum.Models.Enums.Role;

public class ContentTest {

    @Test
    public void testGettersAndSetters() {
        // Arrange
        Content content = new Content() {
        }; // Create an anonymous subclass because Content is abstract
        Long contentId = 1L;
        String contentText = "content";
        UserExtension user = new UserExtension("username", Role.USER);
        int upvotes = 5;
        int downvotes = 3;
        LocalDateTime createdAt = LocalDateTime.now();

        // Act
        content.setContentId(contentId);
        content.setContent(contentText);
        content.setUser(user);
        content.setUpvotes(upvotes);
        content.setDownvotes(downvotes);
        content.setCreatedAt(createdAt);

        // Assert
        assertEquals(contentId, content.getContentId());
        assertEquals(contentText, content.getContent());
        assertEquals(user, content.getUser());
        assertEquals(upvotes, content.getUpvotes());
        assertEquals(downvotes, content.getDownvotes());
        assertEquals(createdAt, content.getCreatedAt());
    }

    @Test
    public void testUpVote() {
        // Arrange
        Content content = new Content() {
        }; // Create an anonymous subclass because Content is abstract

        // Act
        content.upVote();

        // Assert
        assertEquals(1, content.getUpvotes());
    }

    @Test
    public void testDownVote() {
        // Arrange
        Content content = new Content() {
        }; // Create an anonymous subclass because Content is abstract

        // Act
        content.downVote();

        // Assert
        assertEquals(1, content.getDownvotes());
    }

    @Test
    public void testCheckThreshold() {
        // Arrange
        Content content = new Content() {
        }; // Create an anonymous subclass because Content is abstract
        content.setDownvotes(5);

        // Act
        boolean result = content.checkThreshold(3);

        // Assert
        assertTrue(result);
    }
}
