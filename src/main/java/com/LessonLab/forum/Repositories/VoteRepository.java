package com.LessonLab.forum.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.LessonLab.forum.Models.Content;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Models.Vote;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByUserAndContent(User user, Content content);
}
