package com.LessonLab.forum.ServiceTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.LessonLab.forum.Models.Content;
import com.LessonLab.forum.Models.Thread;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Repositories.ContentRepository;
import com.LessonLab.forum.Repositories.ThreadRepository;
import com.LessonLab.forum.Services.ThreadService;
import com.LessonLab.forum.Services.UserService;
import com.LessonLab.forum.Models.ThreadDTO;

class ThreadServiceTest {

    @Mock
    private ThreadRepository threadRepository;

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ThreadService threadService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");

        when(userService.getCurrentUser()).thenReturn(testUser);
    }

    @Test
    void testCreateThread() {
        String title = "Test Thread";
        String description = "Test Description";
        Thread thread = new Thread(title, description);
        thread.setUser(testUser);

        when(contentRepository.save(any(Thread.class))).thenReturn(thread);

        Thread result = threadService.createThread(title, description);

        assertNotNull(result);
        assertEquals(title, result.getTitle());
        assertEquals(description, result.getDescription());
        assertEquals(testUser, result.getUser());
        verify(contentRepository).save(any(Thread.class));
    }

    @Test
    void testCreateThreadWithNullUser() {
        when(userService.getCurrentUser()).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> threadService.createThread("Test", "Description"));
    }

    @Test
    void testUpdateThread() {
        Long threadId = 1L;
        Thread existingThread = new Thread("Existing Title", "Existing Description");
        existingThread.setContentId(threadId);
        existingThread.setUser(testUser);
        Thread updateThread = new Thread("Updated Title", "Updated Description");

        when(contentRepository.findById(threadId)).thenReturn(Optional.of(existingThread));
        when(contentRepository.save(any(Thread.class))).thenAnswer(invocation -> {
            Thread savedThread = invocation.getArgument(0);
            savedThread.setUser(testUser); // Ensure the user is set
            return savedThread;
        });

        Thread result = threadService.updateThread(threadId, updateThread);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(testUser, result.getUser());
        verify(contentRepository).save(any(Thread.class));
    }

    @Test
    void testUpdateNonExistentThread() {
        Long threadId = 1L;
        when(contentRepository.findById(threadId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> threadService.updateThread(threadId, new Thread()));
    }

    @Test
    void testGetThreadsByTitle() {
        String title = "Test";
        List<Thread> expectedThreads = Arrays.asList(new Thread(), new Thread());
        when(threadRepository.findByTitleContaining(title)).thenReturn(expectedThreads);

        List<Thread> result = threadService.getThreadsByTitle(title);

        assertEquals(expectedThreads, result);
        verify(threadRepository).findByTitleContaining(title);
    }

    @Test
    void testGetThreadsByTitleWithNullTitle() {
        assertThrows(IllegalArgumentException.class, () -> threadService.getThreadsByTitle(null));
    }

    @Test
    void testGetThreadsByDescription() {
        String description = "Test";
        List<Thread> expectedThreads = Arrays.asList(new Thread(), new Thread());
        when(threadRepository.findByDescriptionContaining(description)).thenReturn(expectedThreads);

        List<Thread> result = threadService.getThreadsByDescription(description);

        assertEquals(expectedThreads, result);
        verify(threadRepository).findByDescriptionContaining(description);
    }

    @Test
    void testGetThreadsByDescriptionWithNullDescription() {
        assertThrows(IllegalArgumentException.class, () -> threadService.getThreadsByDescription(null));
    }

    @Test
    void testGetRecentContents() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Content> contents = Arrays.asList(
                new Thread("Thread 1", "Description 1"),
                new Thread("Thread 2", "Description 2"));
        Page<Content> contentPage = new PageImpl<>(contents, pageable, contents.size());
        when(contentRepository.findRecentContents(pageable)).thenReturn(contentPage);

        Page<Thread> result = threadService.getRecentContents(pageable);

        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().stream().allMatch(content -> content instanceof Thread));
    }

    @Test
    void testCreateWithThreadId() {
        Long threadId = 1L;
        ThreadDTO result = ThreadService.createWithThreadId(threadId);

        assertNotNull(result);
        assertEquals(threadId, result.getThreadId());
    }

    @Test
    void testGetThreadWithId() {
        Long threadId = 1L;
        Thread thread = new Thread("Test Thread", "Test Description");
        thread.setContentId(threadId);
        when(contentRepository.findById(threadId)).thenReturn(Optional.of(thread));

        Thread result = threadService.getThreadWithId(threadId);

        assertNotNull(result);
        assertEquals(threadId, result.getContentId());
    }

    @Test
    void testGetThreadWithIdNotFound() {
        Long threadId = 1L;
        when(contentRepository.findById(threadId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> threadService.getThreadWithId(threadId));
    }

    @Test
    void testListContentWithNestedPosts() {
        List<Thread> expectedThreads = Arrays.asList(new Thread(), new Thread());
        when(threadRepository.findAllWithPosts()).thenReturn(expectedThreads);

        List<Thread> result = threadService.listContent(true);

        assertEquals(expectedThreads, result);
        verify(threadRepository).findAllWithPosts();
    }

    @Test
    void testListContentWithoutNestedPosts() {
        List<Thread> expectedThreads = Arrays.asList(new Thread(), new Thread());
        when(threadRepository.findAll()).thenReturn(expectedThreads);

        List<Thread> result = threadService.listContent(false);

        assertEquals(expectedThreads, result);
        verify(threadRepository).findAll();
    }

    @Test
    void testGetPagedThreadsByUser() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Thread> expectedPage = new PageImpl<>(Arrays.asList(new Thread(), new Thread()));
        when(threadRepository.findThreadsByUserId(userId, pageable)).thenReturn(expectedPage);

        Page<Thread> result = threadService.getPagedThreadsByUser(userId, pageable);

        assertEquals(expectedPage, result);
        verify(threadRepository).findThreadsByUserId(userId, pageable);
    }
}