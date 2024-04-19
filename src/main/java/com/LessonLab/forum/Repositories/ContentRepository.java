package com.LessonLab.forum.Repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.LessonLab.forum.Models.Content;

import java.util.List;

@NoRepositoryBean  
public interface ContentRepository<T extends Content, ID> extends JpaRepository<T, ID> {
  //  List<T> findByUserId(Long userId);

    List<T> findByContentContaining(String text);

    Page<T> findByUserId(Long userId, Pageable pageable);
    
}
