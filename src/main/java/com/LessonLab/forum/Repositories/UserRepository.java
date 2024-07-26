package com.LessonLab.forum.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Models.Enums.Account;
import com.LessonLab.forum.Models.Enums.Status;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @NonNull
    List<User> findAll();

    boolean existsByUsername(String username);

    void deleteByUsername(String username);

    /**
     * Method to find a User entity by its username field
     *
     * @param username The username of the User entity to search for
     * @return An Optional of the found User entity or an empty Optional if not found
     */
    Optional<User> findByUsername(String username);

    List<User> findByStatus(Status status);

    List<User> findByAccountStatus(Account accountStatus);
}
