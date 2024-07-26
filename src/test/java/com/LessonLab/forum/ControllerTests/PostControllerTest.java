package com.LessonLab.forum.ControllerTests;

import com.LessonLab.forum.Controllers.PostController;
import com.LessonLab.forum.Models.Post;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Services.PostService;
import com.LessonLab.forum.Services.UserService;
import com.LessonLab.forum.Models.PostDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PostControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Mock
    private PostService postService;

    @Mock
    private UserService userService;

    @InjectMocks
    private PostController postController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser(roles = { "USER" })
    public void testAddPostContentToThread() {
        User mockUser = new User();
        Post mockPost = new Post();
        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(postService.addPostToThread(any(Long.class), any(String.class), any(User.class))).thenReturn(mockPost);

        ResponseEntity<?> response = postController.addPostContentToThread(1L, "Test post content");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockPost, response.getBody());
    }

    @Test
    @WithMockUser(roles = { "USER" })
    public void testGetMostCommentedPostDTOs() {
        List<PostDTO> mockPosts = new ArrayList<>();
        Pageable pageable = PageRequest.of(0, 10);
        when(postService.getMostCommentedPostDTOs(pageable, true)).thenReturn(mockPosts);

        ResponseEntity<List<PostDTO>> response = postController.getMostCommentedPostDTOs(pageable, true);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockPosts, response.getBody());
    }
}
