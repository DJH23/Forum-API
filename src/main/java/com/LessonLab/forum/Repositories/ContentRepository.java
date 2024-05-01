package com.LessonLab.forum.Repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.LessonLab.forum.Models.Content;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long>{

   // @Query("SELECT c FROM #{#entityName} c WHERE c.user.id = :userId")

    // Find content created within a specific time range
   // List<Content> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT c FROM #{#entityName} c WHERE c.createdAt BETWEEN :start AND :end")
    List<Content> findByCreatedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    Page<Content> findByUserUserId(@Param("userId") Long userId, Pageable pageable);

    List<Content> findByContentContaining(String text);

    @Query("SELECT c FROM #{#entityName} c ORDER BY c.createdAt DESC")
    Page<Content> findRecentContents(Pageable pageable);
}
