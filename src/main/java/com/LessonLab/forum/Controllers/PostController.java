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

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @PostMapping("/add-post-to-thread")
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
    public ResponseEntity<List<PostDTO>> getMostCommentedPostDTOs(
            Pageable pageableMostCommentedPostDTOs,
            @RequestParam(value = "includeNestedComments", defaultValue = "true") boolean includeNestedComments) {
        List<PostDTO> posts = postService.getMostCommentedPostDTOs(pageableMostCommentedPostDTOs,
                includeNestedComments);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

}