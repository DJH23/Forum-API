package com.LessonLab.forum.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.LessonLab.forum.Models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Optional<UserExtension> findByUsername(String username);

    @NonNull
    List<User> findAll();

    boolean existsByUsername(String username);

    void deleteByUsername(String username);

    /**
     * Method to find a User entity by its username field
     *
     * @param username The username of the User entity to search for
     * @return The found User entity or null if not found
     */
    User findByUsername(String username);

}
