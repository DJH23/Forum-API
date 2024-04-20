package com.LessonLab.forum.Repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.LessonLab.forum.Models.Content;

//@NoRepositoryBean
@Repository
public interface ContentRepository extends JpaRepository<Content, Long>{

    @Query("SELECT c FROM #{#entityName} c WHERE c.user.id = :userId")

    // Find content created within a specific time range
    List<Content> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    Page<Content> findByUserId(@Param("userId") Long userId, Pageable pageable);

    List<Content> findByContentContaining(String text);
}
