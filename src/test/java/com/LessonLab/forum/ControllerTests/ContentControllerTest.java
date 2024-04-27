package com.LessonLab.forum.ControllerTests;
    
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.LessonLab.forum.Controllers.ContentController;
import com.LessonLab.forum.Models.Comment;
import com.LessonLab.forum.Services.CommentService;
import com.LessonLab.forum.Services.PostService;
import com.LessonLab.forum.Services.ThreadService;
import com.fasterxml.jackson.databind.ObjectMapper;


@RunWith(SpringRunner.class)
//@WebMvcTest(ContentController.class)
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMIN")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ContentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private PostService postService;

    @MockBean
    private ThreadService threadService;

    // Test for addContent
    @Test
    public void testAddContent() throws Exception {
        Comment comment = new Comment();
        comment.setContentId(1L);
        when(commentService.addContent(any(Comment.class), any())).thenReturn(comment);

        mockMvc.perform(post("/api/contents/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(comment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)));
    }
    
    // Test for updateContent
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateContent() throws Exception {
        Comment comment = new Comment();
        comment.setContentId(1L);
        when(commentService.updateContent(any(Long.class), any(String.class), any())).thenReturn(comment);
    
        mockMvc.perform(put("/api/contents/comment/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("New Content"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    // Add more tests for other endpoints here...
}