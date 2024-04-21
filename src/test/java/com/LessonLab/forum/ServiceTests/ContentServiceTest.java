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

@RunWith(MockitoJUnitRunner.class)
public class ContentServiceTest {

    @Mock
    private ContentRepository contentRepository;

    private ContentService contentService;

    @Before
    public void setUp() {
        contentService = new ContentService(contentRepository) {
            // Implement abstract methods here, if any
        };
    }

    @Test
    public void testAddContent_NullContent_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            contentService.addContent(null, new User());
        });
    }

    @Test
    public void testAddContent_AdminRole_CallsSaveAndReturnsResult() {
        User user = new User();
        user.setRole(Role.ADMIN);
        Content content = Mockito.mock(Content.class);
        Content savedContent = Mockito.mock(Content.class);
        when(contentRepository.save(content)).thenReturn(savedContent);

        Content result = contentService.addContent(content, user);

        verify(contentRepository, times(1)).save(content);
        assertEquals(savedContent, result);
    }

    @Test
    public void testAddContent_ModeratorRole_CallsSaveAndReturnsResult() {
        User user = new User();
        user.setRole(Role.MODERATOR);
        Content content = Mockito.mock(Content.class);
        Content savedContent = Mockito.mock(Content.class);
        when(contentRepository.save(content)).thenReturn(savedContent);

        Content result = contentService.addContent(content, user);

        verify(contentRepository, times(1)).save(content);
        assertEquals(savedContent, result);
    }

    @Test
    public void testAddContent_UserRole_CallsSaveAndReturnsResult() {
        User user = new User();
        user.setRole(Role.USER);
        Content content = Mockito.mock(Content.class);
        Content savedContent = Mockito.mock(Content.class);
        when(contentRepository.save(content)).thenReturn(savedContent);

        Content result = contentService.addContent(content, user);

        verify(contentRepository, times(1)).save(content);
        assertEquals(savedContent, result);
    }
}
