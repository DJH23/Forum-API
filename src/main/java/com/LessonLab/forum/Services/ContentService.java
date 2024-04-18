package com.LessonLab.forum.Services;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.LessonLab.forum.Models.Content;
import com.LessonLab.forum.Models.Role;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Repositories.ContentRepository;
import com.LessonLab.forum.Repositories.UserRepository;

@Service
public class ContentService {

    @Autowired
    private ContentRepository<Content, Long> contentRepository;  
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Content addContent(Content content) {
        if (content == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }
        return contentRepository.save(content);
    }

    @Transactional
    public Content updateContent(Long id, String newContent) {
        if (id == null) {
            throw new IllegalArgumentException("Content ID cannot be null");
        }
        Content content = contentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Content not found with ID: " + id));
        content.setContent(newContent);
        return contentRepository.save(content);
    }

    public Content getContent(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Content ID cannot be null");
        }
        return contentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Content not found with ID: " + id));
    }

    public List<Content> searchContent(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            throw new IllegalArgumentException("Search text must not be empty");
        }
        return contentRepository.findByContentContaining(searchText);
    }
    
    public Page<Content> getPagedContentByUser(Long userId, Pageable pageable) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
        return contentRepository.findByUserId(userId, pageable);
    }

    @Transactional
    public void deleteContent(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Content ID cannot be null");
        }
        contentRepository.deleteById(id);
    }

    public List<Content> listContent() {
        return contentRepository.findAll();
    }

    @Transactional
    public void incrementUpVote(Long contentId) {
        Content content = contentRepository.findById(contentId)
            .orElseThrow(() -> new RuntimeException("Content not found"));
        content.upVote();  // Increment the upvote count
        contentRepository.save(content);  // Save the updated content
    }

    @Transactional
    public void incrementDownVote(Long contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found"));
        content.downVote();  // Increment the downvote count
    
        // Assume ConfigurationService is a service managing dynamic threshold, injected via @Autowired
        int threshold = configurationService.getVoteThreshold();  // Get the dynamically managed threshold
    
        if (content.checkThreshold(threshold)) {  // Check if the downvotes exceed the threshold
            notifyAdmins(content);  // Notify administrators and moderators
        }
        contentRepository.save(content);  // Save the updated content
    }
    
    public void notifyAdmins(Content content) {
        // Fetch admins and moderators
        List<User> adminsAndMods = userRepository.findByRole(Arrays.asList(Role.ADMIN, Role.MODERATOR));
    
        // Sending a notification (assumes a NotificationService is set up and injected)
        notificationService.notifyUsers(adminsAndMods, "Threshold reached for content ID: " + content.getId());
    }

}
