package com.LessonLab.forum.Repositories;

import org.springframework.stereotype.Repository;

import com.LessonLab.forum.Models.Comment;

@Repository
public interface CommentRepository extends ContentRepository<Comment> {
    
}
