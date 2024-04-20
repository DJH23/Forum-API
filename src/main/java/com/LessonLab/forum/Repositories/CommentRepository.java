package com.LessonLab.forum.Repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.LessonLab.forum.Models.Comment;
import com.LessonLab.forum.Models.Post;

@Repository
public interface CommentRepository extends ContentRepository<Comment> {

    // Find comments by post
    List<Comment> findByPost(Post post);

    // Custom query to find the most recent comments
    @Query("SELECT c FROM Comment c ORDER BY c.createdAt DESC")
    List<Comment> findRecentComments(Pageable pageable);
}
