package com.LessonLab.forum.Repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.LessonLab.forum.Models.Post;

import java.util.List;

@Repository
public interface PostRepository extends ContentRepository<Post> {

    // Find posts by thread
    List<Post> findByThread(Thread thread);

    // Find posts by comment content
    @Query("SELECT p FROM Post p JOIN p.comments c WHERE c.content LIKE %:content%")
    List<Post> findByCommentContent(@Param("content") String content);

    // Custom query to find the most commented posts
    @Query("SELECT p, COUNT(c) FROM Post p JOIN p.comments c GROUP BY p ORDER BY COUNT(c) DESC")
    List<Post> findMostCommentedPosts(Pageable pageable);
}
