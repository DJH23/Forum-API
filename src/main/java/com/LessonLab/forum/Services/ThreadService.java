package com.LessonLab.forum.Services;

import com.LessonLab.forum.Models.Role;
import com.LessonLab.forum.Models.Thread;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Repositories.ThreadRepository;
import com.LessonLab.forum.Repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ThreadService {

    @Autowired
    private ThreadRepository threadRepository;

    @Autowired
    private UserRepository userRepository; 

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

    public List<Thread> getAllThreads() {
        return threadRepository.findAll();
    }

    public List<Thread> searchThreadsByTitle(String title) {
        return threadRepository.findByTitleContaining(title);
    }

    @Transactional
    public void deleteThread(Long threadId) {
        // Fetch the thread or throw if not found
        Thread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new IllegalArgumentException("Thread not found with ID: " + threadId));

        // Retrieve the currently authenticated user
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Current user not found"));
        
        // Check if the current user is allowed to delete the thread
        if (!canDeleteThread(currentUser, thread)) {
            throw new SecurityException("You do not have permission to delete this thread");
        }

        // Perform deletion
        threadRepository.delete(thread);

        // Optionally log the deletion event for audit
        logDeletionEvent(currentUser, thread);
    }

    // Utility method to determine if a user can delete a specific thread
    private boolean canDeleteThread(User user, Thread thread) {
        // Admins can delete any thread
        if (user.getRole().equals(Role.ADMIN)) {
            return true;
        }
        // Moderators can delete if they manage the category
      /* if (user.getRole().equals(Role.MODERATOR) && user.getManagedCategories().contains(thread.getCategory())) {
            return true;
        }*/  
        // Original posters can delete their own thread if there are no replies
        if (thread.getUser().equals(user) && thread.getPosts().isEmpty()) {
            return true;
        }
        return false;
    }

    // Method to log the deletion event
    private void logDeletionEvent(User user, Thread thread) {
        // Implement logging mechanism here, e.g., using a logging framework or database audit logs
        System.out.println("Thread deleted by user " + user.getUsername() + ": " + thread.getTitle());
    }
}


