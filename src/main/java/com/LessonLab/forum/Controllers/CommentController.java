package com.LessonLab.forum.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.LessonLab.forum.Models.Comment;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Services.CommentService;
import com.LessonLab.forum.Services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @PostMapping("/add-comment-to-post")
      @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MODERATOR')")
    @Operation(summary = "Add comment to post", description = "Add a comment to an existing post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Comment created", content = @Content(schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Post not found", content = @Content(schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<?> addCommentToPost(
            @RequestParam Long postId,
            @RequestParam String commentContent) {

        User user = userService.getCurrentUser();
        Comment addedComment = commentService.addCommentToPost(postId, commentContent, user);
        return new ResponseEntity<>(addedComment, HttpStatus.CREATED);
    }
}
