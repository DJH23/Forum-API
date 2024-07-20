package com.LessonLab.forum.Controllers;

import com.LessonLab.forum.Models.Post;
import com.LessonLab.forum.Models.PostDTO;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Services.PostService;
import com.LessonLab.forum.Services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @PostMapping("/add-post-to-thread")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MODERATOR')")
    @Operation(summary = "Add post to thread", description = "Add a post to an existing thread")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Post created", content = @Content(schema = @Schema(implementation = Post.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Thread not found", content = @Content(schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<?> addPostContentToThread(@RequestParam Long threadId, @RequestParam String postContent) {
        User user = userService.getCurrentUser();
        Post addedPost = postService.addPostToThread(threadId, postContent, user);
        return new ResponseEntity<>(addedPost, HttpStatus.CREATED);
    }

    // Constructor injection of PostService
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/most-commented-posts")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MODERATOR')")
    @Operation(summary = "Get most commented posts", description = "Retrieve the posts with the most comments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Posts retrieved", content = @Content(schema = @Schema(implementation = PostDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<List<PostDTO>> getMostCommentedPostDTOs(
            Pageable pageableMostCommentedPostDTOs,
            @RequestParam(value = "includeNestedComments", defaultValue = "true") boolean includeNestedComments) {
        List<PostDTO> posts = postService.getMostCommentedPostDTOs(pageableMostCommentedPostDTOs,
                includeNestedComments);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }
}
