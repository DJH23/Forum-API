package com.LessonLab.forum.ControllerTests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import com.LessonLab.forum.Controllers.ContentController;
import com.LessonLab.forum.Models.Comment;
import com.LessonLab.forum.Models.Content;
import com.LessonLab.forum.Models.ContentUpdateDTO;
import com.LessonLab.forum.Models.Post;
import com.LessonLab.forum.Models.Thread;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Services.CommentService;
import com.LessonLab.forum.Services.PostService;
import com.LessonLab.forum.Services.ThreadService;
import com.LessonLab.forum.Services.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ContentControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;
    @MockBean
    private PostService postService;
    @MockBean
    private ThreadService threadService;
    @MockBean
    private UserService userService;
    @InjectMocks
    private ContentController contentController;
    private Pageable pageable;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        pageable = PageRequest.of(0, 10);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(username = "user", roles = { "USER", "ADMIN" })
    public void testUpdateContent_ShouldReturnUpdatedContent() throws Exception {
        String contentType = "comment";
        Long id = 1L;
        ContentUpdateDTO updateDTO = new ContentUpdateDTO();
        updateDTO.setNewContent("Updated Content");

        Comment updatedComment = new Comment();
        updatedComment.setContent(updateDTO.getNewContent());

        when(userService.getCurrentUser()).thenReturn(new User());
        when(commentService.updateContent(eq(id), any(ContentUpdateDTO.class), any(User.class)))
                .thenReturn(updatedComment);

        mockMvc.perform(put("/api/contents/{contentType}/{id}", contentType, id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").value("Updated Content"));

        verify(commentService, times(1)).updateContent(eq(id), any(ContentUpdateDTO.class), any(User.class));
    }

    @Test
    @WithMockUser(roles = { "ADMIN", "MODERATOR" })
    public void testGetContentById_ShouldReturnContent() throws Exception {
        String contentType = "post";
        Long id = 1L;

        Post post = new Post();
        post.setContent("Sample content");

        when(postService.getContentById(eq(id), eq(contentType))).thenReturn(post);

        mockMvc.perform(get("/api/contents/{contentType}/get-content-by-id/{id}", contentType, id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Sample content"));

        verify(postService, times(1)).getContentById(eq(id), eq(contentType));
    }

    @Test
    @WithMockUser(roles = { "USER", "ADMIN", "MODERATOR" })
    public void testSearchContent_ShouldReturnContents() throws Exception {
        String contentType = "comment";
        String searchText = "Sample";

        Comment comment = new Comment();
        comment.setContent("Sample comment");

        when(commentService.searchContent(eq(searchText))).thenReturn(Collections.singletonList(comment));

        mockMvc.perform(get("/api/contents/search/{contentType}", contentType)
                .param("searchText", searchText)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Sample comment"));

        verify(commentService, times(1)).searchContent(eq(searchText));
    }

    @Test
    public void testGetRecentContents_ShouldReturnContents() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<Content> mockContents = Arrays.asList(new Comment(), new Post(), new Comment());

        // Filter only Comment instances
        List<Comment> mockComments = mockContents.stream()
                .filter(content -> content instanceof Comment)
                .map(content -> (Comment) content)
                .collect(Collectors.toList());

        when(contentController.getRecentContents("comment", pageable)).thenReturn(mockComments);

        mockMvc.perform(get("/api/contents/recent")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(mockComments.size()));
    }

    @Test
    public void testGetPagedContentByUser_Comments() throws Exception {
        Page<Comment> comments = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(commentService.getPagedCommentsByUser(anyLong(), any(Pageable.class))).thenReturn(comments);

        mockMvc.perform(get("/api/contents/user/comment/get-paged-content-by-user/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        verify(commentService, times(1)).getPagedCommentsByUser(anyLong(), any(Pageable.class));
    }

    @Test
    public void testGetPagedContentByUser_Posts() throws Exception {
        Page<Post> posts = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(postService.getPagedPostsByUser(anyLong(), any(Pageable.class))).thenReturn(posts);

        mockMvc.perform(get("/api/contents/user/post/get-paged-content-by-user/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        verify(postService, times(1)).getPagedPostsByUser(anyLong(), any(Pageable.class));
    }

    @Test
    public void testGetPagedContentByUser_Threads() throws Exception {
        Page<Thread> threads = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(threadService.getPagedThreadsByUser(anyLong(), any(Pageable.class))).thenReturn(threads);

        mockMvc.perform(get("/api/contents/user/thread/get-paged-content-by-user/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        verify(threadService, times(1)).getPagedThreadsByUser(anyLong(), any(Pageable.class));
    }

    @Test
    public void testGetPagedContentByUser_InvalidContentType() throws Exception {
        mockMvc.perform(get("/api/contents/user/invalid/get-paged-content-by-user/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid content type: invalid"));
    }

    @Test
    public void testGetContentsByCreatedAtBetween() throws Exception {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 31, 23, 59);
        Content thread = new Thread();
        when(commentService.getContentsByCreatedAtBetween(start, end)).thenReturn(Collections.singletonList(thread));

        mockMvc.perform(get("/api/contents/created-at-between/comment")
                .param("start", start.toString())
                .param("end", end.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    public void testGetContentsByCreatedAtBetweenInvalidContentType() throws Exception {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 31, 23, 59);

        mockMvc.perform(get("/api/contents/created-at-between/invalid")
                .param("start", start.toString())
                .param("end", end.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid content type: invalid"));
    }

    @Test
    @WithMockUser(roles = { "USER", "ADMIN", "MODERATOR" })
    public void testGetContentsByContentContaining() throws Exception {
        List<Content> mockContents = Arrays.asList(new Thread(), new Post(), new Comment());

        when(commentService.getContentsByContentContaining(anyString())).thenReturn(mockContents);
        when(postService.getContentsByContentContaining(anyString())).thenReturn(mockContents);
        when(threadService.getContentsByContentContaining(anyString())).thenReturn(mockContents);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/contents/content-containing/comment")
                .param("text", "sampleText")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(mockContents.size()));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/contents/content-containing/post")
                .param("text", "sampleText")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(mockContents.size()));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/contents/content-containing/thread")
                .param("text", "sampleText")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(mockContents.size()));
    }

    @Test
    @WithMockUser(roles = { "USER", "ADMIN", "MODERATOR" })
    public void testGetContentsByContentContainingInvalidType() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/contents/content-containing/invalidType")
                .param("text", "sampleText")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = { "ADMIN", "MODERATOR" })
    public void testDeleteContentById_ShouldReturnNoContent() throws Exception {
        String contentType = "post";
        Long id = 1L;

        when(userService.getCurrentUser()).thenReturn(new User());
        doNothing().when(postService).deleteContent(eq(id), any(User.class), eq(contentType));

        mockMvc.perform(delete("/api/contents/{contentType}/delete-content-by-id/{id}", contentType, id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(postService, times(1)).deleteContent(eq(id), any(User.class), eq(contentType));
    }

    @Test
    public void testListContent_Comment() {
        List<Comment> mockComments = new ArrayList<>();
        when(commentService.listContent()).thenReturn(mockComments);

        ResponseEntity<List<Content>> response = contentController.listContent("comment", false);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockComments, response.getBody());
    }

    @Test
    public void testListContent_Post() {
        List<Post> mockPosts = new ArrayList<>();
        when(postService.listContent(false)).thenReturn(mockPosts);

        ResponseEntity<List<Content>> response = contentController.listContent("post", false);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockPosts, response.getBody());
    }

    @Test
    public void testListContent_Thread() {
        List<Thread> mockThreads = new ArrayList<>();
        when(threadService.listContent(false)).thenReturn(mockThreads);

        ResponseEntity<List<Content>> response = contentController.listContent("thread", false);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockThreads, response.getBody());
    }

    @Test
    public void testListContent_InvalidContentType() {
        try {
            contentController.listContent("invalid", false);
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid content type: invalid", e.getMessage());
        }
    }

    @Test
    public void testHandleVoteForComment() {
        Long contentId = 1L;
        Long userId = 1L;
        boolean isUpVote = true;
        String contentType = "comment";

        doNothing().when(commentService).handleVote(contentId, userId, isUpVote, contentType);

        ResponseEntity<?> response = contentController.handleVote(contentType, contentId, userId, isUpVote);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(commentService).handleVote(contentId, userId, isUpVote, contentType);
    }

    @Test
    public void testHandleVoteForPost() {
        Long contentId = 1L;
        Long userId = 1L;
        boolean isUpVote = true;
        String contentType = "post";

        doNothing().when(postService).handleVote(contentId, userId, isUpVote, contentType);

        ResponseEntity<?> response = contentController.handleVote(contentType, contentId, userId, isUpVote);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(postService).handleVote(contentId, userId, isUpVote, contentType);
    }

    @Test
    public void testHandleVoteForThread() {
        Long contentId = 1L;
        Long userId = 1L;
        boolean isUpVote = true;
        String contentType = "thread";

        doNothing().when(threadService).handleVote(contentId, userId, isUpVote, contentType);

        ResponseEntity<?> response = contentController.handleVote(contentType, contentId, userId, isUpVote);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(threadService).handleVote(contentId, userId, isUpVote, contentType);
    }

    @Test
    public void testHandleVoteInvalidContentType() {
        Long contentId = 1L;
        Long userId = 1L;
        boolean isUpVote = true;
        String contentType = "invalid";

        try {
            contentController.handleVote(contentType, contentId, userId, isUpVote);
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid content type: " + contentType, e.getMessage());
        }
    }
}