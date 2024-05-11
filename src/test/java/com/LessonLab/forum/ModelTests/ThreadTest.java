package com.LessonLab.forum.ModelTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.LessonLab.forum.Models.Post;
import com.LessonLab.forum.Models.Thread;

public class ThreadTest {
    
    @Test
    public void testGettersAndSetters() {
        // Arrange
        Thread thread = new Thread();
        String title = "title";
        String description = "description";
        List<Post> posts = new ArrayList<>();

        // Act
        thread.setTitle(title);
        thread.setDescription(description);
        thread.setPosts(posts);

        // Assert
        assertEquals(title, thread.getTitle());
        assertEquals(description, thread.getDescription());
        assertEquals(posts, thread.getPosts());
    }

    @Test
    public void testAddPost() {
        // Arrange
        Thread thread = new Thread();
        Post post = new Post();

        // Act
        thread.addPost(post);

        // Assert
        assertTrue(thread.getPosts().contains(post));
        assertEquals(thread, post.getThread());
    }

    @Test
    public void testRemovePost() {
        // Arrange
        Thread thread = new Thread();
        Post post = new Post();
        thread.addPost(post);
    
        // Act
        thread.removePost(post);
    
        // Assert
        assertFalse(thread.getPosts().contains(post));
        assertNull(post.getThread());
    }
}
