package com.LessonLab.forum.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.LessonLab.forum.Models.Content;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Models.Vote;
import com.LessonLab.forum.Repositories.ContentRepository;
import com.LessonLab.forum.Repositories.UserRepository;
import com.LessonLab.forum.Repositories.VoteRepository;

@Service
public abstract class ContentService<T extends Content> {
    
    @Autowired
    private ContentRepository<T> contentRepository; 
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private ConfigurationService configurationService;

    @Transactional
    public T addContent(T content) {
        return contentRepository.save(content);
    }

    @Transactional
    public T updateContent(Long id, String newContent) {
        T content = contentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Content not found with ID: " + id));
        content.setContent(newContent);
        return contentRepository.save(content);
    }

    public T getContent(Long id) {
        return contentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Content not found with ID: " + id));
    }

    public List<T> searchContent(String searchText) {
        return contentRepository.findByContentContaining(searchText);
    }
    
    public Page<T> getPagedContentByUser(Long userId, Pageable pageable) {
        return contentRepository.findByUserId(userId, pageable);
    }

    @Transactional
    public void deleteContent(Long contentId) {
        T content = getContent(contentId);
        if (!canDeleteContent(content)) {
            throw new SecurityException("You do not have permission to delete this content");
        }
        contentRepository.delete(content);
        logDeletionEvent(content);
    }

    protected abstract boolean canDeleteContent(T content);

    protected void logDeletionEvent(T content) {
        // Implementation of logging the deletion
    }

      public List<T> listContent() {
        return contentRepository.findAll();
    }

    @Transactional
    public void handleVote(Long contentId, Long userId, boolean isUpVote) {
        User user = userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));
        T content = getContent(contentId);
        Vote existingVote = voteRepository.findByUserAndContent(user, content).orElse(null);
    
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
            contentRepository.save(content);
        } else {
            content.downVote();
            if (content.checkThreshold(configurationService.getVoteThreshold())) { 
               /*  notifyAdmins(content);*/
               System.out.println("Threshold reached for content ID: " + content.getId());
            }
            contentRepository.save(content);
        }
    }
    
  /*   public void notifyAdmins(T content) {
        List<User> adminsAndMods = userRepository.findByRoleIn(Arrays.asList(Role.ADMIN, Role.MODERATOR));
        notificationService.notifyUsers(adminsAndMods, "Threshold reached for content ID: " + content.getId());
    }*/
}
