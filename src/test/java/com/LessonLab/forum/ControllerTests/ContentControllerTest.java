package com.LessonLab.forum.ControllerTests;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.LessonLab.forum.Services.CommentService;
import com.LessonLab.forum.Services.PostService;
import com.LessonLab.forum.Services.ThreadService;
import com.LessonLab.forum.Services.UserService;
import com.LessonLab.forum.Models.Comment;
import com.LessonLab.forum.Models.Content;
import com.LessonLab.forum.Repositories.UserRepository;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
// @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
public class ContentControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(ContentControllerTest.class);

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;
    @MockBean
    private PostService postService;
    @MockBean
    private ThreadService threadService;
    @MockBean
    private UserService userService;
    @MockBean
    private UserRepository userRepository;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    /*
     * @Test
     * public void testAddContent() throws Exception {
     * User user = new User();
     * user.setUsername("testUser");
     * user.setRole(Role.ADMIN);
     * userRepository.save(user);
     * 
     * when(userService.getCurrentUser()).thenReturn(user);
     * 
     * Comment comment = new Comment();
     * comment.setContent("This is a comment");
     * comment.setUser(user);
     * LocalDateTime createdAt = LocalDateTime.now(); // Get the current date and
     * time
     * comment.setCreatedAt(createdAt);
     * 
     * logger.info("Created Comment object: " + comment);
     * 
     * // when(commentService.addContent(any(Comment.class),
     * any())).thenReturn(comment);
     * logger.info("Mocked commentService.addContent method");
     * 
     * ObjectMapper mapper = new ObjectMapper();
     * mapper.registerModule(new JavaTimeModule());
     * String requestJson = mapper.writeValueAsString(comment);
     * logger.info("Request JSON: " + requestJson);
     * 
     * MvcResult result = mockMvc.perform(post("/api/contents/comment")
     * .contentType(MediaType.APPLICATION_JSON)
     * .content(requestJson))
     * .andExpect(status().isCreated()) // Expect a 201 status for successful
     * creation
     * .andReturn(); // Capture the result to inspect the response
     * 
     * logger.info("Performed POST request to /api/contents/comment");
     * 
     * logger.info("Response status: " + result.getResponse().getStatus());
     * logger.info("Response headers: " + result.getResponse().getHeaderNames());
     * logger.info("Response body: " + result.getResponse().getContentAsString());
     * 
     * // Verify that the addContent method was called with the expected parameters
     * verify(commentService, times(1)).addContent(any(Comment.class), any());
     * }
     */

    @Test
    public void testAddContent() throws Exception {
        Comment comment = new Comment();
        comment.setContent("This is a new comment");

        when(commentService.addContent(any(Comment.class), any())).thenReturn(comment);

        String requestJson = new ObjectMapper().writeValueAsString(comment);

        MvcResult result = mockMvc.perform(post("/api/contents/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andReturn();

        logger.info("Performed POST request to /api/contents/comment");

        logger.info("Response status: " + result.getResponse().getStatus());
        logger.info("Response headers: " + result.getResponse().getHeaderNames());
        logger.info("Response body: " + result.getResponse().getContentAsString());

        // Verify that the addContent method was called with the expected parameters
        verify(commentService, times(1)).addContent(any(Comment.class), any());
    }

    @Test
    public void testUpdateContent() throws Exception {
        Comment comment = new Comment();
        comment.setContent("This is an updated comment");

        when(commentService.updateContent(any(Long.class), any(String.class), any())).thenReturn(comment);

        String requestJson = new ObjectMapper().writeValueAsString(comment.getContent());

        MvcResult result = mockMvc.perform(put("/api/contents/comment/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andReturn();

        logger.info("Performed PUT request to /api/contents/comment/1");

        logger.info("Response status: " + result.getResponse().getStatus());
        logger.info("Response headers: " + result.getResponse().getHeaderNames());
        logger.info("Response body: " + result.getResponse().getContentAsString());
    }

    @Test
    public void testGetContent() throws Exception {
        // Arrange
        Long id = 1L;
        Comment comment = new Comment();
        comment.setContent("This is a comment");

        // Assume that the commentService returns the comment when called with the ID
        when(commentService.getContent(id)).thenReturn(comment);

        // Act and Assert
        mockMvc.perform(get("/api/contents/comment/" + id))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(comment)));

        // Verify that the commentService was called with the expected ID
        verify(commentService, times(1)).getContent(id);
    }

    @Test
    public void testSearchContent() throws Exception {
        // Arrange
        String searchText = "test";
        List<Content> comments = new ArrayList<>();
        Comment comment = new Comment();
        comment.setContent("This is a test comment");
        comments.add(comment);

        // Assume that the commentService returns the comments when called with the
        // search text
        when(commentService.searchContent(searchText)).thenReturn(comments);

        // Act and Assert
        mockMvc.perform(get("/api/contents/search/comment")
                .param("searchText", searchText))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(comments)));

        // Verify that the commentService was called with the expected search text
        verify(commentService, times(1)).searchContent(searchText);
    }

    @Test
    public void testGetPagedContentByUser() throws Exception {
        // Arrange
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<Content> commentList = new ArrayList<>();
        Comment comment = new Comment();
        comment.setContent("This is a test comment");
        commentList.add(comment);
        Page<Content> comments = new PageImpl<>(commentList);

        // Assume that the commentService returns the comments when called with the user
        // ID and pageable
        when(commentService.getPagedContentByUser(userId, pageable)).thenReturn(comments);

        // Act and Assert
        mockMvc.perform(get("/api/contents/user/comment/" + userId)
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("size", String.valueOf(pageable.getPageSize())))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(comments)));

        // Verify that the commentService was called with the expected user ID and
        // pageable
        verify(commentService, times(1)).getPagedContentByUser(userId, pageable);
    }

    @Test
    public void testGetContentsByCreatedAtBetween() throws Exception {
        // Arrange
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        List<Content> commentList = new ArrayList<>();
        Comment comment = new Comment();
        comment.setContent("This is a test comment");
        commentList.add(comment);

        // Assume that the commentService returns the comments when called with the
        // start and end dates
        when(commentService.getContentsByCreatedAtBetween(start, end)).thenReturn(commentList);

        // Act and Assert
        mockMvc.perform(get("/api/contents/created-at-between/comment")
                .param("start", start.toString())
                .param("end", end.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(commentList)));

        // Verify that the commentService was called with the expected start and end
        // dates
        verify(commentService, times(1)).getContentsByCreatedAtBetween(start, end);
    }

    @Test
    public void testGetContentsByContentContaining() throws Exception {
        // Arrange
        String text = "test";
        List<Content> commentList = new ArrayList<>();
        Comment comment = new Comment();
        comment.setContent("This is a test comment");
        commentList.add(comment);

        // Assume that the commentService returns the comments when called with the text
        when(commentService.getContentsByContentContaining(text)).thenReturn(commentList);

        // Act and Assert
        mockMvc.perform(get("/api/contents/content-containing/comment")
                .param("text", text))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(commentList)));

        // Verify that the commentService was called with the expected text
        verify(commentService, times(1)).getContentsByContentContaining(text);
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    public void testDeleteContent() throws Exception {
        // Arrange
        Long id = 1L;

        // Act and Assert
        mockMvc.perform(delete("/api/contents/comment/" + id))
                .andExpect(status().isNoContent());

        // Verify that the commentService was called with the expected ID
        verify(commentService, times(1)).deleteContent(id, null);
    }

    @Test
    public void testListContent() throws Exception {
        // Arrange
        List<Content> commentList = new ArrayList<>();
        Comment comment = new Comment();
        comment.setContent("This is a test comment");
        commentList.add(comment);

        // Assume that the commentService returns the comments when called
        when(commentService.listContent()).thenReturn(commentList);

        // Act and Assert
        mockMvc.perform(get("/api/contents/comment"))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(commentList)));

        // Verify that the commentService was called
        verify(commentService, times(1)).listContent();
    }

    @Test
    @WithMockUser(roles = { "USER" })
    public void testHandleVote() throws Exception {
        // Arrange
        Long contentId = 1L;
        Long userId = 1L;
        boolean isUpVote = true;

        // Act and Assert
        mockMvc.perform(post("/api/contents/comment/" + contentId + "/vote")
                .param("userId", userId.toString())
                .param("isUpVote", String.valueOf(isUpVote)))
                .andExpect(status().isOk());

        // Verify that the commentService was called with the expected content ID, user
        // ID, and vote type
        verify(commentService, times(1)).handleVote(contentId, userId, isUpVote);
    }
}
