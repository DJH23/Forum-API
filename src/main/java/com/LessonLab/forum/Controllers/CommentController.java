package com.LessonLab.forum.Controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.LessonLab.forum.Models.Comment;
import com.LessonLab.forum.Models.Post;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Services.CommentService;
import com.LessonLab.forum.Services.PostService;
import com.LessonLab.forum.Services.UserService;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;
    
    @Autowired
    private PostService postService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> addComment(@RequestBody Comment comment) {
        Comment savedComment = commentService.addComment(comment, null);
        return new ResponseEntity<>(savedComment, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> updateComment(@PathVariable Long id, @RequestBody String newContent) {
        Comment updatedComment = commentService.updateComment(id, newContent, null);
        return new ResponseEntity<>(updatedComment, HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getComment(@PathVariable Long id) {
        Comment comment = commentService.getComment(id);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }
    
    @GetMapping("/search/{text}")
    public ResponseEntity<?> searchComments(@PathVariable String text) {
        List<Comment> comments = commentService.searchComments(text);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }
    
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> getPagedCommentsByUser(@PathVariable Long userId, Pageable pageable) {
        Page<Comment> comments = commentService.getPagedCommentsByUser(userId, pageable);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }
    
    @GetMapping("/created-at-between")
    public ResponseEntity<?> getCommentsByCreatedAtBetween(@RequestParam LocalDateTime start, @RequestParam LocalDateTime end) {
        List<Comment> comments = commentService.getCommentsByCreatedAtBetween(start, end);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }
    
    @GetMapping("/content-containing/{text}")
    public ResponseEntity<?> getCommentsByContentContaining(@PathVariable String text) {
        List<Comment> comments = commentService.getCommentsByContentContaining(text);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id, null);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @GetMapping("/post/{postId}")
    public ResponseEntity<?> getCommentsByPost(@PathVariable Long postId) {
        Post post = postService.getPost(postId);
        List<Comment> comments = commentService.getCommentsByPost(post);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }
    
    @GetMapping("/recent")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> getRecentComments(Pageable pageable) {
        List<Comment> comments = commentService.getRecentComments(pageable);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }
    
    @GetMapping("/count/{postId}/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> countCommentsByPostAndUserNot(@PathVariable Long postId, @PathVariable Long userId) {
        Post post = postService.getPost(postId);
        User user = userService.getUser(userId);
        long count = commentService.countCommentsByPostAndUserNot(post, user);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }
    
    @GetMapping("/")
    public ResponseEntity<?> listComments() {
        List<Comment> comments = commentService.listComments();
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }
    
    @PostMapping("/{id}/vote")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> handleCommentVote(@PathVariable Long id, @RequestParam Long userId, @RequestParam boolean isUpVote) {
        commentService.handleCommentVote(id, userId, isUpVote);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    
}
