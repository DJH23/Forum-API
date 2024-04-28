package com.LessonLab.forum.Services;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.data.domain.Page;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.LessonLab.forum.Models.Comment;
import com.LessonLab.forum.Models.Content;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Models.Vote;
import com.LessonLab.forum.Models.Thread;
import com.LessonLab.forum.Models.Post;
import com.LessonLab.forum.Models.Enums.Role;
import com.LessonLab.forum.Repositories.CommentRepository;
import com.LessonLab.forum.Repositories.ContentRepository;
import com.LessonLab.forum.Repositories.UserRepository;
import com.LessonLab.forum.Repositories.VoteRepository;

@Service
public abstract class ContentService {

    @Autowired
    protected ContentRepository contentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private ConfigurationService configurationService;
    @Autowired
    private CommentRepository commentRepository;

    @Transactional
    public Content addContent(Content content, User user) {
        if (content == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }
        checkRole(user, Role.ADMIN, Role.MODERATOR, Role.USER);
        return contentRepository.save(content);
    }

    @Transactional
    public Content updateContent(Long id, String newContent, User user) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Content not found with ID: " + id));
        checkRole(user, Role.ADMIN, Role.MODERATOR, Role.USER);
        content.setContent(newContent);
        return contentRepository.save(content);
    }

    protected void checkRole(User user, Role... roles) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (!Arrays.asList(roles).contains(user.getRole())) {
            throw new IllegalArgumentException(
                    "User with role " + user.getRole() + " does not have permission to perform this action");
        }
    }

    public Content getContent(Long id) {
        return contentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Content not found with ID: " + id));
    }

    public List<Content> searchContent(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            throw new IllegalArgumentException("Search text must not be empty");
        }
        return contentRepository.findByContentContaining(searchText);
    }

    public Page<Content> getPagedContentByUser(Long userId, Pageable pageable) {
        try {
            return contentRepository.findByUserUserId(userId, pageable);
        } catch (Exception e) {
            throw new RuntimeException("Error getting paged content by user", e);
        }
    }

    public List<Content> getContentsByCreatedAtBetween(LocalDateTime start, LocalDateTime end) {
        try {
            return contentRepository.findByCreatedAtBetween(start, end);
        } catch (Exception e) {
            throw new RuntimeException("Error getting contents by created at between", e);
        }
    }

    public List<Content> getContentsByContentContaining(String text) {
        try {
            return contentRepository.findByContentContaining(text);
        } catch (Exception e) {
            throw new RuntimeException("Error getting contents by content containing", e);
        }
    }

    @Transactional
    public void deleteContent(Long contentId, User user) {
        Content content = getContent(contentId);

        if (!hasPermissionToDelete(content, user)) {
            throw new SecurityException("You do not have permission to delete this content");
        }

        contentRepository.delete(content);
        logDeletionEvent(content);
    }

    protected boolean hasPermissionToDelete(Content content, User user) {
        return (user.getRole().equals(Role.ADMIN) ||
                user.getRole().equals(Role.MODERATOR) ||
                (content.getUser().equals(user) && canOriginalPosterDelete(content)));
    }

    protected boolean canOriginalPosterDelete(Content content) {
        if (content instanceof Post) {
            Post post = (Post) content;
            return getCommentCountExcludingPoster(post) == 0;
        }
        return true; // Assumes that other types of Content can always be deleted by the original
                     // poster.
    }

    public long getCommentCountExcludingPoster(Post post) {
        return commentRepository.countByPostAndUserNot(post, post.getUser());
    }

    protected void logDeletionEvent(Content content) {
        // Fetch the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (authentication != null && authentication.getPrincipal() instanceof UserDetails)
                ? ((UserDetails) authentication.getPrincipal()).getUsername()
                : "Unknown";

        // Assuming the content's title or similar information can be generalized or
        // accessed generically
        String contentDetail = getContentDetail(content);

        String logMessage = String.format("Content ID %d, with detail '%s', was deleted by user '%s' at %s",
                content.getContentId(),
                contentDetail,
                username,
                LocalDateTime.now());

        // Consider using a proper logging framework like SLF4J instead of System.out
        System.out.println(logMessage);
    }

    private String getContentDetail(Content content) {
        if (content instanceof Post) {
            return ((Post) content).getContent();

        } else if (content instanceof Comment) {
            return "Comment Content: " + content.getContent();

        } else if (content instanceof Thread) {
            return ((Thread) content).getTitle();
        }

        return "Generic Content"; // Fallback for other or undefined content types
    }

    public List<Content> listContent() {
        try {
            return contentRepository.findAll();
        } catch (Exception e) {
            // Log the exception and rethrow it
            System.err.println("Error listing content: " + e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void handleVote(Long contentId, Long userId, boolean isUpVote) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            Content content = getContent(contentId);
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
                    /* notifyAdmins(content); */
                    System.out.println("Threshold reached for content ID: " + content.getContentId());
                }
                contentRepository.save(content);
            }
        } catch (Exception e) {
            // Log the exception and rethrow it
            System.err.println("Error handling vote: " + e.getMessage());
            throw e;
        }
    }

    /*
     * public void notifyAdmins(T content) {
     * List<User> adminsAndMods =
     * userRepository.findByRoleIn(Arrays.asList(Role.ADMIN, Role.MODERATOR));
     * notificationService.notifyUsers(adminsAndMods,
     * "Threshold reached for content ID: " + content.getId());
     * }
     */
}
