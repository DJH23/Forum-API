package com.LessonLab.forum.Repositories;

import com.LessonLab.forum.Models.Thread;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ThreadRepository extends ContentRepository<Thread> {
    
    //find threads by a specific user or with a specific title.
    List<Thread> findByUserId(Long userId); // Assuming 'User' has a one-to-many relation with 'Thread'

    List<Thread> findByTitleContaining(String title);
}
