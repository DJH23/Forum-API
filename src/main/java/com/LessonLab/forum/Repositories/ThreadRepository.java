package com.LessonLab.forum.Repositories;

import com.LessonLab.forum.Models.Thread;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ThreadRepository extends JpaRepository<Thread, Long> {

    // Find threads by title containing a specific text
    List<Thread> findByTitleContaining(String title);

    // Find threads by description containing a specific text
    List<Thread> findByDescriptionContaining(String description);

    @Query("SELECT t FROM Thread t LEFT JOIN FETCH t.posts")
    List<Thread> findAllWithPosts();

    @Query("SELECT t.id, t.title, t.description, t.createdAt, t.upvotes, t.downvotes FROM Thread t")
    List<Thread> findAllWithoutPosts();

    @Query("SELECT t FROM Thread t WHERE t.user.id = :userId")
    Page<Thread> findThreadsByUserId(@Param("userId") Long userId, Pageable pageable);

}
