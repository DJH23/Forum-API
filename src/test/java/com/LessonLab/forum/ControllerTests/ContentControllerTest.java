package com.LessonLab.forum.ControllerTests;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.LessonLab.forum.Services.CommentService;
import com.LessonLab.forum.Services.PostService;
import com.LessonLab.forum.Services.ThreadService;
import com.LessonLab.forum.Models.Comment;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.util.logging.Logger;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@WithMockUser(username = "admin", password = "password", roles = "ADMIN")
public class ContentControllerTest {

    private static final Logger logger = Logger.getLogger(ContentControllerTest.class.getName());

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;
    @MockBean
    private PostService postService;
    @MockBean
    private ThreadService threadService;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testAddContent() throws Exception {
        Comment comment = new Comment();
        comment.setContent("This is a comment");

        logger.info("Created Comment object: " + comment);

        when(commentService.addContent(any(Comment.class), any())).thenReturn(comment);
        logger.info("Mocked commentService.addContent method");

        String requestJson = new ObjectMapper().writeValueAsString(comment);
        logger.info("Request JSON: " + requestJson);

        MvcResult result = mockMvc.perform(post("/api/contents/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().is4xxClientError())
                .andReturn(); // Capture the result to inspect the response

        logger.info("Performed POST request to /api/contents/comment");

        logger.info("Response status: " + result.getResponse().getStatus());
        logger.info("Response headers: " + result.getResponse().getHeaderNames());
        logger.info("Response body on error: " + result.getResponse().getContentAsString());
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
                .andReturn();  // Capture the result to inspect the response
    
        logger.info("Performed PUT request to /api/contents/comment/1");
    
        logger.info("Response status: " + result.getResponse().getStatus());
        logger.info("Response headers: " + result.getResponse().getHeaderNames());
        logger.info("Response body: " + result.getResponse().getContentAsString());
    }
}
