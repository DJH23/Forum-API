package com.LessonLab.forum.Controllers;

import com.LessonLab.forum.Models.Thread;
import com.LessonLab.forum.Services.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/threads")
public class ThreadController {

    @Autowired
    private ThreadService threadService;

    @PostMapping("/")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MODERATOR')")
    @Operation(summary = "Create a new thread", description = "Create a new discussion thread")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Thread created", content = @Content(schema = @Schema(implementation = Thread.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<?> createThread(@RequestParam String threadTitle, @RequestParam String threadDescription) {
        Thread savedThread = threadService.createThread(threadTitle, threadDescription);
        return new ResponseEntity<>(savedThread, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MODERATOR')")
    @Operation(summary = "Update a thread", description = "Update the title and description of a thread")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thread updated", content = @Content(schema = @Schema(implementation = Thread.class))),
            @ApiResponse(responseCode = "404", description = "Thread not found", content = @Content(schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<?> updateThread(@PathVariable Long id, @RequestParam String newTitle,
            @RequestParam String newDescription) {
        Thread thread = new Thread(newTitle, newDescription);
        Thread updatedThread = threadService.updateThread(id, thread);
        return new ResponseEntity<>(updatedThread, HttpStatus.OK);
    }

    @GetMapping("/title/{title}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MODERATOR')")
    @Operation(summary = "Get threads by title", description = "Retrieve threads by their title")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Threads retrieved", content = @Content(schema = @Schema(implementation = Thread.class))),
            @ApiResponse(responseCode = "404", description = "Threads not found", content = @Content(schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<?> getThreadsByTitle(@PathVariable String title) {
        return new ResponseEntity<>(threadService.getThreadsByTitle(title), HttpStatus.OK);
    }

    @GetMapping("/description/{description}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MODERATOR')")
    @Operation(summary = "Get threads by description", description = "Retrieve threads by their description")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Threads retrieved", content = @Content(schema = @Schema(implementation = Thread.class))),
            @ApiResponse(responseCode = "404", description = "Threads not found", content = @Content(schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<?> getThreadsByDescription(@PathVariable String description) {
        return new ResponseEntity<>(threadService.getThreadsByDescription(description), HttpStatus.OK);
    }
}
