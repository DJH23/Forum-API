package com.LessonLab.forum.ServiceTests;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.LessonLab.forum.Models.Content;
import com.LessonLab.forum.Models.Post;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Models.Enums.Role;
import com.LessonLab.forum.Repositories.CommentRepository;
import com.LessonLab.forum.Repositories.ContentRepository;
import com.LessonLab.forum.Repositories.PostRepository;
import com.LessonLab.forum.Repositories.UserRepository;
import com.LessonLab.forum.Repositories.VoteRepository;
import com.LessonLab.forum.Services.ConfigurationService;
import com.LessonLab.forum.Services.NotificationService;
import com.LessonLab.forum.Services.PostService;

@RunWith(MockitoJUnitRunner.class)
public class ContentServiceTest {

    @Mock
    private ContentRepository contentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private VoteRepository voteRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private PostRepository postRepository;
    @Mock
    private ConfigurationService configurationService;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private PostService postService;

    @Before
    public void setup() {
        // Initialize the contentService with the mock dependencies
        postService = new PostService() {
            // Provide dummy implementations for the abstract methods
            @Override
            protected void checkRole(User user, Role... roles) {
                // Do nothing, we're not testing role checks here
            }

            @Override
            protected boolean hasPermissionToDelete(Content content, User user) {
                return true; // Allow deletion of any content
            }
        };
    }

    @Test
    public void testAddContent() {
        // Create a test user and post
        User user = new User("testUser", Role.USER);
        Post post = new Post("Test post", user);

        // Mock the contentRepository to return the saved post
        when(postRepository.save(post)).thenReturn(post);

        // Call the addContent method
        Post addedContent = postService.addPost(post, user);

        // Assert that the post was added successfully
        assertNotNull(addedContent);
        assertEquals(post, addedContent);

        // Verify that the contentRepository was called with the correct arguments
        verify(postRepository).save(post);
    }

    @Test
    public void testUpdateContent() {
        // Create a test user and content
        User user = new User("testUser", Role.USER);
        Content post = new Post("Test post content", user);
        post = contentRepository.save(post); // Save the content to the repository

        // Update the content
        String newContent = "Updated test content";

        // Mock the contentRepository to return the updated content
        when(contentRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(contentRepository.save(post)).thenReturn(post);

        // Call the updateContent method
        Content updatedPostContent = postService.updateContent(post.getId(), newContent, user);

        // Assert that the content was updated successfully
        assertNotNull(updatedPostContent);
        assertEquals(newContent, updatedPostContent.getContent());

        // Verify that the contentRepository was called with the correct arguments
        verify(contentRepository).findById(post.getId());
        verify(contentRepository).save(post);
    }

    @Test
    public void testGetContent() {
        // Create a test content
        Content post = new Post("Test post content", new User("testUser", Role.USER));

        // Mock the contentRepository to return the content
        when(contentRepository.findById(1L)).thenReturn(Optional.of(post));

        // Call the getContent method
        Content retrievedContent = postService.getContent(1L);

        // Assert that the content was retrieved successfully
        assertNotNull(retrievedContent);
        assertEquals(post, retrievedContent);

        // Verify that the contentRepository was called with the correct arguments
        verify(contentRepository).findById(1L);
    }

}