package com.LessonLab.forum.ControllerTests;

import com.LessonLab.forum.Models.User;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.LessonLab.forum.Services.CommentService;
import com.LessonLab.forum.Services.UserService;
import com.LessonLab.forum.Models.Comment;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc

public class CommentControllerTest {

       @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(roles = {"USER"})
    public void testAddCommentToPost() throws Exception {
        Long postId = 1L;
        String commentContent = "This is a test comment";
        User user = new User(); // Initialize with necessary fields
        Comment comment = new Comment(commentContent, user, null); // Initialize with necessary fields

        Mockito.when(userService.getCurrentUser()).thenReturn(user);
        Mockito.when(commentService.addCommentToPost(postId, commentContent, user)).thenReturn(comment);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/comments/add-comment-to-post")
                .param("postId", postId.toString())
                .param("commentContent", commentContent)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value(commentContent));
    }

}
