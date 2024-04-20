package com.LessonLab.forum.Services;

import com.LessonLab.forum.Models.Content;
import com.LessonLab.forum.Models.Thread;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Repositories.ContentRepository;
import com.LessonLab.forum.Repositories.ThreadRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ThreadService {

    @Autowired
    private ThreadRepository threadRepository;
    @Autowired
    private ContentRepository contentRepository;
    @Autowired
    private ContentService contentService;

    @Transactional
    public Thread createThread(Thread thread) {
        if (thread == null) {
            throw new IllegalArgumentException("Thread cannot be null");
        }
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

    public Thread getThread(Long threadId) {
    Content content = contentRepository.findById(threadId)
            .orElseThrow(() -> new IllegalArgumentException("Thread not found with ID: " + threadId));
    return (Thread) content;
}

    public List<Thread> getRecentThreads() {
        Pageable pageable = PageRequest.of(0, 10); // Get the first 10 recent threads
        return threadRepository.findRecentThreads(pageable);
    }

    @Transactional
    public void deleteContent(Long threadId, User user) {
        contentService.deleteContent(threadId, user);
    }

}


