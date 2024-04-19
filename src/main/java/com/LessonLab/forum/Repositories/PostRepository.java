package com.LessonLab.forum.Repositories;

import org.springframework.stereotype.Repository;

import com.LessonLab.forum.Models.Post;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends ContentRepository<Post> {

    // Additional specialized methods specific to posts
    List<Post> findByThreadId(Long threadId);  // Find all posts by specific thread ID

    List<Post> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);  // Find posts created within a specific time range
}
