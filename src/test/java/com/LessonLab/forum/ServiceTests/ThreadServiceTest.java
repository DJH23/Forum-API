package com.LessonLab.forum.ServiceTests;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.LessonLab.forum.Models.Content;
import com.LessonLab.forum.Models.Thread;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Models.Enums.Role;
import com.LessonLab.forum.Repositories.ContentRepository;
import com.LessonLab.forum.Repositories.ThreadRepository;
import com.LessonLab.forum.Repositories.UserRepository;
import com.LessonLab.forum.Repositories.VoteRepository;
import com.LessonLab.forum.Services.ContentService;
import com.LessonLab.forum.Services.ThreadService;


@RunWith(MockitoJUnitRunner.class)
public class ThreadServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private VoteRepository voteRepository;
    @Mock
    private ThreadRepository threadRepository;
    @Mock
    private ContentService contentService;
    @InjectMocks
    private ThreadService threadService;
    private AutoCloseable closeable;

    @BeforeEach
    public void setup() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void closeService() throws Exception {
        closeable.close();
    }

    @Test
    public void testCreateThread() {
        // Create a thread
        Thread thread = new Thread("Test thread title", "Test thread description");

        // Mock the contentRepository to return the thread when save is called
        when(contentRepository.save(thread)).thenReturn((Thread) (Content) thread);

        // Call createThread
        Thread createdThread = threadService.createThread(thread);

        // Assert that the created thread is the same as the original thread
        assertNotNull(createdThread);
        assertEquals(thread, createdThread);
    }

    @Test
    public void testUpdateThread() {
        // Create a thread
        Thread thread = new Thread("Test thread title", "Test thread description");
    
        // Mock the contentRepository to return the thread when findById is called
        when(contentRepository.findById(thread.getId())).thenReturn(Optional.of((Content) thread));
    
        // Create an updateThread with a new title and description
        Thread updateThread = new Thread("Updated thread title", "Updated thread description");
    
        // Mock the contentRepository to return the updated thread when save is called
        when(contentRepository.save(any(Thread.class))).thenAnswer(i -> i.getArguments()[0]);
    
        // Call updateThread
        Thread updatedThread = threadService.updateThread(thread.getId(), updateThread);
    
        // Assert that the updated thread has the new title and description
        assertNotNull(updatedThread);
        assertEquals(updateThread.getTitle(), updatedThread.getTitle());
        assertEquals(updateThread.getDescription(), updatedThread.getDescription());
    }

    @Test
    public void testGetThread() {
        // Create a thread
        Thread thread = new Thread("Test thread title", "Test thread description");
    
        // Mock the contentRepository to return the thread when findById is called
        when(contentRepository.findById(thread.getId())).thenReturn(Optional.of((Content) thread));
    
        // Call getThread
        Thread retrievedThread = threadService.getThread(thread.getId());
    
        // Assert that the retrieved thread is the same as the original thread
        assertNotNull(retrievedThread);
        assertEquals(thread, retrievedThread);
    }

    @Test
    public void testSearchThreads() {
        // Create a list of threads with titles containing a specific text
        List<Thread> threads = new ArrayList<>();
        String searchText = "Test thread";
        for (int i = 0; i < 3; i++) {
            Thread thread = new Thread(searchText + " " + i, "Test thread description");
            threads.add(thread);
        }
    
        // Mock the contentRepository to return the threads when findByContentContaining is called
        when(contentRepository.findByContentContaining(searchText)).thenReturn(new ArrayList<Content>(threads));
    
        // Call searchThreads
        List<Thread> retrievedThreads = threadService.searchThreads(searchText);
    
        // Assert that the retrieved threads are the same as the original threads
        assertNotNull(retrievedThreads);
        assertEquals(threads, retrievedThreads);
    }

    @Test
    public void testGetPagedThreadsByUser() {
        // Create a user
        User user = new User("testUser", Role.USER);
    
        // Create a list of threads for the user
        List<Content> threads = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Thread thread = new Thread("Test thread " + i, "Test thread description");
            thread.setUser(user);
            threads.add(thread);
        }
    
        // Create a Page of threads
        Pageable pageable = PageRequest.of(0, 3);
        Page<Content> threadPage = new PageImpl<>(threads, pageable, threads.size());
    
        // Mock the contentRepository to return the Page of threads when findByUserId is called
        when(contentRepository.findByUserId(user.getId(), pageable)).thenReturn(threadPage);
    
        // Call getPagedThreadsByUser
        Page<Thread> retrievedThreadPage = threadService.getPagedThreadsByUser(user.getId(), pageable);
    
        // Assert that the retrieved Page of threads is the same as the original Page of threads
        assertNotNull(retrievedThreadPage);
        assertEquals(threadPage.getContent(), retrievedThreadPage.getContent());
    }

    @Test
    public void testGetThreadsByCreatedAtBetween() {
        // Create a start and end LocalDateTime
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
    
        // Create a list of threads with createdAt timestamps between start and end
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Thread thread = new Thread("Test thread " + i, "Test thread description");
            thread.setCreatedAt(start.plusHours(i));
            threads.add(thread);
        }
    
        // Mock the contentRepository to return the threads when findByCreatedAtBetween is called
        when(contentRepository.findByCreatedAtBetween(start, end)).thenReturn(new ArrayList<Content>(threads));
    
        // Call getThreadsByCreatedAtBetween
        List<Thread> retrievedThreads = threadService.getThreadsByCreatedAtBetween(start, end);
    
        // Assert that the retrieved threads are the same as the original threads
        assertNotNull(retrievedThreads);
        assertEquals(threads, retrievedThreads);
    }

    @Test
    public void testGetThreadsByContentContaining() {
        // Create a list of threads with content containing a specific text
        List<Thread> threads = new ArrayList<>();
        String searchText = "Test thread";
        for (int i = 0; i < 3; i++) {
            Thread thread = new Thread(searchText + " " + i, "Test thread description");
            threads.add(thread);
        }
    
        // Mock the contentRepository to return the threads when findByContentContaining is called
        when(contentRepository.findByContentContaining(searchText)).thenReturn(new ArrayList<Content>(threads));
    
        // Call getThreadsByContentContaining
        List<Thread> retrievedThreads = threadService.getThreadsByContentContaining(searchText);
    
        // Assert that the retrieved threads are the same as the original threads
        assertNotNull(retrievedThreads);
        assertEquals(threads, retrievedThreads);
    }

    @Test
    public void testGetThreadsByTitle() {
        // Create a list of threads with titles containing a specific text
        List<Thread> threads = new ArrayList<>();
        String titleText = "Test thread";
        for (int i = 0; i < 3; i++) {
            Thread thread = new Thread(titleText + " " + i, "Test thread description");
            threads.add(thread);
        }
    
        // Mock the threadRepository to return the threads when findByTitleContaining is called
        when(threadRepository.findByTitleContaining(titleText)).thenReturn(threads);
    
        // Call getThreadsByTitle
        List<Thread> retrievedThreads = threadService.getThreadsByTitle(titleText);
    
        // Assert that the retrieved threads are the same as the original threads
        assertNotNull(retrievedThreads);
        assertEquals(threads, retrievedThreads);
    }
    
    @Test
    public void testGetThreadsByTitleNull() {
        // Call getThreadsByTitle with null
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            threadService.getThreadsByTitle(null);
        });
    
        // Assert that an IllegalArgumentException is thrown
        assertEquals("Title cannot be null", exception.getMessage());
    }

    @Test
    public void testGetThreadsByDescription() {
        // Create a list of threads with descriptions containing a specific text
        List<Thread> threads = new ArrayList<>();
        String descriptionText = "Test thread description";
        for (int i = 0; i < 3; i++) {
            Thread thread = new Thread("Test thread " + i, descriptionText + " " + i);
            threads.add(thread);
        }
    
        // Mock the threadRepository to return the threads when findByDescriptionContaining is called
        when(threadRepository.findByDescriptionContaining(descriptionText)).thenReturn(threads);
    
        // Call getThreadsByDescription
        List<Thread> retrievedThreads = threadService.getThreadsByDescription(descriptionText);
    
        // Assert that the retrieved threads are the same as the original threads
        assertNotNull(retrievedThreads);
        assertEquals(threads, retrievedThreads);
    }
    
    @Test
    public void testGetThreadsByDescriptionNull() {
        // Call getThreadsByDescription with null
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            threadService.getThreadsByDescription(null);
        });
    
        // Assert that an IllegalArgumentException is thrown
        assertEquals("Description cannot be null", exception.getMessage());
    }

    @Test
    public void testGetRecentThreads() {
        // Create a list of threads
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread("Test thread " + i, "Test thread description");
            threads.add(thread);
        }
    
        // Create a Pageable object
        Pageable pageable = PageRequest.of(0, 10);
    
        // Mock the threadRepository to return the threads when findRecentThreads is called
        when(threadRepository.findRecentThreads(pageable)).thenReturn(threads);
    
        // Call getRecentThreads
        List<Thread> retrievedThreads = threadService.getRecentThreads();
    
        // Assert that the retrieved threads are the same as the original threads
        assertNotNull(retrievedThreads);
        assertEquals(threads, retrievedThreads);
    }

    @Test
    public void testDeleteThread() {
        // Create a test user
        User user = new User("testUser", Role.ADMIN);

        // Create a test thread
        Thread thread = new Thread("Test thread title", "Test thread description");
        thread.setId(1L);

        // Mock the contentRepository to return the thread
        when(contentRepository.findById(thread.getId())).thenReturn(Optional.of(thread));

        // Call the deleteThread method
        threadService.deleteThread(thread.getId(), user);

        // Verify that the contentRepository was called with the correct arguments
        verify(contentRepository).delete(thread);
    }

    @Test
    public void testListThreads() {

        // Create threads
        Thread thread1 = new Thread("Test title 1", "Test description 1");
        Thread thread2 = new Thread("Test title 2", "Test description 2");
        Thread thread3 = new Thread("Test title 3", "Test description 3");

        // Save the threads
        when(contentRepository.save(thread1)).thenReturn(thread1);
        when(contentRepository.save(thread2)).thenReturn(thread2);
        when(contentRepository.save(thread3)).thenReturn(thread3);

        // Mock the contentRepository to return the threads when findAll is called
        when(contentRepository.findAll()).thenReturn(Arrays.asList(thread1, thread2, thread3));

        // Call listThreads
        List<Thread> returnedThreads = threadService.listThreads();

        // Assert that the returned threads are the same as the original threads
        assertNotNull(returnedThreads);
        assertEquals(3, returnedThreads.size());
        assertTrue(returnedThreads.containsAll(Arrays.asList(thread1, thread2, thread3)));

        // Verify that the findAll method was called
        verify(contentRepository, times(1)).findAll();
    }

    @Test
    public void testHandleThreadVote() {
        // Create a user
        User user = new User("testUser", Role.USER);
    
        // Create a thread
        Thread thread = new Thread("Test thread title", "Test thread description");
    
        // Mock the userRepository to return the user when findById is called with 1L
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    
        // Mock the contentRepository to return the thread when findById is called with 1L
        when(contentRepository.findById(1L)).thenReturn(Optional.of(thread));
    
        // Mock the voteRepository to return Optional.empty() when findByUserAndContent is called
        when(voteRepository.findByUserAndContent(user, thread)).thenReturn(Optional.empty());
    
        // Call handleThreadVote
        threadService.handleThreadVote(1L, 1L, true);
    
        // Verify that the correct methods were called on the mock repositories
        verify(userRepository, times(1)).findById(1L);
        verify(contentRepository, times(1)).findById(1L);
        verify(voteRepository, times(1)).findByUserAndContent(user, thread);
        verify(contentRepository, times(1)).save(any(Content.class));
    }
    
}
