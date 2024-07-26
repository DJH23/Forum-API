package com.LessonLab.forum.ModelTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.LessonLab.forum.Models.Content;
import com.LessonLab.forum.Models.User;

public class ContentTest {

    @Test
    public void testGettersAndSetters() {
        // Arrange
        User user = new User();
        Content content = new Content() {
        }; // Create an anonymous subclass because Content is abstract
        Long contentId = 1L;
        String contentText = "content";
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

    @Test
    public void testAddToCollection() {
        // Arrange
        Content content = new Content() {
            // Create an anonymous subclass because Content is abstract
        };
        List<String> collection = new ArrayList<>(); // Declare and initialize the collection variable

        // Act
        content.addToCollection(collection, "Item1");
        content.addToCollection(collection, "Item1"); // Adding the same item again

        // Assert
        assertEquals(1, collection.size());
        assertTrue(collection.contains("Item1"));
    }

    @Test
    public void testRemoveFromCollection() {
        // Arrange
        List<String> collection = new ArrayList<>();
        collection.add("Item1");        
        Content content = new Content() {
            // Create an anonymous subclass because Content is abstract
        };

        // Act
        content.removeFromCollection(collection, "Item1");
        content.removeFromCollection(collection, "Item2"); // Removing an item not in the list

        // Assert
        assertTrue(collection.isEmpty());
    }

    @Test
    public void testPrePersistSetsCreatedAt() {
        // Arrange
        Content content = new Content() {
            // Create an anonymous subclass because Content is abstract
        };
        // Act
        content.prePersist(); // Simulate prePersist call

        // Assert
        assertTrue(content.getCreatedAt().isBefore(LocalDateTime.now()));
    }
}
