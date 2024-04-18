package com.LessonLab.forum.Repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.LessonLab.forum.Models.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>{

   
    
}
