package com.LessonLab.forum.Services;

import com.LessonLab.forum.Models.Content;
import com.LessonLab.forum.Models.Thread;
import com.LessonLab.forum.Models.ThreadDTO;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Repositories.ThreadRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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

    /*
     * @Transactional
     * public Thread createThread(Thread thread) {
     * if (thread == null) {
     * throw new IllegalArgumentException("Thread cannot be null");
     * }
     * return (Thread) contentRepository.save(thread);
     * }
     */

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

    /*
     * public Thread getThread(Long id) {
     * return (Thread) getContent(id);
     * }
     */

    /*
     * public List<Thread> searchThreads(String searchText) {
     * List<Content> contents = searchContent(searchText);
     * return contents.stream().map(content -> (Thread)
     * content).collect(Collectors.toList());
     * }
     */

    /*
     * public Page<Thread> getPagedThreadsByUser(Long userId, Pageable pageable) {
     * Page<Content> contents = getPagedContentByUser(userId, pageable);
     * return new PageImpl<>(contents.getContent().stream().map(content -> (Thread)
     * content).collect(Collectors.toList()), pageable,
     * contents.getTotalElements());
     * }
     */

    /*
     * public List<Thread> getThreadsByCreatedAtBetween(LocalDateTime start,
     * LocalDateTime end) {
     * List<Content> contents = getContentsByCreatedAtBetween(start, end);
     * return contents.stream().map(content -> (Thread)
     * content).collect(Collectors.toList());
     * }
     */

    /*
     * public List<Thread> getThreadsByContentContaining(String text) {
     * List<Content> contents = getContentsByContentContaining(text);
     * return contents.stream().map(content -> (Thread)
     * content).collect(Collectors.toList());
     * }
     */

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

    /*
     * public List<Thread> getRecentThreads() {
     * try {
     * Pageable pageable = PageRequest.of(0, 10); // Get the first 10 recent threads
     * return threadRepository.findRecentThreads(pageable);
     * } catch (Exception e) {
     * // Log the exception and rethrow it
     * System.err.println("Error getting recent threads: " + e.getMessage());
     * throw e;
     * }
     * }
     */

    public Page<Thread> getRecentContents(Pageable pageable) {
        Page<Content> contents = super.contentRepository.findRecentContents(pageable);
        List<Thread> threads = contents.stream()
                .filter(content -> content instanceof Thread)
                .map(content -> (Thread) content)
                .collect(Collectors.toList());
        return new PageImpl<>(threads, pageable, threads.size());
    }

    /*
     * @Transactional
     * public void deleteThread(Long threadId, User user) {
     * super.deleteContent(threadId, user);
     * }
     */

    /*
     * public List<Thread> listThreads() {
     * List<Content> contents = super.listContent();
     * return contents.stream()
     * .filter(content -> content instanceof Thread)
     * .map(content -> (Thread) content)
     * .collect(Collectors.toList());
     * }
     */

    /*
     * public void handleThreadVote(Long threadId, Long userId, boolean isUpVote) {
     * super.handleVote(threadId, userId, isUpVote);
     * }
     */

    public static ThreadDTO createWithThreadId(Long newThreadId) {
        ThreadDTO thread = new ThreadDTO();
        thread.setThreadId(newThreadId);
        return thread;
    }

}
