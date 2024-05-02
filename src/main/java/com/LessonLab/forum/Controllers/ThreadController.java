package com.LessonLab.forum.Controllers;

import com.LessonLab.forum.Models.Thread;
import com.LessonLab.forum.Services.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/threads")
public class ThreadController {

    @Autowired
    private ThreadService threadService;

    @PostMapping("/")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> createThread(@RequestBody Thread thread) {
        Thread savedThread = threadService.createThread(thread);
        return new ResponseEntity<>(savedThread, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> updateThread(@PathVariable Long id, @RequestBody Thread thread) {
        Thread updatedThread = threadService.updateThread(id, thread);
        return new ResponseEntity<>(updatedThread, HttpStatus.OK);
    }

    /*
     * @GetMapping("/{id}")
     * public ResponseEntity<?> getThread(@PathVariable Long id) {
     * Thread thread = threadService.getThread(id);
     * return new ResponseEntity<>(thread, HttpStatus.OK);
     * }
     */

    /*
     * @GetMapping("/search/{text}")
     * public ResponseEntity<?> searchThreads(@PathVariable String text) {
     * List<Thread> threads = threadService.searchThreads(text);
     * return new ResponseEntity<>(threads, HttpStatus.OK);
     * }
     */

    /*
     * @GetMapping("/user/{userId}")
     * public ResponseEntity<?> getPagedThreadsByUser(@PathVariable Long userId,
     * Pageable pageable) {
     * return new ResponseEntity<>(threadService.getPagedThreadsByUser(userId,
     * pageable), HttpStatus.OK);
     * }
     */

    /*
     * @GetMapping("/created-at-between")
     * public ResponseEntity<?> getThreadsByCreatedAtBetween(@RequestParam
     * LocalDateTime start, @RequestParam LocalDateTime end) {
     * return new ResponseEntity<>(threadService.getThreadsByCreatedAtBetween(start,
     * end), HttpStatus.OK);
     * }
     */

    /*
     * @GetMapping("/content-containing/{text}")
     * public ResponseEntity<?> getThreadsByContentContaining(@PathVariable String
     * text) {
     * return new
     * ResponseEntity<>(threadService.getThreadsByContentContaining(text),
     * HttpStatus.OK);
     * }
     */

    @GetMapping("/title/{title}")
    public ResponseEntity<?> getThreadsByTitle(@PathVariable String title) {
        return new ResponseEntity<>(threadService.getThreadsByTitle(title), HttpStatus.OK);
    }

    @GetMapping("/description/{description}")
    public ResponseEntity<?> getThreadsByDescription(@PathVariable String description) {
        return new ResponseEntity<>(threadService.getThreadsByDescription(description), HttpStatus.OK);
    }

    /*
     * @GetMapping("/recent")
     * public ResponseEntity<?> getRecentThreads() {
     * return new ResponseEntity<>(threadService.getRecentThreads(), HttpStatus.OK);
     * }
     */

    /*
     * @DeleteMapping("/{id}")
     * 
     * @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
     * public ResponseEntity<?> deleteThread(@PathVariable Long id) {
     * threadService.deleteThread(id, null);
     * return new ResponseEntity<>(HttpStatus.NO_CONTENT);
     * }
     */

    /*
     * @GetMapping("/")
     * public ResponseEntity<?> listThreads() {
     * List<Thread> threads = threadService.listThreads();
     * return new ResponseEntity<>(threads, HttpStatus.OK);
     * }
     */

    /*
     * @PostMapping("/{id}/vote")
     * 
     * @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MODERATOR')")
     * public ResponseEntity<?> handleThreadVote(@PathVariable Long
     * id, @RequestParam Long userId, @RequestParam boolean isUpVote) {
     * threadService.handleThreadVote(id, userId, isUpVote);
     * return new ResponseEntity<>(HttpStatus.OK);
     * }
     */
}