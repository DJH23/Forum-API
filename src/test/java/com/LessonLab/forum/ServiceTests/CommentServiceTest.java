package com.LessonLab.forum.ServiceTests;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.LessonLab.forum.Models.Comment;
import com.LessonLab.forum.Models.Content;
import com.LessonLab.forum.Models.Post;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Models.Enums.Role;
import com.LessonLab.forum.Repositories.CommentRepository;
import com.LessonLab.forum.Repositories.ContentRepository;
import com.LessonLab.forum.Repositories.UserRepository;
import com.LessonLab.forum.Repositories.VoteRepository;
import com.LessonLab.forum.Services.CommentService;
import com.LessonLab.forum.Services.ContentService;


public class CommentServiceTest {

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private VoteRepository voteRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ContentService contentService;

    @InjectMocks
    private CommentService commentService;

    @Before // or @BeforeEach for JUnit 5
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Proper initialization of mocks
    }

    @Test
    public void testAddComment() {
        // Create user
        User user = new User("testUser", Role.USER);

        // Create a post
        Post post = new Post("Test post", user);
        post.setUser(user);

        // Create a comment
        Comment comment = new Comment("Test comment", user);
        comment.setPost(post);

        // Mock the contentRepository to return the comment when save is called
        when(contentRepository.save(comment)).thenReturn(comment);

        // Call addComment
        Comment returnedComment = commentService.addComment(comment, user);

        // Assert that the returned comment is the same as the original comment
        assertNotNull(returnedComment);
        assertEquals(comment, returnedComment);

        // Verify that the save method was called
        verify(contentRepository, times(1)).save(comment);
    }

    @Test
    public void testUpdateComment() {
        // Create user
        User user = new User("testUser", Role.USER);
    
        // Create a post
        Post post = new Post("Test post", user);
        post.setUser(user);
    
        // Create a comment
        Comment comment = new Comment("Test comment", user);
        comment.setPost(post);
    
        // Save the comment
        when(contentRepository.save(comment)).thenReturn(comment);
    
        // Update the comment
        String newContent = "Updated comment";
        when(contentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        Comment updatedComment = commentService.updateComment(comment.getId(), newContent, user);
    
        // Assert that the returned comment's content is the updated content
        assertNotNull(updatedComment);
        assertEquals(newContent, updatedComment.getContent());
    
        // Verify that the save method was called
        verify(contentRepository, times(1)).save(comment);
    }

    @Test
    public void testGetComment() {
        // Create user
        User user = new User("testUser", Role.USER);

        // Create a post
        Post post = new Post("Test post", user);
        post.setUser(user);

        // Create a comment
        Comment comment = new Comment("Test comment", user);
        comment.setPost(post);

        // Save the comment
        when(contentRepository.save(comment)).thenReturn(comment);

        // Mock the contentRepository to return the comment when findById is called
        when(contentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        // Call getComment
        Comment returnedComment = commentService.getComment(comment.getId());

        // Assert that the returned comment is the same as the original comment
        assertNotNull(returnedComment);
        assertEquals(comment, returnedComment);

        // Verify that the findById method was called
        verify(contentRepository, times(1)).findById(comment.getId());
    }

    @Test
    public void testSearchComments() {
        // Create user
        User user = new User("testUser", Role.USER);
    
        // Create a post
        Post post = new Post("Test post", user);
        post.setUser(user);
    
        // Create comments
        Comment comment1 = new Comment("Test comment 1", user);
        Comment comment2 = new Comment("Test comment 2", user);
        Comment comment3 = new Comment("Test comment 3", user);
    
        // Save the comments
        when(contentRepository.save(comment1)).thenReturn(comment1);
        when(contentRepository.save(comment2)).thenReturn(comment2);
        when(contentRepository.save(comment3)).thenReturn(comment3);
    
        // Mock the contentRepository to return the comments when searchContent is called
        String searchText = "Test comment";
        when(contentRepository.findByContentContaining(searchText)).thenReturn(Arrays.asList(comment1, comment2, comment3));
        
        // Call searchComments
        List<Comment> returnedComments = commentService.searchComments(searchText);
        
        // Assert that the returned comments are the same as the original comments
        assertNotNull(returnedComments);
        assertEquals(3, returnedComments.size());
        assertTrue(returnedComments.containsAll(Arrays.asList(comment1, comment2, comment3)));
        
        // Verify that the searchContent method was called
        verify(contentRepository, times(1)).findByContentContaining(searchText);
    }

    @Test
    public void testGetPagedCommentsByUser() {
        // Create user
        User user = new User("testUser", Role.USER);
    
        // Create a post
        Post post = new Post("Test post", user);
        post.setUser(user);
    
        // Create comments
        Comment comment1 = new Comment("Test comment 1", user);
        Comment comment2 = new Comment("Test comment 2", user);
        Comment comment3 = new Comment("Test comment 3", user);
    
        // Save the comments
        when(contentRepository.save(comment1)).thenReturn(comment1);
        when(contentRepository.save(comment2)).thenReturn(comment2);
        when(contentRepository.save(comment3)).thenReturn(comment3);
    
        // Create a Page of comments
        Pageable pageable = PageRequest.of(0, 3);
        Page<Content> contentPage = new PageImpl<>(Arrays.asList(comment1, comment2, comment3), pageable, 3);
    

        // Mock the contentRepository to return the Page of comments when findByUserId is called
        when(contentRepository.findByUserId(user.getId(), pageable)).thenReturn(contentPage);   
    
        // Call getPagedCommentsByUser
        Page<Comment> returnedComments = commentService.getPagedCommentsByUser(user.getId(), pageable);
    
        // Assert that the returned comments are the same as the original comments
        assertNotNull(returnedComments);
        assertEquals(3, returnedComments.getTotalElements());
        assertTrue(returnedComments.getContent().containsAll(Arrays.asList(comment1, comment2, comment3)));
    
        // Verify that the getPagedContentByUser method was called
        verify(contentRepository, times(1)).findByUserId(user.getId(), pageable);
    }

    @Test
    public void testGetCommentsByCreatedAtBetween() {
        // Create user
        User user = new User("testUser", Role.USER);
    
        // Create a post
        Post post = new Post("Test post", user);
        post.setUser(user);
    
        // Create comments
        Comment comment1 = new Comment("Test comment 1", user);
        Comment comment2 = new Comment("Test comment 2", user);
        Comment comment3 = new Comment("Test comment 3", user);
    
        // Save the comments
        when(contentRepository.save(comment1)).thenReturn(comment1);
        when(contentRepository.save(comment2)).thenReturn(comment2);
        when(contentRepository.save(comment3)).thenReturn(comment3);
    
        // Mock the contentRepository to return the comments when findByCreatedAtBetween is called
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        when(contentRepository.findByCreatedAtBetween(start, end)).thenReturn(Arrays.asList(comment1, comment2, comment3));
    
        // Call getCommentsByCreatedAtBetween
        List<Comment> returnedComments = commentService.getCommentsByCreatedAtBetween(start, end);
    
        // Assert that the returned comments are the same as the original comments
        assertNotNull(returnedComments);
        assertEquals(3, returnedComments.size());
        assertTrue(returnedComments.containsAll(Arrays.asList(comment1, comment2, comment3)));
    
        // Verify that the findByCreatedAtBetween method was called
        verify(contentRepository, times(1)).findByCreatedAtBetween(start, end);
    }

    @Test
    public void testGetCommentsByContentContaining() {
        // Create user
        User user = new User("testUser", Role.USER);
    
        // Create a post
        Post post = new Post("Test post", user);
        post.setUser(user);
    
        // Create comments
        Comment comment1 = new Comment("Test comment 1", user);
        Comment comment2 = new Comment("Test comment 2", user);
        Comment comment3 = new Comment("Test comment 3", user);
    
        // Save the comments
        when(contentRepository.save(comment1)).thenReturn(comment1);
        when(contentRepository.save(comment2)).thenReturn(comment2);
        when(contentRepository.save(comment3)).thenReturn(comment3);
    
        // Mock the contentRepository to return the comments when findByContentContaining is called
        String text = "Test comment";
        when(contentRepository.findByContentContaining(text)).thenReturn(Arrays.asList(comment1, comment2, comment3));
    
        // Call getCommentsByContentContaining
        List<Comment> returnedComments = commentService.getCommentsByContentContaining(text);
    
        // Assert that the returned comments are the same as the original comments
        assertNotNull(returnedComments);
        assertEquals(3, returnedComments.size());
        assertTrue(returnedComments.containsAll(Arrays.asList(comment1, comment2, comment3)));
    
        // Verify that the findByContentContaining method was called
        verify(contentRepository, times(1)).findByContentContaining(text);
    }

    @Test
    public void testDeleteComment() {
        // Create user
        User user = new User("testUser", Role.USER);
    
        // Create a post
        Post post = new Post("Test post", user);
        post.setUser(user);
    
        // Create a comment
        Comment comment = new Comment("Test comment", user);
        comment.setPost(post);
    
        // Save the comment
        when(contentRepository.save(comment)).thenReturn(comment);
    
        // Mock the contentRepository to return the comment when findById is called
        when(contentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
    
        // Call deleteComment
        commentService.deleteComment(comment.getId(), user);
    
        // Verify that the delete method was called
        verify(contentRepository, times(1)).delete(comment);
    }

    @Test
    public void testGetCommentsByPost() {
        // Create user
        User user = new User("testUser", Role.USER);

        // Create a post
        Post post = new Post("Test post", user);
        post.setUser(user);

        // Create comments
        Comment comment1 = new Comment("Test comment 1", user);
        Comment comment2 = new Comment("Test comment 2", user);
        Comment comment3 = new Comment("Test comment 3", user);

        // Save the comments
        when(commentRepository.save(comment1)).thenReturn(comment1);
        when(commentRepository.save(comment2)).thenReturn(comment2);
        when(commentRepository.save(comment3)).thenReturn(comment3);

        // Mock the commentRepository to return the comments when findByPost is called
        when(commentRepository.findByPost(post)).thenReturn(Arrays.asList(comment1, comment2, comment3));

        // Call getCommentsByPost
        List<Comment> returnedComments = commentService.getCommentsByPost(post);

        // Assert that the returned comments are the same as the original comments
        assertNotNull(returnedComments);
        assertEquals(3, returnedComments.size());
        assertTrue(returnedComments.containsAll(Arrays.asList(comment1, comment2, comment3)));

        // Verify that the findByPost method was called
        verify(commentRepository, times(1)).findByPost(post);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetCommentsByPostWithNullPost() {
        // Call getCommentsByPost with null post
        commentService.getCommentsByPost(null);
    }

    @Test
    public void testGetRecentComments() {
        // user
        User user = new User("testUser", Role.USER);

        // Create a post
        Post post = new Post("Test post", user);
        post.setUser(user);

        // Create comments
        Comment comment1 = new Comment("Test comment 1", user);
        Comment comment2 = new Comment("Test comment 2", user);
        Comment comment3 = new Comment("Test comment 3", user);

        // Save the comments
        when(commentRepository.save(comment1)).thenReturn(comment1);
        when(commentRepository.save(comment2)).thenReturn(comment2);
        when(commentRepository.save(comment3)).thenReturn(comment3);

        // Create a Pageable
        Pageable pageable = PageRequest.of(0, 3);

        // Mock the commentRepository to return the comments when findRecentComments is called
        when(commentRepository.findRecentComments(pageable)).thenReturn(Arrays.asList(comment1, comment2, comment3));

        // Call getRecentComments
        List<Comment> returnedComments = commentService.getRecentComments(pageable);

        // Assert that the returned comments are the same as the original comments
        assertNotNull(returnedComments);
        assertEquals(3, returnedComments.size());
        assertTrue(returnedComments.containsAll(Arrays.asList(comment1, comment2, comment3)));

        // Verify that the findRecentComments method was called
        verify(commentRepository, times(1)).findRecentComments(pageable);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetRecentCommentsWithNullPageable() {
        // Call getRecentComments with null pageable
        commentService.getRecentComments(null);
    }

    @Test
    public void testCountCommentsByPostAndUserNot() {
        // Create users
        User user1 = new User("testUser1", Role.USER);
        User user2 = new User("testUser2", Role.USER);
    
        // Create a post
        Post post = new Post("Test post", user1);
        post.setUser(user1);
    
        // Create comments
        Comment comment1 = new Comment("Test comment 1", user2);
        Comment comment2 = new Comment("Test comment 2", user2);
        Comment comment3 = new Comment("Test comment 3", user2);
    
        // Save the comments
        when(commentRepository.save(comment1)).thenReturn(comment1);
        when(commentRepository.save(comment2)).thenReturn(comment2);
        when(commentRepository.save(comment3)).thenReturn(comment3);
    
        // Mock the commentRepository to return the count of comments when countByPostAndUserNot is called
        when(commentRepository.countByPostAndUserNot(post, user1)).thenReturn(3L);
    
        // Call countCommentsByPostAndUserNot
        long returnedCount = commentService.countCommentsByPostAndUserNot(post, user1);
    
        // Assert that the returned count is the same as the original count
        assertEquals(3L, returnedCount);
    
        // Verify that the countByPostAndUserNot method was called
        verify(commentRepository, times(1)).countByPostAndUserNot(post, user1);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCountCommentsByPostAndUserNotWithNullPostAndUser() {
        // Call countCommentsByPostAndUserNot with null post and user
        commentService.countCommentsByPostAndUserNot(null, null);
    }

    @Test
    public void testListContent() {
        // Create user
        User user = new User("testUser", Role.USER);

        // Create a post
        Post post = new Post("Test post", user);
        post.setUser(user);

        // Create comments
        Comment comment1 = new Comment("Test comment 1", user);
        Comment comment2 = new Comment("Test comment 2", user);
        Comment comment3 = new Comment("Test comment 3", user);

        // Save the comments
        when(contentRepository.save(comment1)).thenReturn(comment1);
        when(contentRepository.save(comment2)).thenReturn(comment2);
        when(contentRepository.save(comment3)).thenReturn(comment3);

        // Mock the contentRepository to return the comments when findAll is called
        when(contentRepository.findAll()).thenReturn(Arrays.asList(comment1, comment2, comment3));

        // Call listContent
        List<Content> returnedContents = commentService.listContent();

        // Assert that the returned contents are the same as the original comments
        assertNotNull(returnedContents);
        assertEquals(3, returnedContents.size());
        assertTrue(returnedContents.containsAll(Arrays.asList(comment1, comment2, comment3)));

        // Verify that the findAll method was called
        verify(contentRepository, times(1)).findAll();
    }

    @Test
    public void testHandleCommentVote() {
        // Create a user
        User user = new User("testUser", Role.USER);
    
        // Create a comment
        Content comment = new Comment("Test comment", user);
    
        // Mock the userRepository to return the user when findById is called with 1L
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    
        // Mock the contentRepository to return the comment when findById is called with 1L
        when(contentRepository.findById(1L)).thenReturn(Optional.of(comment));
    
        // Mock the voteRepository to return Optional.empty() when findByUserAndContent is called
        when(voteRepository.findByUserAndContent(user, comment)).thenReturn(Optional.empty());
    
        // Call handleCommentVote
        commentService.handleCommentVote(1L, 1L, true);
    
        // Verify that the correct methods were called on the mock repositories
        verify(userRepository, times(1)).findById(1L);
        verify(contentRepository, times(1)).findById(1L);
        verify(voteRepository, times(1)).findByUserAndContent(user, comment);
        verify(contentRepository, times(1)).save(any(Content.class));
    }
    
}
