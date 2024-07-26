package com.LessonLab.forum.ServiceTests;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.LessonLab.forum.Models.Content;
import com.LessonLab.forum.Models.Post;
import com.LessonLab.forum.Models.Role;
import com.LessonLab.forum.Models.Thread;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Repositories.ContentRepository;
import com.LessonLab.forum.Repositories.ThreadRepository;
import com.LessonLab.forum.Repositories.UserRepository;
import com.LessonLab.forum.Repositories.VoteRepository;
import com.LessonLab.forum.Services.ContentService;
import com.LessonLab.forum.Services.ThreadService;
import com.LessonLab.forum.Services.UserService;

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

    @InjectMocks
    private UserService userService;

    private AutoCloseable closeable;

    private User testUser;

    @BeforeEach
    public void setup() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Create a mock for UserService
        UserService userService = Mockito.mock(UserService.class);

        // Initialize testUser
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setPassword("password");
        testUser.setName("testName");
        testUser.setRoles(new HashSet<>(Collections.singleton(new Role(1L, "ADMIN"))));

        when(userService.getCurrentUser()).thenReturn(testUser);
    }

    @AfterEach
    public void closeService() throws Exception {
        closeable.close();
    }

    @Test
    public void test_createThread_withValidInputs() {
        // Arrange
        Thread thread = new Thread();
        thread.setTitle("Test Title");
        thread.setDescription("Test Description");

        Mockito.when(userService.getCurrentUser()).thenReturn(testUser); 

        // Act
        Thread result = threadService.createThread(thread.getTitle(), thread.getDescription());

        // Assert
        Assert.assertNotNull(result);
        Assert.assertEquals(thread.getTitle(), result.getTitle());
        Assert.assertEquals(thread.getDescription(), result.getDescription());
        Assert.assertEquals(testUser, result.getUser());
    }

    @Test
    public void testUpdateThread() {
        // Arrange
        UserService mockUserService = Mockito.mock(UserService.class);
        ThreadService threadService = new ThreadService(mockUserService);
        Thread originalThread = new Thread("Original Title", "Original Content");
        Thread updatedThread = new Thread("Updated Title", "Updated Content");

        // Act
        Long threadId = originalThread.getContentId(); // get the ID of the original thread
        threadService.updateThread(threadId, updatedThread); // update the thread

        // Assert
        Thread resultThread = (Thread) threadService.getContentById(threadId, "thread"); // get the updated thread
        assertEquals("Updated Title", resultThread.getTitle());
        assertEquals("Updated Content", resultThread.getContent());
    }
    /*
     * @Test
     * public void testGetThread() {
     * // Create a thread
     * Thread thread = new Thread("Test thread title", "Test thread description");
     * 
     * // Mock the contentRepository to return the thread when findById is called
     * when(contentRepository.findById(thread.getContentId())).thenReturn(Optional.
     * of((Content) thread));
     * 
     * // Call getThread
     * Thread retrievedThread = threadService.getThread(thread.getContentId());
     * 
     * // Assert that the retrieved thread is the same as the original thread
     * assertNotNull(retrievedThread);
     * assertEquals(thread, retrievedThread);
     * }
     */

    /*
     * @Test
     * public void testSearchThreads() {
     * // Create a list of threads with titles containing a specific text
     * List<Thread> threads = new ArrayList<>();
     * String searchText = "Test thread";
     * for (int i = 0; i < 3; i++) {
     * Thread thread = new Thread(searchText + " " + i, "Test thread description");
     * threads.add(thread);
     * }
     * 
     * // Mock the contentRepository to return the threads when
     * findByContentContaining is called
     * when(contentRepository.findByContentContaining(searchText)).thenReturn(new
     * ArrayList<Content>(threads));
     * 
     * // Call searchThreads
     * List<Thread> retrievedThreads = threadService.searchThreads(searchText);
     * 
     * // Assert that the retrieved threads are the same as the original threads
     * assertNotNull(retrievedThreads);
     * assertEquals(threads, retrievedThreads);
     * }
     */

    /*
     * @Test
     * public void testGetPagedThreadsByUser() {
     * // Create a user
     * User user = new User("testUser", Role.USER);
     * 
     * // Create a list of threads for the user
     * List<Content> threads = new ArrayList<>();
     * for (int i = 0; i < 3; i++) {
     * Thread thread = new Thread("Test thread " + i, "Test thread description");
     * thread.setUser(user);
     * threads.add(thread);
     * }
     * 
     * // Create a Page of threads
     * Pageable pageable = PageRequest.of(0, 3);
     * Page<Content> threadPage = new PageImpl<>(threads, pageable, threads.size());
     * 
     * // Mock the contentRepository to return the Page of threads when findByUserId
     * is called
     * when(contentRepository.findByUserUserId(user.getUserId(),
     * pageable)).thenReturn(threadPage);
     * 
     * // Call getPagedThreadsByUser
     * Page<Thread> retrievedThreadPage =
     * threadService.getPagedThreadsByUser(user.getUserId(), pageable);
     * 
     * // Assert that the retrieved Page of threads is the same as the original Page
     * of threads
     * assertNotNull(retrievedThreadPage);
     * assertEquals(threadPage.getContent(), retrievedThreadPage.getContent());
     * }
     */

    /*
     * @Test
     * public void testGetThreadsByCreatedAtBetween() {
     * // Create a start and end LocalDateTime
     * LocalDateTime start = LocalDateTime.now().minusDays(1);
     * LocalDateTime end = LocalDateTime.now();
     * 
     * // Create a list of threads with createdAt timestamps between start and end
     * List<Thread> threads = new ArrayList<>();
     * for (int i = 0; i < 3; i++) {
     * Thread thread = new Thread("Test thread " + i, "Test thread description");
     * thread.setCreatedAt(start.plusHours(i));
     * threads.add(thread);
     * }
     * 
     * // Mock the contentRepository to return the threads when
     * findByCreatedAtBetween is called
     * when(contentRepository.findByCreatedAtBetween(start, end)).thenReturn(new
     * ArrayList<Content>(threads));
     * 
     * // Call getThreadsByCreatedAtBetween
     * List<Thread> retrievedThreads =
     * threadService.getThreadsByCreatedAtBetween(start, end);
     * 
     * // Assert that the retrieved threads are the same as the original threads
     * assertNotNull(retrievedThreads);
     * assertEquals(threads, retrievedThreads);
     * }
     */
    /*
     * @Test
     * public void testGetThreadsByContentContaining() {
     * // Create a list of threads with content containing a specific text
     * List<Thread> threads = new ArrayList<>();
     * String searchText = "Test thread";
     * for (int i = 0; i < 3; i++) {
     * Thread thread = new Thread(searchText + " " + i, "Test thread description");
     * threads.add(thread);
     * }
     * 
     * // Mock the contentRepository to return the threads when
     * findByContentContaining is called
     * when(contentRepository.findByContentContaining(searchText)).thenReturn(new
     * ArrayList<Content>(threads));
     * 
     * // Call getThreadsByContentContaining
     * List<Thread> retrievedThreads =
     * threadService.getThreadsByContentContaining(searchText);
     * 
     * // Assert that the retrieved threads are the same as the original threads
     * assertNotNull(retrievedThreads);
     * assertEquals(threads, retrievedThreads);
     * }
     */

    @Test
    public void testGetThreadsByTitle() {
        // Create a list of threads with titles containing a specific text
        List<Thread> threads = new ArrayList<>();
        String titleText = "Test thread";
        for (int i = 0; i < 3; i++) {
            Thread thread = new Thread(titleText + " " + i, "Test thread description");
            threads.add(thread);
        }

        // Mock the threadRepository to return the threads when findByTitleContaining is
        // called
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

        // Mock the threadRepository to return the threads when
        // findByDescriptionContaining is called
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

    /*
     * @Test
     * public void testGetRecentThreads() {
     * // Create a list of threads
     * List<Thread> threads = new ArrayList<>();
     * for (int i = 0; i < 10; i++) {
     * Thread thread = new Thread("Test thread " + i, "Test thread description");
     * threads.add(thread);
     * }
     * 
     * // Create a Pageable object
     * Pageable pageable = PageRequest.of(0, 10);
     * 
     * // Mock the threadRepository to return the threads when findRecentThreads is
     * called
     * when(threadRepository.findRecentThreads(pageable)).thenReturn(threads);
     * 
     * // Call getRecentThreads
     * List<Thread> retrievedThreads = threadService.getRecentThreads();
     * 
     * // Assert that the retrieved threads are the same as the original threads
     * assertNotNull(retrievedThreads);
     * assertEquals(threads, retrievedThreads);
     * }
     */

    @Test
    public void testGetRecentThreadContents() {
        Pageable pageable = PageRequest.of(0, 10);
        Thread thread1 = new Thread("Thread 1", "Description 1");
        Thread thread2 = new Thread("Thread 2", "Description 2");
        Post post = new Post(); // Assume Post is another subclass of Content
        List<Content> mixedContents = Arrays.asList(thread1, post, thread2);

        // Mocking the generic ContentRepository to return mixed Content types
        when(contentRepository.findRecentContents(pageable)).thenReturn(new PageImpl<>(mixedContents));

        // Fetching using ThreadService
        Page<Thread> result = threadService.getRecentContents(pageable);

        assertNotNull(result);
        assertEquals(2, result.getNumberOfElements());
        assertTrue(result.getContent().stream().allMatch(c -> c instanceof Thread));

        Thread resultThread1 = result.getContent().get(0);
        Thread resultThread2 = result.getContent().get(1);

        assertEquals("Thread 1", resultThread1.getTitle());
        assertEquals("Thread 2", resultThread2.getTitle());
    }

    

}
