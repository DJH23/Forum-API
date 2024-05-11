package com.LessonLab.forum.Controllers;

import com.LessonLab.forum.Models.Thread;
import com.LessonLab.forum.Services.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/threads")
public class ThreadController {

    @Autowired
    private ThreadService threadService;
    @PostMapping("/")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> createThread(@RequestParam Long userId, @RequestParam String threadTitle,
            @RequestParam String threadDescription) {
        Thread savedThread = threadService.createThread(userId, threadTitle, threadDescription);
        return new ResponseEntity<>(savedThread, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> updateThread(@PathVariable Long id, @RequestParam String newTitle,
            @RequestParam String newDescription) {
        Thread thread = new Thread(newTitle, newDescription);
        Thread updatedThread = threadService.updateThread(id, thread);
        return new ResponseEntity<>(updatedThread, HttpStatus.OK);
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<?> getThreadsByTitle(@PathVariable String title) {
        return new ResponseEntity<>(threadService.getThreadsByTitle(title), HttpStatus.OK);
    }

    @GetMapping("/description/{description}")
    public ResponseEntity<?> getThreadsByDescription(@PathVariable String description) {
        return new ResponseEntity<>(threadService.getThreadsByDescription(description), HttpStatus.OK);
    }

}