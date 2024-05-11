package com.LessonLab.forum.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Models.Enums.Account;
import com.LessonLab.forum.Models.Enums.Role;
import com.LessonLab.forum.Models.Enums.Status;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    List<User> findByRole(Role role);
    
    List<User> findByStatus(Status status);

    List<User> findByAccountStatus(Account accountStatus);

    List<User> findByRoleIn(List<Role> roles);

    boolean existsByUsername(String username);

    void deleteByUsername(String username);

}
