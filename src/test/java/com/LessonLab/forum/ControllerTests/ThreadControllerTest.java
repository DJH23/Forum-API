package com.LessonLab.forum.ControllerTests;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.LessonLab.forum.Controllers.ThreadController;
import com.LessonLab.forum.Models.Thread;
import com.LessonLab.forum.Services.ThreadService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ThreadControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @MockBean
    private ThreadService threadService;

    @Autowired
    private ThreadController threadController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser(roles = { "USER", "ADMIN", "MODERATOR" })
    public void testCreateThread() {
        Thread mockThread = new Thread("Test Title", "Test Description");
        when(threadService.createThread(anyString(), anyString())).thenReturn(mockThread);

        ResponseEntity<?> response = threadController.createThread("Test Title", "Test Description");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockThread, response.getBody());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testUpdateThread_Success() throws Exception {
        Long threadId = 1L;
        String newTitle = "Updated Title";
        String newDescription = "Updated Description";
        Thread updatedThread = new Thread(newTitle, newDescription);
        updatedThread.setContentId(threadId);

        when(threadService.updateThread(eq(threadId), any(Thread.class))).thenReturn(updatedThread);

        mockMvc.perform(put("/api/threads/{id}", threadId)
                .param("newTitle", newTitle)
                .param("newDescription", newDescription)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(newTitle))
                .andExpect(jsonPath("$.description").value(newDescription));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testUpdateThread_NotFound() throws Exception {
        Long threadId = 1L;
        String newTitle = "Updated Title";
        String newDescription = "Updated Description";

        when(threadService.updateThread(eq(threadId), any(Thread.class)))
                .thenThrow(new IllegalArgumentException("Thread not found"));

        mockMvc.perform(put("/api/threads/{id}", threadId)
                .param("newTitle", newTitle)
                .param("newDescription", newDescription)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Thread not found"));
    }

    @Test
    public void testGetThreadsByTitle() throws Exception {
        List<Thread> threads = new ArrayList<>();
        String titleText = "Test thread";
        for (int i = 0; i < 3; i++) {
            Thread thread = new Thread(titleText + " " + i, "Test thread description");
            threads.add(thread);
        }

        when(threadService.getThreadsByTitle(titleText)).thenReturn(threads);

        mockMvc.perform(get("/api/threads/title/" + titleText))
                .andExpect(status().isOk())
                .andExpect(content().json(serializeThreads(threads)));
    }

    @Test
    public void testGetThreadsByDescription() throws Exception {
        List<Thread> threads = new ArrayList<>();
        String descriptionText = "Test thread description";
        for (int i = 0; i < 3; i++) {
            Thread thread = new Thread("Test thread " + i, descriptionText);
            threads.add(thread);
        }

        when(threadService.getThreadsByDescription(descriptionText)).thenReturn(threads);

        mockMvc.perform(get("/api/threads/description/" + descriptionText))
                .andExpect(status().isOk())
                .andExpect(content().json(serializeThreads(threads)));
    }

    private String serializeThreads(List<Thread> threads) {
        // Implement serialization logic here
        // For example, using ObjectMapper from Jackson library
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(threads);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}