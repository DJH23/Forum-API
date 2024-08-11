package com.LessonLab.forum.ServiceTests;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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
import com.LessonLab.forum.Models.CommentDTO;
import com.LessonLab.forum.Models.Content;
import com.LessonLab.forum.Models.Post;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Repositories.CommentRepository;
import com.LessonLab.forum.Repositories.ContentRepository;
import com.LessonLab.forum.Repositories.PostRepository;
import com.LessonLab.forum.Services.CommentService;


public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private ContentRepository contentRepository;

    @InjectMocks
    private CommentService commentService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetCommentsByPost() {
        Post post = new Post();
        List<Comment> expectedComments = Arrays.asList(new Comment(), new Comment());
        when(commentRepository.findByPost(post)).thenReturn(expectedComments);

        List<Comment> actualComments = commentService.getCommentsByPost(post);

        assertEquals(expectedComments, actualComments);
        verify(commentRepository).findByPost(post);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetCommentsByPostWithNullPost() {
        commentService.getCommentsByPost(null);
    }

    @Test
    public void testGetRecentContents() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Content> contents = Arrays.asList(
                new Comment(),
                new Post(), // This should be filtered out
                new Comment());
        Page<Content> contentPage = new PageImpl<>(contents, pageable, contents.size());
        when(contentRepository.findRecentContents(pageable)).thenReturn(contentPage);

        Page<Comment> result = commentService.getRecentContents(pageable);

        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().stream().allMatch(content -> content instanceof Comment));
    }

    @Test
    public void testCountCommentsByPostAndUserNot() {
        Post post = new Post();
        User user = new User();
        long expectedCount = 5L;
        when(commentRepository.countByPostAndUserNot(post, user)).thenReturn(expectedCount);

        long actualCount = commentService.countCommentsByPostAndUserNot(post, user);

        assertEquals(expectedCount, actualCount);
        verify(commentRepository).countByPostAndUserNot(post, user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCountCommentsByPostAndUserNotWithNullPost() {
        commentService.countCommentsByPostAndUserNot(null, new User());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCountCommentsByPostAndUserNotWithNullUser() {
        commentService.countCommentsByPostAndUserNot(new Post(), null);
    }

    @Test
    public void testAddContent() {
        CommentDTO dto = new CommentDTO();
        dto.setPostId(1L);
        dto.setContent("Test comment");
        User user = new User();
        Post post = new Post();
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        Comment expectedComment = new Comment(dto.getContent(), user, post);
        when(commentRepository.save(any(Comment.class))).thenReturn(expectedComment);

        Comment result = commentService.addContent(dto, user);

        assertNotNull(result);
        assertEquals(dto.getContent(), result.getContent());
        assertEquals(user, result.getUser());
        assertEquals(post, result.getPost());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    public void testListContent() {
        List<Comment> expectedComments = Arrays.asList(new Comment(), new Comment());
        when(commentRepository.findAll()).thenReturn(expectedComments);

        List<Comment> result = commentService.listContent();

        assertEquals(expectedComments, result);
        verify(commentRepository).findAll();
    }

    @Test
    public void testGetPagedCommentsByUser() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> expectedPage = new PageImpl<>(Arrays.asList(new Comment(), new Comment()));
        when(commentRepository.findCommentsByUserId(userId, pageable)).thenReturn(expectedPage);

        Page<Comment> result = commentService.getPagedCommentsByUser(userId, pageable);

        assertEquals(expectedPage, result);
        verify(commentRepository).findCommentsByUserId(userId, pageable);
    }

    @Test
    public void testAddCommentToPost() {
        Long postId = 1L;
        String content = "Test comment";
        User user = new User();
        Post post = new Post();
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        Comment expectedComment = new Comment(content, user, post);
        when(commentRepository.save(any(Comment.class))).thenReturn(expectedComment);

        Comment result = commentService.addCommentToPost(postId, content, user);

        assertNotNull(result);
        assertEquals(content, result.getContent());
        assertEquals(user, result.getUser());
        assertEquals(post, result.getPost());
        verify(postRepository).findById(postId);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test(expected = RuntimeException.class)
    public void testAddCommentToPostWithNonExistentPost() {
        Long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        commentService.addCommentToPost(postId, "Test comment", new User());
    }
}