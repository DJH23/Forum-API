package com.LessonLab.forum.Services;

import com.LessonLab.forum.Models.Thread;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Repositories.ThreadRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ThreadService extends ContentService<Thread>{

    @Autowired
    private ThreadRepository threadRepository;

    @Transactional
    public Thread createThread(Thread thread) {
        if (thread == null) {
            throw new IllegalArgumentException("Thread cannot be null");
        }
        return threadRepository.save(thread);
    }

    @Transactional
    public Thread updateThread(Long threadId, Thread updateThread) {
        Thread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new IllegalArgumentException("Thread not found with ID: " + threadId));
        thread.setTitle(updateThread.getTitle());
        thread.setDescription(updateThread.getDescription());
        // Update other fields as necessary
        return threadRepository.save(thread);
    }

    public Thread getThread(Long threadId) {
        return threadRepository.findById(threadId)
                .orElseThrow(() -> new IllegalArgumentException("Thread not found with ID: " + threadId));
    }

    public List<Thread> getRecentThreads() {
        Pageable pageable = PageRequest.of(0, 10); // Get the first 10 recent threads
        return threadRepository.findRecentThreads(pageable);
    }

    @Override
    @Transactional
    public void deleteContent(Long threadId, User user) {
        threadRepository.findById(threadId)
                .orElseThrow(() -> new RuntimeException("Thread not found with ID: " + threadId));
        
        super.deleteContent(threadId, user);
    }

}


