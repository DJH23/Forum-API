package com.LessonLab.forum.Services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Models.Content;
import com.LessonLab.forum.Models.Comment;
import com.LessonLab.forum.Models.CommentDTO;
import com.LessonLab.forum.Models.Post;
import com.LessonLab.forum.Repositories.CommentRepository;
import com.LessonLab.forum.Repositories.PostRepository;

@Service
public class CommentService extends ContentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    public List<Comment> getCommentsByPost(Post post) {
        try {
            if (post == null) {
                throw new IllegalArgumentException("Post cannot be null");
            }
            return commentRepository.findByPost(post);
        } catch (Exception e) {
            // Log the exception and rethrow it
            System.err.println("Error getting comments by post: " + e.getMessage());
            throw e;
        }
    }

    public Page<Comment> getRecentContents(Pageable pageable) {
        Page<Content> contents = super.contentRepository.findRecentContents(pageable);
        List<Comment> comments = contents.stream()
                .filter(content -> content instanceof Comment)
                .map(content -> (Comment) content)
                .collect(Collectors.toList());
        return new PageImpl<>(comments, pageable, comments.size());
    }

    public long countCommentsByPostAndUserNot(Post post, User user) {
        try {
            if (post == null || user == null) {
                throw new IllegalArgumentException("Post and User cannot be null");
            }
            return commentRepository.countByPostAndUserNot(post, user);
        } catch (Exception e) {
            // Log the exception and rethrow it
            System.err.println("Error counting comments by post and user not: " + e.getMessage());
            throw e;
        }
    }

    public Comment addContent(CommentDTO dto, User user) {
        Comment comment = convertToCommentEntity(dto, user);
        return commentRepository.save(comment);
    }

    private Comment convertToCommentEntity(CommentDTO dto, User user) {
        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return new Comment(dto.getContent(), user, post);
    }

    public List<Comment> listContent() {
        return commentRepository.findAll();
    }

    public Page<Comment> getPagedCommentsByUser(Long userId, Pageable pageable) {
        return commentRepository.findCommentsByUserId(userId, pageable);
    }

    public Comment addCommentToPost(Long postId, String content, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        Comment newComment = new Comment(content, user, post);
        return commentRepository.save(newComment);
    }

}
