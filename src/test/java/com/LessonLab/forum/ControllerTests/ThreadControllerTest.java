package com.LessonLab.forum.ControllerTests;

import com.LessonLab.forum.Models.Thread;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Models.Enums.Account;
import com.LessonLab.forum.Models.Enums.Role;
import com.LessonLab.forum.Models.Enums.Status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.LessonLab.forum.Services.CommentService;
import com.LessonLab.forum.Services.PostService;
import com.LessonLab.forum.Services.ThreadService;
import com.LessonLab.forum.Services.UserService;
import com.LessonLab.forum.Models.Comment;
import com.LessonLab.forum.Models.Content;
import com.LessonLab.forum.Repositories.ThreadRepository;
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
import com.fasterxml.jackson.core.JsonProcessingException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc

public class ThreadControllerTest {

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
    @MockBean
    private ThreadRepository threadRepository;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testCreateThread() throws Exception {
        // Arrange
        User user = createUser();
        Thread thread = createThread(user);
        Thread savedThread = createSavedThread(thread);

        String threadJson = serializeThread(thread);

        // Assume that the threadService returns the savedThread when called with the
        // thread
        when(threadService.createThread(thread)).thenReturn(savedThread);

        // Act and Assert
        mockMvc.perform(post("/api/threads/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(threadJson)) // Use the serialized threadJson
                .andExpect(status().isCreated())
                .andReturn();

        // Verify that the createThread method was called with the expected thread
        verify(threadService, times(1)).createThread(argThat(new ArgumentMatcher<Thread>() {
            @Override
            public boolean matches(Thread argument) {
                return argument.getTitle().equals(thread.getTitle())
                        && argument.getDescription().equals(thread.getDescription());
            }
        }));
    }

    private User createUser() {
        User user = new User();
        user.setUserId(1L);
        user.setUsername("Test User");
        user.setRole(Role.USER);
        user.setStatus(Status.ONLINE);
        user.setAccountStatus(Account.ACTIVE);
        user.setContents(new ArrayList<>());
        return user;
    }

    private Thread createThread(User user) {
        Thread thread = new Thread();
        thread.setTitle("Test Thread");
        thread.setDescription("This is a test thread");
        thread.setContentId(1L);
        thread.setContent("Test content");
        thread.setUser(user);
        thread.setPosts(new ArrayList<>());
        return thread;
    }

    private Thread createSavedThread(Thread thread) {
        Thread savedThread = new Thread();
        savedThread.setTitle(thread.getTitle());
        savedThread.setDescription(thread.getDescription());
        return savedThread;
    }

    private String serializeThread(Thread thread) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper.writeValueAsString(thread);
    }

    @Test
    public void testUpdateThread() throws Exception {
        // Arrange
        User user = createUser();
        Thread thread = createThread(user);
        Thread updatedThread = createUpdatedThread(thread);

        String threadJson = serializeThread(updatedThread);

        // Assume that the threadService returns the updatedThread when called with the thread
        when(threadService.updateThread(any(Long.class), any(Thread.class))).thenReturn(updatedThread);

        // Act and Assert
        mockMvc.perform(put("/api/threads/" + thread.getContentId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(threadJson)) // Use the serialized threadJson
            .andExpect(status().isOk())
            .andReturn();

        // Verify that the updateThread method was called with the expected thread
        verify(threadService, times(1)).updateThread(any(Long.class), argThat(new ArgumentMatcher<Thread>() {
            @Override
            public boolean matches(Thread argument) {
                return argument.getTitle().equals(updatedThread.getTitle())
                    && argument.getDescription().equals(updatedThread.getDescription());
            }
        }));
    }

    private Thread createUpdatedThread(Thread thread) {
        Thread updatedThread = new Thread();
        updatedThread.setTitle("Updated " + thread.getTitle());
        updatedThread.setDescription("Updated " + thread.getDescription());
        return updatedThread;
    }

    @Test
    public void testGetThreadsByTitle() {
        // Arrange
        List<Thread> threads = new ArrayList<>();
        String titleText = "Test thread";
        for (int i = 0; i < 3; i++) {
            Thread thread = new Thread(titleText + " " + i, "Test thread description");
            threads.add(thread);
        }

        System.out.println("Title text: " + titleText);
        System.out.println("Threads: " + threads);

        // Assume that the threadRepository returns the threads when findByTitleContaining is called
        when(threadRepository.findByTitleContaining(titleText)).thenReturn(threads);

        // Act
        List<Thread> retrievedThreads = threadService.getThreadsByTitle(titleText);

        System.out.println("Retrieved threads: " + retrievedThreads);

        // Assert
        assertNotNull(retrievedThreads);
        assertEquals(threads, retrievedThreads);
    }

    
}
