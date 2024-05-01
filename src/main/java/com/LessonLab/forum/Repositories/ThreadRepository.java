package com.LessonLab.forum.Repositories;

import com.LessonLab.forum.Models.Thread;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ThreadRepository extends JpaRepository<Thread, Long>{

    // Find threads by title containing a specific text
    List<Thread> findByTitleContaining(String title);

    // Find threads by description containing a specific text
    List<Thread> findByDescriptionContaining(String description);

    // Custom query to find the most recent threads
    /* @Query("SELECT t FROM Thread t ORDER BY t.createdAt DESC")
    List<Thread> findRecentThreads(Pageable pageable); */
}
