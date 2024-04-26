package com.LessonLab.forum.Controllers;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.LessonLab.forum.Models.Content;

@RestController
@RequestMapping("/api/contents")
public class ContentController {

    @PostMapping("/")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> addContent(@RequestBody Content content) {
        // Call the addContent method from the service
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> updateContent(@PathVariable Long id, @RequestBody String newContent) {
        // Call the updateContent method from the service
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getContent(@PathVariable Long id) {
        // Call the getContent method from the service
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchContent(@RequestParam String searchText) {
        // Call the searchContent method from the service
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getPagedContentByUser(@PathVariable Long userId, Pageable pageable) {
        // Call the getPagedContentByUser method from the service
    }

    @GetMapping("/created-at-between")
    public ResponseEntity<?> getContentsByCreatedAtBetween(@RequestParam LocalDateTime start, @RequestParam LocalDateTime end) {
        // Call the getContentsByCreatedAtBetween method from the service
    }

    @GetMapping("/content-containing")
    public ResponseEntity<?> getContentsByContentContaining(@RequestParam String text) {
        // Call the getContentsByContentContaining method from the service
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> deleteContent(@PathVariable Long id) {
        // Call the deleteContent method from the service
    }

    @GetMapping("/")
    public ResponseEntity<?> listContent() {
        // Call the listContent method from the service
    }

    @PostMapping("/{contentId}/vote")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> handleVote(@PathVariable Long contentId, @RequestParam Long userId, @RequestParam boolean isUpVote) {
        // Call the handleVote method from the service
    }
}
