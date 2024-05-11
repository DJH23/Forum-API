package com.LessonLab.forum.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.LessonLab.forum.Models.Comment;
import com.LessonLab.forum.Models.UserExtension;
import com.LessonLab.forum.Services.CommentService;
import com.LessonLab.forum.Services.UserService;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @PostMapping("/add-comment-to-post")
    public ResponseEntity<?> addCommentToPost(
            @RequestParam Long postId,
            @RequestParam String commentContent) {

        UserExtension user = userService.getUser(1L); // This is a placeholder.
        Comment addedComment = commentService.addCommentToPost(postId, commentContent, user);
        return new ResponseEntity<>(addedComment, HttpStatus.CREATED);
    }

}
