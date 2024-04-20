package com.LessonLab.forum.Services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.data.domain.Page;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.LessonLab.forum.Models.Content;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Models.Vote;
import com.LessonLab.forum.Models.Thread;
import com.LessonLab.forum.Models.Enums.Permission;
import com.LessonLab.forum.Models.Enums.Role;
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
    public T addContent(T content, User user) {
        if (content == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }
        checkPermission(user, Permission.WRITE_POST);
        return contentRepository.save(content);
    }

    @Transactional
    public T updateContent(Long id, String newContent, User user) {
        T content = contentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Content not found with ID: " + id));
        checkPermission(user, Permission.WRITE_POST);  
        content.setContent(newContent);  
        return contentRepository.save(content);  
    }

    public T getContent(Long id) {
        return contentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Content not found with ID: " + id));
    }

    public List<T> searchContent(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            throw new IllegalArgumentException("Search text must not be empty");
        }
        return contentRepository.findByContentContaining(searchText);
    }
    
    public Page<T> getPagedContentByUser(Long userId, Pageable pageable) {
        return contentRepository.findByUserId(userId, pageable);
    }

    @Transactional
    public void deleteContent(Long contentId, User user) {
        T content = getContent(contentId);
        
        if (!(user.getRole().equals(Role.ADMIN) || 
            user.getRole().equals(Role.MODERATOR) ||
            (content.getUser().equals(user) && canOriginalPosterDelete(content)))) {
            throw new SecurityException("You do not have permission to delete this content");
        }

        contentRepository.delete(content);
        logDeletionEvent(content);
    }

    protected boolean canOriginalPosterDelete(T content) {
        // Assuming `getNumberOfRepliesNotByPoster()` returns the count of replies not made by the original poster
        return content.getNumberOfRepliesNotByPoster() == 0;
    }

    protected void logDeletionEvent(T content, Thread thread) {
        // Fetch the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (authentication != null && authentication.getPrincipal() instanceof UserDetails) ?
                          ((UserDetails)authentication.getPrincipal()).getUsername() : "Unknown";
    
        String logMessage = String.format("Content ID %d, titled '%s', was deleted by user '%s' at %s",
                                          content.getId(),
                                          thread.getTitle(), 
                                          username,
                                          LocalDateTime.now());
    
        // Update to use a logging framework like SLF4J instead of System.out
        System.out.println(logMessage);
    }

    
    protected void checkPermission(User user, Permission requiredPermission) {
        if (!user.getRole().getPermissions().contains(requiredPermission)) {
            throw new SecurityException("You do not have permission to perform this action");
        }
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
