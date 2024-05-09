package com.LessonLab.forum.Controllers;

import com.LessonLab.forum.Models.Content;
import com.LessonLab.forum.Models.Post;
import com.LessonLab.forum.Models.PostDTO;
import com.LessonLab.forum.Models.Thread;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Services.PostService;
import com.LessonLab.forum.Services.ThreadService;
import com.LessonLab.forum.Services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private ThreadService threadService;

    @Autowired
    private UserService userService;

    /*
     * @PostMapping("/")
     * 
     * @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MODERATOR')")
     * public ResponseEntity<?> addPost(@RequestBody Post post) {
     * Post savedPost = postService.addPost(post, null);
     * return new ResponseEntity<>(savedPost, HttpStatus.CREATED);
     * }
     */

    /*
     * @PutMapping("/{id}")
     * 
     * @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MODERATOR')")
     * public ResponseEntity<?> updatePost(@PathVariable Long id, @RequestBody
     * String newContent) {
     * Post updatedPost = postService.updatePost(id, newContent, null);
     * return new ResponseEntity<>(updatedPost, HttpStatus.OK);
     * }
     */

    /*
     * @GetMapping("/{id}")
     * public ResponseEntity<?> getPost(@PathVariable Long id) {
     * Post post = postService.getPost(id);
     * return new ResponseEntity<>(post, HttpStatus.OK);
     * }
     */

    /*
     * @PostMapping("/addPost")
     * public ResponseEntity<?> addPostContent(@RequestBody PostDTO postDTO) {
     * User user = userService.getUser(1L);
     * Content addedContent = postService.addContent(postDTO, user);
     * return new ResponseEntity<>(addedContent, HttpStatus.CREATED);
     * }
     */

    @PostMapping("/add-post-to-thread")
    public ResponseEntity<?> addPostContentToThread(@RequestParam Long threadId, @RequestParam String postContent) {
        User user = userService.getUser(1L); // This is a placeholder.
        Post addedPost = postService.addPostToThread(threadId, postContent, user);
        return new ResponseEntity<>(addedPost, HttpStatus.CREATED);
    }

    /*
     * @GetMapping("/thread/{threadId}")
     * public ResponseEntity<?> getPostsByThread(@PathVariable Long threadId) {
     * 
     * Thread thread = (Thread) threadService.getThreadWithId(threadId);
     * List<Post> posts = postService.getPostsByThread(thread);
     * return new ResponseEntity<>(posts, HttpStatus.OK);
     * }
     */

    /*
     * @GetMapping("/comment-content/{content}")
     * public ResponseEntity<?> getPostsByCommentContent(@PathVariable String
     * content) {
     * List<Post> posts = postService.getPostsByCommentContent(content);
     * return new ResponseEntity<>(posts, HttpStatus.OK);
     * }
     */

    // Constructor injection of PostService
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/most-commented-posts")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<List<PostDTO>> getMostCommentedPostDTOs(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageableMostCommentedPostDTOs,
            @RequestParam(value = "includeNestedComments", defaultValue = "true") boolean includeNestedComments) {
        List<PostDTO> posts = postService.getMostCommentedPostDTOs(pageableMostCommentedPostDTOs,
                includeNestedComments);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    /*
     * @GetMapping("/search/{searchText}")
     * public ResponseEntity<?> searchPosts(@PathVariable String searchText) {
     * List<Post> posts = postService.searchPosts(searchText);
     * return new ResponseEntity<>(posts, HttpStatus.OK);
     * }
     */

    /*
     * @GetMapping("/user/{userId}")
     * 
     * @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MODERATOR')")
     * public ResponseEntity<?> getPagedPostsByUser(@PathVariable Long userId,
     * Pageable pageable) {
     * Page<Post> posts = postService.getPagedPostsByUser(userId, pageable);
     * return new ResponseEntity<>(posts, HttpStatus.OK);
     * }
     */

    /*
     * @GetMapping("/created-at-between")
     * public ResponseEntity<?> getPostsByCreatedAtBetween(@RequestParam
     * LocalDateTime start, @RequestParam LocalDateTime end) {
     * List<Post> posts = postService.getPostsByCreatedAtBetween(start, end);
     * return new ResponseEntity<>(posts, HttpStatus.OK);
     * }
     */

    /*
     * @GetMapping("/content-containing/{text}")
     * public ResponseEntity<?> getPostsByContentContaining(@PathVariable String
     * text) {
     * List<Post> posts = postService.getPostsByContentContaining(text);
     * return new ResponseEntity<>(posts, HttpStatus.OK);
     * }
     */

    /*
     * @DeleteMapping("/{id}")
     * 
     * @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
     * public ResponseEntity<?> deletePost(@PathVariable Long id) {
     * postService.deletePost(id, null);
     * return new ResponseEntity<>(HttpStatus.NO_CONTENT);
     * }
     */

    /*
     * @GetMapping("/")
     * public ResponseEntity<?> listPosts() {
     * List<Post> posts = postService.listPosts();
     * return new ResponseEntity<>(posts, HttpStatus.OK);
     * }
     */

    /*
     * @PostMapping("/{id}/vote")
     * 
     * @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MODERATOR')")
     * public ResponseEntity<?> handlePostVote(@PathVariable Long id, @RequestParam
     * Long userId, @RequestParam boolean isUpVote) {
     * postService.handlePostVote(id, userId, isUpVote);
     * return new ResponseEntity<>(HttpStatus.OK);
     * }
     */
}