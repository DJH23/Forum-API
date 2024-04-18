package com.LessonLab.forum.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.LessonLab.forum.Models.Role;
import com.LessonLab.forum.Models.Status;
import com.LessonLab.forum.Models.User;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    List<User> findByRole(Role role);
    
    List<User> findByStatus(Status status);

    // Add your custom query methods here

}
