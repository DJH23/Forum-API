package com.LessonLab.forum.Services;

import com.LessonLab.forum.Models.Content;
import com.LessonLab.forum.Models.Thread;
import com.LessonLab.forum.Models.ThreadDTO;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Repositories.ThreadRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ThreadService extends ContentService {

    @Autowired
    private ThreadRepository threadRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public Thread createThread(Long userId, String threadTitle, String threadDescription) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }

        User user = userService.getUser(userId);
        Thread thread = new Thread();
        thread.setUser(user);
        thread.setTitle(threadTitle);
        thread.setDescription(threadDescription);

        return (Thread) contentRepository.save(thread);
    }

    @Transactional
    public Thread updateThread(Long threadId, Thread updateThread) {
        Thread thread = (Thread) contentRepository.findById(threadId)
                .orElseThrow(() -> new IllegalArgumentException("Thread not found with ID: " + threadId));
        thread.setTitle(updateThread.getTitle());
        thread.setDescription(updateThread.getDescription());
        // Update other fields as necessary
        return (Thread) contentRepository.save(thread);
    }

    public List<Thread> getThreadsByTitle(String title) {
        try {
            if (title == null) {
                throw new IllegalArgumentException("Title cannot be null");
            }
            return threadRepository.findByTitleContaining(title);
        } catch (Exception e) {
            // Log the exception and rethrow it
            System.err.println("Error getting threads by title: " + e.getMessage());
            throw e;
        }
    }

    public List<Thread> getThreadsByDescription(String description) {
        try {
            if (description == null) {
                throw new IllegalArgumentException("Description cannot be null");
            }
            return threadRepository.findByDescriptionContaining(description);
        } catch (Exception e) {
            // Log the exception and rethrow it
            System.err.println("Error getting threads by description: " + e.getMessage());
            throw e;
        }
    }

    public Page<Thread> getRecentContents(Pageable pageable) {
        Page<Content> contents = super.contentRepository.findRecentContents(pageable);
        List<Thread> threads = contents.stream()
                .filter(content -> content instanceof Thread)
                .map(content -> (Thread) content)
                .collect(Collectors.toList());
        return new PageImpl<>(threads, pageable, threads.size());
    }

    public static ThreadDTO createWithThreadId(Long newThreadId) {
        ThreadDTO thread = new ThreadDTO();
        thread.setThreadId(newThreadId);
        return thread;
    }

    public Thread getThreadWithId(Long id) {
        Content content = getContentById(id, "thread");
        if (content instanceof Thread) {
            return (Thread) content;
        } else {
            throw new IllegalArgumentException("Content with ID: " + id + " is not a Thread");
        }
    }

    public List<Thread> listContent(boolean includeNested) {
        if (includeNested) {
            return threadRepository.findAllWithPosts();
        } else {
            return threadRepository.findAll();
        }
    }

    public Page<Thread> getPagedThreadsByUser(Long userId, Pageable pageable) {
        return threadRepository.findThreadsByUserId(userId, pageable);
    }

}
