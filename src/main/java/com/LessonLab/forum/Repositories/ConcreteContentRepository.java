package com.LessonLab.forum.Repositories;

import com.LessonLab.forum.Models.Content;
import org.springframework.stereotype.Repository;

@Repository
public interface ConcreteContentRepository extends ContentRepository<Content, Long> {
}
