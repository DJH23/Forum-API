package com.LessonLab.forum.Controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.web.exchanges.HttpExchange.Principal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.LessonLab.forum.Models.Comment;
import com.LessonLab.forum.Models.CommentDTO;
import com.LessonLab.forum.Models.Content;
import com.LessonLab.forum.Models.ContentUpdateDTO;
import com.LessonLab.forum.Models.Thread;
import com.LessonLab.forum.Models.Post;
import com.LessonLab.forum.Models.PostDTO;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Models.Enums.Role;
import com.LessonLab.forum.Services.CommentService;
import com.LessonLab.forum.Services.PostService;
import com.LessonLab.forum.Services.ThreadService;
import com.LessonLab.forum.Services.UserService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api/contents")
public class ContentController {

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Autowired
    private ThreadService threadService;

    /*
     * @PostMapping("/addContent/{contentType}")
     * 
     * @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MODERATOR')")
     * public ResponseEntity<?> addContent(@PathVariable String contentType,
     * 
     * @RequestParam(value = "contentToBeAdded", required = true) Object
     * contentToBeAdded) {
     * try {
     * User user = userService.getUser(1L);
     * Content addedContent;
     * 
     * switch (contentType.toLowerCase()) {
     * case "post":
     * addedContent = postService.addContent((Post) contentToBeAdded, user);
     * break;
     * case "comment":
     * addedContent = commentService.addContent((Comment) contentToBeAdded, user);
     * break;
     * default:
     * throw new IllegalArgumentException("Invalid content type: " + contentType);
     * }
     * return new ResponseEntity<>(addedContent, HttpStatus.CREATED);
     * } catch (Exception ex) {
     * return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
     * .body("Error processing request: " + ex.getMessage());
     * }
     * }
     */

    /*
     * @PostMapping("/{contentType}/addContent")
     * public ResponseEntity<?> addContent(@PathVariable String
     * contentType, @RequestBody Object contentDTO) {
     * User user = userService.getUser(1L); // Ensure real user retrieval
     * Content addedContent;
     * 
     * switch (contentType.toLowerCase()) {
     * case "post":
     * addedContent = postService.addContent((PostDTO) contentDTO, user);
     * break;
     * case "comment":
     * addedContent = commentService.addContent((CommentDTO) contentDTO, user);
     * break;
     * default:
     * throw new IllegalArgumentException("Invalid content type: " + contentType);
     * }
     * return new ResponseEntity<>(addedContent, HttpStatus.CREATED);
     * }
     */

    @PutMapping("/{contentType}/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> updateContent(@PathVariable String contentType, @PathVariable Long id,
            @RequestBody ContentUpdateDTO contentUpdate) {
        // User user = getCurrentUser(); // Fetch the current user
        User user = userService.getUser(1L); // This is a placeholder.
        Content updatedContent;
        switch (contentType.toLowerCase()) {
            case "comment":
                updatedContent = commentService.updateContent(id, new ContentUpdateDTO(contentUpdate.getNewContent()),
                        user);
                break;
            case "post":
                updatedContent = postService.updateContent(id, new ContentUpdateDTO(contentUpdate.getNewContent()),
                        user);
                break;
            default:
                throw new IllegalArgumentException("Invalid content type: " + contentType);
        }
        return new ResponseEntity<>(updatedContent, HttpStatus.OK);
    }

    /*
     * @GetMapping("/user/{contentType}/{userId}")
     * public ResponseEntity<?> getPagedContentByUser(@PathVariable String
     * contentType, @PathVariable Long userId,
     * Pageable pageable) {
     * Page<? extends Content> contents;
     * switch (contentType.toLowerCase()) {
     * case "comment":
     * contents = commentService.getPagedContentByUser(userId, pageable);
     * break;
     * case "post":
     * contents = postService.getPagedContentByUser(userId, pageable);
     * break;
     * case "thread":
     * contents = threadService.getPagedContentByUser(userId, pageable);
     * break;
     * default:
     * throw new IllegalArgumentException("Invalid content type: " + contentType);
     * }
     * return new ResponseEntity<>(contents, HttpStatus.OK);
     * }
     */

    @GetMapping("/{contentType}/get-content-by-id/{id}")
    public ResponseEntity<?> getContent(@PathVariable String contentType, @PathVariable Long id) {
        Content content;
        switch (contentType.toLowerCase()) {
            case "comment":
                content = commentService.getContentById(id, contentType);
                break;
            case "post":
                content = postService.getContentById(id, contentType);
                break;
            case "thread":
                content = threadService.getContentById(id, contentType);
                break;
            default:
                throw new IllegalArgumentException("Invalid content type: " + contentType);
        }
        return new ResponseEntity<>(content, HttpStatus.OK);
    }

    @GetMapping("/search/{contentType}")
    public ResponseEntity<?> searchContent(@PathVariable String contentType, @RequestParam String searchText) {
        List<? extends Content> contents;
        switch (contentType.toLowerCase()) {
            case "comment":
                contents = commentService.searchContent(searchText);
                break;
            case "post":
                contents = postService.searchContent(searchText);
                break;
            case "thread":
                contents = threadService.searchContent(searchText);
                break;
            default:
                throw new IllegalArgumentException("Invalid content type: " + contentType);
        }
        return new ResponseEntity<>(contents, HttpStatus.OK);
    }

