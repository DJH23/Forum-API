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

import com.LessonLab.forum.Models.Content;
import com.LessonLab.forum.Services.ContentService;

@RestController
@RequestMapping("/api/contents")
public class ContentController {

    @Autowired
    private ContentService contentService;
    
    @PostMapping("/")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> addContent(@RequestBody Content content) {
        Content savedContent = contentService.addContent(content, null);
        return new ResponseEntity<>(savedContent, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> updateContent(@PathVariable Long id, @RequestBody String newContent) {
        Content updatedContent = contentService.updateContent(id, newContent, null);
        return new ResponseEntity<>(updatedContent, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getContent(@PathVariable Long id) {
        Content content = contentService.getContent(id);
        return new ResponseEntity<>(content, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchContent(@RequestParam String searchText) {
        List<Content> contents = contentService.searchContent(searchText);
        return new ResponseEntity<>(contents, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getPagedContentByUser(@PathVariable Long userId, Pageable pageable) {
        Page<Content> contents = contentService.getPagedContentByUser(userId, pageable);
        return new ResponseEntity<>(contents, HttpStatus.OK);
    }

    @GetMapping("/created-at-between")
    public ResponseEntity<?> getContentsByCreatedAtBetween(@RequestParam LocalDateTime start, @RequestParam LocalDateTime end) {
        List<Content> contents = contentService.getContentsByCreatedAtBetween(start, end);
        return new ResponseEntity<>(contents, HttpStatus.OK);
    }

    @GetMapping("/content-containing")
    public ResponseEntity<?> getContentsByContentContaining(@RequestParam String text) {
        List<Content> contents = contentService.getContentsByContentContaining(text);
        return new ResponseEntity<>(contents, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> deleteContent(@PathVariable Long id) {
        contentService.deleteContent(id, null);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/")
    public ResponseEntity<?> listContent() {
        List<Content> contents = contentService.listContent();
        return new ResponseEntity<>(contents, HttpStatus.OK);
    }

    @PostMapping("/{contentId}/vote")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> handleVote(@PathVariable Long contentId, @RequestParam Long userId, @RequestParam boolean isUpVote) {
        contentService.handleVote(contentId, userId, isUpVote);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
