package com.LessonLab.forum.Repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.LessonLab.forum.Models.Thread;
import com.LessonLab.forum.Models.Post;
import com.LessonLab.forum.Models.PostDTO;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // Find posts by thread
    List<Post> findByThread(Thread thread);

    // Find posts by comment content
    @Query("SELECT p FROM Post p JOIN p.comments c WHERE c.content LIKE %:content%")
    List<Post> findByCommentContent(@Param("content") String content);

    // Custom query to find the most commented posts
    @Query("SELECT p, COUNT(c) FROM Post p JOIN p.comments c GROUP BY p ORDER BY COUNT(c) DESC")
    List<Post> findMostCommentedPosts(Pageable pageable);

    @Query("SELECT new com.LessonLab.forum.Models.PostDTO(p.content, p.thread.id, COUNT(c)) " +
            "FROM Post p LEFT JOIN p.comments c GROUP BY p.id, p.thread.id, p.content ORDER BY COUNT(c) DESC")
    List<PostDTO> findMostCommentedPostDTOs(Pageable pageable);

    @Query("SELECT p FROM Post p JOIN FETCH p.comments")
    List<Post> findAllWithComments();

    @Query("SELECT p FROM Post p")
    List<Post> findAllWithoutComments();

    @Query("SELECT p FROM Post p WHERE p.user.id = :userId")
    Page<Post> findPostsByUserId(@Param("userId") Long userId, Pageable pageable);
}