    @GetMapping("/recent/{contentType}")
    public ResponseEntity<?> getRecentContents(@PathVariable String contentType, Pageable pageable) {
        Page<? extends Content> contents;
        switch (contentType.toLowerCase()) {
            case "comment":
                contents = commentService.getRecentContents(pageable);
                break;
            case "post":
                contents = postService.getRecentContents(pageable);
                break;
            case "thread":
                contents = threadService.getRecentContents(pageable);
                break;
            default:
                throw new IllegalArgumentException("Invalid content type: " + contentType);
        }
        return new ResponseEntity<>(contents, HttpStatus.OK);
    }

    @GetMapping("/user/{contentType}/get-paged-content-by-user/{userId}")
    public ResponseEntity<?> getPagedContentByUser(
            @PathVariable String contentType,
            @PathVariable Long userId,
            Pageable pageable) {

        try {
            switch (contentType.toLowerCase()) {
                case "comment":
                    Page<Comment> comments = commentService.getPagedCommentsByUser(userId, pageable);
                    return ResponseEntity.ok(comments);

                case "post":
                    Page<Post> posts = postService.getPagedPostsByUser(userId, pageable);
                    return ResponseEntity.ok(posts);

                case "thread":
                    Page<Thread> threads = threadService.getPagedThreadsByUser(userId, pageable);
                    return ResponseEntity.ok(threads);

                default:
                    return ResponseEntity.badRequest().body("Invalid content type: " + contentType);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving data: " + e.getMessage());
        }
    }

    @GetMapping("/created-at-between/{contentType}")
    public ResponseEntity<?> getContentsByCreatedAtBetween(@PathVariable String contentType,
            @RequestParam LocalDateTime start, @RequestParam LocalDateTime end) {
        List<Content> contents;
        switch (contentType.toLowerCase()) {
            case "comment":
                contents = commentService.getContentsByCreatedAtBetween(start, end);
                break;
            case "post":
                contents = postService.getContentsByCreatedAtBetween(start, end);
                break;
            case "thread":
                contents = threadService.getContentsByCreatedAtBetween(start, end);
                break;
            default:
                throw new IllegalArgumentException("Invalid content type: " + contentType);
        }
        return new ResponseEntity<>(contents, HttpStatus.OK);
    }

    @GetMapping("/content-containing/{contentType}")
    public ResponseEntity<?> getContentsByContentContaining(@PathVariable String contentType,
            @RequestParam String text) {
        List<? extends Content> contents;
        switch (contentType.toLowerCase()) {
            case "comment":
                contents = commentService.getContentsByContentContaining(text);
                break;
            case "post":
                contents = postService.getContentsByContentContaining(text);
                break;
            case "thread":
                contents = threadService.getContentsByContentContaining(text);
                break;
            default:
                throw new IllegalArgumentException("Invalid content type: " + contentType);
        }
        return new ResponseEntity<>(contents, HttpStatus.OK);
    }

    @DeleteMapping("/{contentType}/delete-content-by-id/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> deleteContent(@PathVariable String contentType, @PathVariable Long id) {
        // Fetching a placeholder user; replace with dynamic user fetching in production
        User user = userService.getUser(1L);

        switch (contentType.toLowerCase()) {
            case "comment":
                commentService.deleteContent(id, user, contentType);
                break;
            case "post":
                postService.deleteContent(id, user, contentType);
                break;
            case "thread":
                threadService.deleteContent(id, user, contentType);
                break;
            default:
                throw new IllegalArgumentException("Invalid content type: " + contentType);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/list-all-content-of-type/{contentType}")
    public ResponseEntity<List<Content>> listContent(@PathVariable String contentType,
            @RequestParam(defaultValue = "false") boolean includeNested) {
        List<? extends Content> contents; // Use wildcard
        switch (contentType.toLowerCase()) {
            case "comment":
                contents = new ArrayList<>(commentService.listContent()); // This ensures it is treated as a list of
                                                                          // Content
                break;
            case "post":
                contents = new ArrayList<>(postService.listContent(includeNested));
                break;
            case "thread":
                contents = new ArrayList<>(threadService.listContent(includeNested));
                break;
            default:
                throw new IllegalArgumentException("Invalid content type: " + contentType);
        }
        return new ResponseEntity<List<Content>>(new ArrayList<>(contents), HttpStatus.OK); // Safe cast through new
                                                                                            // list
    }

    @PostMapping("/{contentType}/{contentId}/vote")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> handleVote(@PathVariable String contentType, @PathVariable Long contentId,
            @RequestParam Long userId, @RequestParam boolean isUpVote) {
        switch (contentType.toLowerCase()) {
            case "comment":
                commentService.handleVote(contentId, userId, isUpVote, contentType);
                ;
                break;
            case "post":
                postService.handleVote(contentId, userId, isUpVote, contentType);
                break;
            case "thread":
                threadService.handleVote(contentId, userId, isUpVote, contentType);
                break;
            default:
                throw new IllegalArgumentException("Invalid content type: " + contentType);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
