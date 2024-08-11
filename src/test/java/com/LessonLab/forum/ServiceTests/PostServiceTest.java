package com.LessonLab.forum.ServiceTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

import com.LessonLab.forum.Models.Content;
import com.LessonLab.forum.Models.Post;
import com.LessonLab.forum.Models.PostDTO;
import com.LessonLab.forum.Models.Thread;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Repositories.CommentRepository;
import com.LessonLab.forum.Repositories.ContentRepository;
import com.LessonLab.forum.Repositories.PostRepository;
import com.LessonLab.forum.Repositories.ThreadRepository;
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
    private ThreadRepository threadRepository;
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
    public void testGetPostsByThread_ValidThread() {
        Thread thread = new Thread();
        List<Post> expectedPosts = Arrays.asList(new Post(), new Post());

        when(postRepository.findByThread(thread)).thenReturn(expectedPosts);

        List<Post> actualPosts = postService.getPostsByThread(thread);

        assertEquals(expectedPosts, actualPosts);
        verify(postRepository, times(1)).findByThread(thread);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPostsByThread_NullThread() {
        postService.getPostsByThread(null);
    }

    @Test
    public void testGetPostsByCommentContent_ValidContent() {
        String content = "sample content";
        List<Post> expectedPosts = Arrays.asList(new Post(), new Post());

        when(postRepository.findByCommentContent(content)).thenReturn(expectedPosts);

        List<Post> actualPosts = postService.getPostsByCommentContent(content);

        assertEquals(expectedPosts, actualPosts);
        verify(postRepository, times(1)).findByCommentContent(content);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPostsByCommentContent_NullContent() {
        postService.getPostsByCommentContent(null);
    }

    @Test
    public void testGetMostCommentedPostDTOs_ValidInput() {
        Pageable pageable = PageRequest.of(0, 10);
        List<PostDTO> postDTOs = new ArrayList<>();
        PostDTO postDTO = new PostDTO("Sample Content", 1L, 5L);
        postDTOs.add(postDTO);

        when(postRepository.findMostCommentedPostDTOs(pageable)).thenReturn(postDTOs);

        List<PostDTO> result = postService.getMostCommentedPostDTOs(pageable, true);

        assertEquals(postDTOs, result);
        assertEquals(true, result.get(0).getShowNestedComments());
        verify(postRepository, times(1)).findMostCommentedPostDTOs(pageable);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetMostCommentedPostDTOs_NullPageable() {
        postService.getMostCommentedPostDTOs(null, true);
    }

    @Test
    public void testGetRecentContents_ValidInput() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Content> contents = Arrays.asList(new Post(), new Post());
        Page<Content> contentPage = new PageImpl<>(contents, pageable, contents.size());

        when(contentRepository.findRecentContents(pageable)).thenReturn(contentPage);

        Page<Post> result = postService.getRecentContents(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(contentRepository, times(1)).findRecentContents(pageable);
    }

    @Test
    public void testAddPostToThread_ValidInput() {
        Long threadId = 1L;
        String content = "Sample content";
        User user = new User();
        Thread thread = new Thread();

        when(threadRepository.findById(threadId)).thenReturn(Optional.of(thread));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Post result = postService.addPostToThread(threadId, content, user);

        assertNotNull(result);
        assertEquals(content, result.getContent());
        assertEquals(user, result.getUser());
        assertEquals(thread, result.getThread());
        verify(threadRepository, times(1)).findById(threadId);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test(expected = RuntimeException.class)
    public void testAddPostToThread_ThreadNotFound() {
        Long threadId = 1L;

        when(threadRepository.findById(threadId)).thenReturn(Optional.empty());

        postService.addPostToThread(threadId, "Sample content", new User());
    }

    @Test
    public void testListContent_IncludeNested() {
        List<Post> posts = Arrays.asList(new Post(), new Post());

        when(postRepository.findAllWithComments()).thenReturn(posts);

        List<Post> result = postService.listContent(true);

        assertEquals(posts, result);
        verify(postRepository, times(1)).findAllWithComments();
    }

    @Test
    public void testListContent_WithoutNested() {
        List<Post> posts = Arrays.asList(new Post(), new Post());

        when(postRepository.findAllWithoutComments()).thenReturn(posts);

        List<Post> result = postService.listContent(false);

        assertEquals(posts, result);
        verify(postRepository, times(1)).findAllWithoutComments();
    }

    @Test
    public void testGetPagedPostsByUser_ValidInput() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<Post> posts = Arrays.asList(new Post(), new Post());
        Page<Post> postPage = new PageImpl<>(posts, pageable, posts.size());

        when(postRepository.findPostsByUserId(userId, pageable)).thenReturn(postPage);

        Page<Post> result = postService.getPagedPostsByUser(userId, pageable);

        assertEquals(postPage, result);
        verify(postRepository, times(1)).findPostsByUserId(userId, pageable);
    }

}
