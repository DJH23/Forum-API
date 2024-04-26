package com.LessonLab.forum.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.LessonLab.forum.Models.Post;
import com.LessonLab.forum.Services.PostService;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping("/")
    public ResponseEntity<?> addPost(@RequestBody Post post) {
        Post savedPost = postService.addPost(post);
        return new ResponseEntity<>(savedPost, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Long id, @RequestBody String newContent) {
        Post updatedPost = postService.updatePost(id, newContent);
        return new ResponseEntity<>(updatedPost, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPost(@PathVariable Long id) {
        Post post = postService.getPost(id);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @GetMapping("/thread/{threadId}")
    public ResponseEntity<?> getPostsByThread(@PathVariable Long threadId) {
        Thread thread = threadService.getThread(threadId);
        List<Post> posts = postService.getPostsByThread(thread);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    // Other endpoints...

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/")
    public ResponseEntity<?> listPosts() {
        List<Post> posts = postService.listPosts();
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @PostMapping("/{id}/vote")
    public ResponseEntity<?> handlePostVote(@PathVariable Long id, @RequestParam Long userId, @RequestParam boolean isUpVote) {
        postService.handlePostVote(id, userId, isUpVote);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
