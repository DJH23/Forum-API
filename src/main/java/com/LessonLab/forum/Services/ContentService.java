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
import com.LessonLab.forum.Models.Vote;
import com.LessonLab.forum.Repositories.ConcreteContentRepository;
import com.LessonLab.forum.Repositories.UserRepository;
import com.LessonLab.forum.Repositories.VoteRepository;

@Service
public class ContentService {

    @Autowired
    private ConcreteContentRepository contentRepository;  
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private ConfigurationService configurationService;

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
        return contentRepository.findByUserId(userId, null);
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
    public void handleVote(Long contentId, Long userId, boolean isUpVote) {
        User user = userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found"));
        Content content = contentRepository.findById(contentId)
                        .orElseThrow(() -> new IllegalArgumentException("Content with ID " + contentId + " not found"));
    
        Vote existingVote = voteRepository.findByUserAndContent(user, content)
                            .orElse(null);  
    
        if (existingVote != null) {
            throw new IllegalStateException("User has already voted on this content");
        }
    
        Vote vote = new Vote();
        vote.setUser(user);
        vote.setContent(content);
        vote.setUpVote(isUpVote);
        voteRepository.save(vote);
    
        if (isUpVote) {
            content.upVote();
        } else {
            content.downVote();
            if (content.checkThreshold(configurationService.getVoteThreshold())) { 
                notifyAdmins(content);
            }
        }
        contentRepository.save(content);
    }
    
    public void notifyAdmins(Content content) {
        List<User> adminsAndMods = userRepository.findByRoleIn(Arrays.asList(Role.ADMIN, Role.MODERATOR));
        notificationService.notifyUsers(adminsAndMods, "Threshold reached for content ID: " + content.getId());
    }

}
