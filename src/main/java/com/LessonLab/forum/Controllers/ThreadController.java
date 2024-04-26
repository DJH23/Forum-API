package com.LessonLab.forum.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.LessonLab.forum.Services.ThreadService;

@RestController
@RequestMapping("/api/threads")
public class ThreadController {

    @Autowired
    private ThreadService threadService;

    @PostMapping("/")
    public ResponseEntity<?> createThread(@RequestBody Thread thread) {
        Thread savedThread = threadService.createThread(thread);
        return new ResponseEntity<>(savedThread, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateThread(@PathVariable Long id, @RequestBody Thread thread) {
        Thread updatedThread = threadService.updateThread(id, thread);
        return new ResponseEntity<>(updatedThread, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getThread(@PathVariable Long id) {
        Thread thread = threadService.getThread(id);
        return new ResponseEntity<>(thread, HttpStatus.OK);
    }

    @GetMapping("/search/{text}")
    public ResponseEntity<?> searchThreads(@PathVariable String text) {
        List<Thread> threads = threadService.searchThreads(text);
        return new ResponseEntity<>(threads, HttpStatus.OK);
    }

    // Other endpoints...

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteThread(@PathVariable Long id) {
        threadService.deleteThread(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/")
    public ResponseEntity<?> listThreads() {
        List<Thread> threads = threadService.listThreads();
        return new ResponseEntity<>(threads, HttpStatus.OK);
    }

    @PostMapping("/{id}/vote")
    public ResponseEntity<?> handleThreadVote(@PathVariable Long id, @RequestParam Long userId, @RequestParam boolean isUpVote) {
        threadService.handleThreadVote(id, userId, isUpVote);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
