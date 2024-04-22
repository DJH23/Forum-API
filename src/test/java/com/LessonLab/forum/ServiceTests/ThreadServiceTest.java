package com.LessonLab.forum.ServiceTests;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.LessonLab.forum.Models.Content;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Models.Enums.Role;
import com.LessonLab.forum.Repositories.ContentRepository;
import com.LessonLab.forum.Services.ContentService;
import com.LessonLab.forum.Services.ThreadService;

@RunWith(MockitoJUnitRunner.class)
public class ThreadServiceTest {

    @Mock
    private ContentRepository contentRepository;

    private ThreadService threadService;

    @Before
    public void setUp() {
        threadService = new ThreadService();
        threadService.setContentRepository(contentRepository);
    }
    
    @Test
    public void testGetThread_ValidId_ReturnsThread() {
        // Arrange
        Long id = 1L;
        Thread expectedThread = new Thread();
        when(contentService.getContent(id)).thenReturn(expectedThread);
    
        // Act
        Thread actualThread = threadService.getThread(id);
    
        // Assert
        verify(contentService, times(1)).getContent(id);
        assertEquals(expectedThread, actualThread);
    }
}
