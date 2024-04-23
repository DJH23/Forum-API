package com.LessonLab.forum.Services;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Models.Content;
import com.LessonLab.forum.Models.Comment;
import com.LessonLab.forum.Models.Post;
import com.LessonLab.forum.Repositories.CommentRepository;

@Service
public class CommentService extends ContentService{

    @Autowired
    private CommentRepository commentRepository;

    public Comment addComment(Comment comment, User user) {
        return (Comment) addContent(comment, user);
    }

    public Comment updateComment(Long id, String newContent, User user) {
        return (Comment) updateContent(id, newContent, user);
    }

    public Comment getComment(Long id) {
        return (Comment) getContent(id);
    }

    public List<Comment> searchComments(String searchText) {
        List<Content> contents = searchContent(searchText);
        return contents.stream().map(content -> (Comment) content).collect(Collectors.toList());
    }

    public Page<Comment> getPagedCommentsByUser(Long userId, Pageable pageable) {
        Page<Content> contents = getPagedContentByUser(userId, pageable);
        return new PageImpl<>(contents.getContent().stream().map(content -> (Comment) content).collect(Collectors.toList()), pageable, contents.getTotalElements());
    }

    public List<Comment> getCommentsByCreatedAtBetween(LocalDateTime start, LocalDateTime end) {
        List<Content> contents = getContentsByCreatedAtBetween(start, end);
        return contents.stream().map(content -> (Comment) content).collect(Collectors.toList());
    }

    public List<Comment> getCommentsByContentContaining(String text) {
        List<Content> contents = getContentsByContentContaining(text);
        return contents.stream().map(content -> (Comment) content).collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(Long commentId, User user) {
        super.deleteContent(commentId, user);
    }

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
    
    public List<Comment> getRecentComments(Pageable pageable) {
        try {
            if (pageable == null) {
                throw new IllegalArgumentException("Pageable cannot be null");
            }
            return commentRepository.findRecentComments(pageable);
        } catch (Exception e) {
            // Log the exception and rethrow it
            System.err.println("Error getting recent comments: " + e.getMessage());
            throw e;
        }
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

    public List<Comment> listComments() {
        List<Content> contents = super.listContent();
        return contents.stream()
            .filter(content -> content instanceof Comment)
            .map(content -> (Comment) content)
            .collect(Collectors.toList());
    }

    public void handleCommentVote(Long commentId, Long userId, boolean isUpVote) {
        super.handleVote(commentId, userId, isUpVote);
    }
    
}
