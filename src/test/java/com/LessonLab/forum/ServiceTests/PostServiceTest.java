package com.LessonLab.forum.ServiceTests;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.LessonLab.forum.Models.Comment;
import com.LessonLab.forum.Models.Content;
import com.LessonLab.forum.Models.Post;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Models.Thread;
import com.LessonLab.forum.Models.Enums.Role;
import com.LessonLab.forum.Repositories.CommentRepository;
import com.LessonLab.forum.Repositories.ContentRepository;
import com.LessonLab.forum.Repositories.PostRepository;
import com.LessonLab.forum.Repositories.UserRepository;
import com.LessonLab.forum.Repositories.VoteRepository;
import com.LessonLab.forum.Services.ConfigurationService;
import com.LessonLab.forum.Services.ContentService;
import com.LessonLab.forum.Services.NotificationService;
import com.LessonLab.forum.Services.PostService;
import com.LessonLab.forum.Services.ThreadService;

@RunWith(MockitoJUnitRunner.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private ThreadService threadService;
    @Mock
    private ContentService contentService;
    @Mock
    private ContentRepository contentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private VoteRepository voteRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private ConfigurationService configurationService;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private PostService postService;

    @Before
    public void setup() {
       
    }

    @Test
    public void testAddPost() {
        // Create a test user and post
        User user = new User("testUser", Role.USER);
        Thread thread = new Thread("Test thread title", "Test thread description");
        Post post = new Post("Test post", user);  
        post.setThread(thread); // Set the thread for the post
    
        // Mock the threadService to return the thread
        when(threadService.getThread(thread.getId())).thenReturn(thread); 
    
        // Mock the contentRepository to return the saved post
        when(contentRepository.save(post)).thenReturn(post);
    
        // Call the addPost method
        Post addedPost = postService.addPost(post, user);      
    
        // Assert that the post was added successfully
        assertNotNull(addedPost);
        assertEquals(post, addedPost);
    
        // Verify that the contentRepository was called with the correct arguments
        verify(contentRepository).save(post);
    }

    @Test
    public void testUpdatePost() {
        // Create a test user and post
        User user = new User("testUser", Role.USER);
        Thread thread = new Thread("Test thread title", "Test thread description");
        Post post = new Post("Test post", user);  
        post.setThread(thread); // Set the thread for the post

        // Mock the threadService to return the thread
        when(threadService.getThread(thread.getId())).thenReturn(thread); 

        // Mock the contentRepository to return the saved post
        when(contentRepository.save(post)).thenReturn(post);

        // Mock the contentRepository to return the post when findById is called
        when(contentRepository.findById(post.getId())).thenReturn(Optional.of(post));

        // Update the post content
        String newContent = "Updated post content";
        Post updatedPost = postService.updatePost(post.getId(), newContent, user);

        // Assert that the post was updated successfully
        assertNotNull(updatedPost);
        assertEquals(newContent, updatedPost.getContent());

        // Verify that the contentRepository was called with the correct arguments
        verify(contentRepository).save(updatedPost);
    }

    @Test
    public void testGetPost() {
        // Create a test user and post
        User user = new User("testUser", Role.USER);
        Thread thread = new Thread("Test thread title", "Test thread description");
        Post post = new Post("Test post", user);  
        post.setThread(thread); // Set the thread for the post

        // Mock the contentRepository to return the post when findById is called
        when(contentRepository.findById(post.getId())).thenReturn(Optional.of(post));

        // Call getPost
        Post retrievedPost = postService.getPost(post.getId());

        // Assert that the retrieved post is the same as the original post
        assertNotNull(retrievedPost);
        assertEquals(post, retrievedPost);
    }

    @Test
    public void testGetPostsByThread() {
        // Create a test user and thread
        User user = new User("testUser", Role.USER);
        Thread thread = new Thread("Test thread title", "Test thread description");
    
        // Create a list of posts and associate them with the thread
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Post post = new Post("Test post " + i, user);
            post.setThread(thread);
            posts.add(post);
        }
    
        // Mock the postRepository to return the posts when findByThread is called
        when(postRepository.findByThread(thread)).thenReturn(posts);
    
        // Call getPostsByThread
        List<Post> retrievedPosts = postService.getPostsByThread(thread);
    
        // Assert that the retrieved posts are the same as the original posts
        assertNotNull(retrievedPosts);
        assertEquals(posts, retrievedPosts);
    }

    @Test
    public void testGetPostsByCommentContent() {
        // Create a test user and thread
        User user = new User("testUser", Role.USER);
        Thread thread = new Thread("Test thread title", "Test thread description");
    
        // Create a list of posts and associate them with the thread
        List<Post> posts = new ArrayList<>();
        String commentContent = "Test comment content";
        for (int i = 0; i < 3; i++) {
            Post post = new Post("Test post " + i, user);
            post.setThread(thread);
            Comment comment = new Comment(commentContent, user);
            post.addComment(comment);
            posts.add(post);
        }
    
        // Mock the postRepository to return the posts when findByCommentContent is called
        when(postRepository.findByCommentContent(commentContent)).thenReturn(posts);
    
        // Call getPostsByCommentContent
        List<Post> retrievedPosts = postService.getPostsByCommentContent(commentContent);
    
        // Assert that the retrieved posts are the same as the original posts
        assertNotNull(retrievedPosts);
        assertEquals(posts, retrievedPosts);
    }

    @Test
    public void testGetPagedPostsByUserWithComments() {
        // Create a test user and thread
        User user = new User("testUser", Role.USER);
        Thread thread = new Thread("Test thread title", "Test thread description");

        // Create a list of contents and associate them with the user
        List<Content> contents = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Post post = new Post("Test post " + i, user);
            post.setThread(thread);

            // Create a comment and associate it with the post
            Comment comment = new Comment("Test comment " + i, user);
            comment.setPost(post);

            // Add the post to the contents list as a Content object
            contents.add((Content) post);
        }

        // Mock the getPagedContentByUser to return the contents when called with 1L and pageable
        when(postService.getPagedContentByUser(1L, PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"))))
            .thenReturn(new PageImpl<>(contents));

        // Call getPagedPostsByUser
        Page<Post> retrievedPosts = postService.getPagedPostsByUser(1L, PageRequest.of(0, 5));

        // Verify that the correct methods were called on the mock repositories
        verify(userRepository, times(1)).findById(1L);
        verify(contentRepository, times(1)).findByUserId(1L, PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt")));

        // After calling getPagedPostsByUser, verify that each post has the correct comment
        for (Post post : retrievedPosts.getContent()) {
            Comment comment = commentRepository.findByPost(post).get(0);
            assertEquals("Test comment " + post.getContent().charAt(post.getContent().length() - 1), comment.getContent());
        }
    }

    @Test
    public void testGetMostCommentedPosts() {
        // Create a test user and thread
        User user = new User("testUser", Role.USER);
        Thread thread = new Thread("Test thread title", "Test thread description");
    
        // Create a list of posts with varying numbers of comments
        List<Post> posts = new ArrayList<>();
        String commentContent = "Test comment content";
        for (int i = 0; i < 3; i++) {
            Post post = new Post("Test post " + i, user);
            post.setThread(thread);
            for (int j = 0; j <= i; j++) {  // Each post will have i comments
                Comment comment = new Comment(commentContent, user);
                post.addComment(comment);
            }
            posts.add(post);
        }
    
        // Create a Pageable
        Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "comments.size"));
    
        // Mock the postRepository to return the posts when findMostCommentedPosts is called
        when(postRepository.findMostCommentedPosts(pageable)).thenReturn(posts);
    
        // Call getMostCommentedPosts
        List<Post> retrievedPosts = postService.getMostCommentedPosts(pageable);
    
        // Assert that the retrieved posts are the same as the original posts
        assertNotNull(retrievedPosts);
        assertEquals(posts, retrievedPosts);
    }

    @Test
    public void testSearchPosts() {
        // Create a test user and thread
        User user = new User("testUser", Role.USER);
        Thread thread = new Thread("Test thread title", "Test thread description");
    
        // Create a list of posts with specific content
        List<Post> posts = new ArrayList<>();
        String searchText = "Test post";
        for (int i = 0; i < 3; i++) {
            Post post = new Post(searchText + " " + i, user);
            post.setThread(thread);
            posts.add(post);
        }
    
        // Mock the contentRepository to return the posts when findByContentContaining is called
        when(contentRepository.findByContentContaining(searchText)).thenReturn(posts.stream().map(post -> (Content) post).collect(Collectors.toList()));
    
        // Call searchPosts
        List<Post> retrievedPosts = postService.searchPosts(searchText);
    
        // Assert that the retrieved posts are the same as the original posts
        assertNotNull(retrievedPosts);
        assertEquals(posts, retrievedPosts);
    }

    @Test
    public void testGetPagedPostsByUser() {
        // Create a test user and thread
        User user = new User("testUser", Role.USER);
        Thread thread = new Thread("Test thread title", "Test thread description");
    
        // Create a list of posts and associate them with the user
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Post post = new Post("Test post " + i, user);
            post.setThread(thread);
            posts.add(post);
        }
    
        // Create a Pageable
        Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"));
    
        // Create a Page using the list of posts
        Page<Content> page = new PageImpl<>(new ArrayList<>(posts), pageable, posts.size());
    
        // Mock the contentRepository to return the page when findByUserId is called
        when(contentRepository.findByUserId(user.getId(), pageable)).thenReturn(page);
    
        // Call getPagedPostsByUser
        Page<Post> retrievedPosts = postService.getPagedPostsByUser(user.getId(), pageable);
    
        // Assert that the retrieved posts are the same as the original posts
        assertNotNull(retrievedPosts);
        assertEquals(posts.size(), retrievedPosts.getContent().size());
        assertTrue(retrievedPosts.getContent().containsAll(posts));
    }

    @Test
    public void testGetPostsByCreatedAtBetween() {
        // Create a test user and thread
        User user = new User("testUser", Role.USER);
        Thread thread = new Thread("Test thread title", "Test thread description");

        // Create a list of posts with createdAt timestamps between start and end
        List<Post> posts = new ArrayList<>();
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        for (int i = 0; i < 3; i++) {
            Post post = new Post("Test post " + i, user);
            post.setThread(thread);
            post.setCreatedAt(start.plusHours(i));  // Each post is created an hour after the previous one
            posts.add(post);
        }

        // Mock the contentRepository to return the posts when findByCreatedAtBetween is called
        when(contentRepository.findByCreatedAtBetween(start, end)).thenReturn(new ArrayList<Content>(posts));

        // Call getPostsByCreatedAtBetween
        List<Post> retrievedPosts = postService.getPostsByCreatedAtBetween(start, end);

        // Assert that the retrieved posts are the same as the original posts
        assertNotNull(retrievedPosts);
        assertEquals(posts, retrievedPosts);
    }

    @Test
    public void testGetPostsByContentContaining() {
        // Create a test user and thread
        User user = new User("testUser", Role.USER);
        Thread thread = new Thread("Test thread title", "Test thread description");

        // Create a list of posts with content containing a specific text
        List<Post> posts = new ArrayList<>();
        String searchText = "Test post";
        for (int i = 0; i < 3; i++) {
            Post post = new Post(searchText + " " + i, user);
            post.setThread(thread);
            posts.add(post);
        }

        // Mock the contentRepository to return the posts when findByContentContaining is called
        when(contentRepository.findByContentContaining(searchText)).thenReturn(new ArrayList<Content>(posts));

        // Call getPostsByContentContaining
        List<Post> retrievedPosts = postService.getPostsByContentContaining(searchText);

        // Assert that the retrieved posts are the same as the original posts
        assertNotNull(retrievedPosts);
        assertEquals(posts, retrievedPosts);
    }

    @Test
    public void testDeletePost() {
        // Create a test user and thread
        User user = new User("testUser", Role.USER);
        Thread thread = new Thread("Test thread title", "Test thread description");
    
        // Create a post
        Post post = new Post("Test post", user);
        post.setThread(thread);
    
        // Mock the contentRepository to return the post when findById is called
        when(contentRepository.findById(post.getId())).thenReturn(Optional.of((Content) post));
    
        // Mock the contentRepository to do nothing when delete is called
        doNothing().when(contentRepository).delete(post);
    
        // Call deletePost
        postService.deletePost(post.getId(), user);
    
        // Verify that delete was called on the contentRepository
        verify(contentRepository, times(1)).delete(post);
    }

    @Test
    public void testListPosts() {
        // Create user
        User user = new User("testUser", Role.USER);
    
        // Create posts
        Post post1 = new Post("Test post 1", user);
        Post post2 = new Post("Test post 2", user);
        Post post3 = new Post("Test post 3", user);
    
        // Save the posts
        when(contentRepository.save(post1)).thenReturn(post1);
        when(contentRepository.save(post2)).thenReturn(post2);
        when(contentRepository.save(post3)).thenReturn(post3);
    
        // Mock the contentRepository to return the posts when findAll is called
        when(contentRepository.findAll()).thenReturn(Arrays.asList(post1, post2, post3));
    
        // Call listPosts
        List<Post> returnedPosts = postService.listPosts();
    
        // Assert that the returned posts are the same as the original posts
        assertNotNull(returnedPosts);
        assertEquals(3, returnedPosts.size());
        assertTrue(returnedPosts.containsAll(Arrays.asList(post1, post2, post3)));
    
        // Verify that the findAll method was called
        verify(contentRepository, times(1)).findAll();
    }

    @Test
    public void testHandlePostVote() {
        // Create a user
        User user = new User("testUser", Role.USER);

        // Create a post
        Content post = new Post("Test post content", user);

        // Mock the userRepository to return the user when findById is called with 1L
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Mock the contentRepository to return the post when findById is called with 1L
        when(contentRepository.findById(1L)).thenReturn(Optional.of(post));

        // Mock the voteRepository to return Optional.empty() when findByUserAndContent is called
        when(voteRepository.findByUserAndContent(user, post)).thenReturn(Optional.empty());

        // Call handlePostVote
        postService.handlePostVote(1L, 1L, true);

        // Verify that the correct methods were called on the mock repositories
        verify(userRepository, times(1)).findById(1L);
        verify(contentRepository, times(1)).findById(1L);
        verify(voteRepository, times(1)).findByUserAndContent(user, post);
        verify(contentRepository, times(1)).save(any(Content.class));
    }
    
}
