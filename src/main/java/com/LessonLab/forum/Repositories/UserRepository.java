package com.LessonLab.forum.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.LessonLab.forum.Models.UserExtension;
import com.LessonLab.forum.Models.Enums.Account;
import com.LessonLab.forum.Models.Enums.Status;

import com.LessonLab.forum.security.models.Role;
import com.LessonLab.forum.security.models.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserExtension, Long> {

    Optional<UserExtension> findByUsername(String username);

    List<UserExtension> getUsers();

    List<User> findByRole(Role role);

    List<User> findByStatus(Status status);

    List<UserExtension> findByAccountStatus(Account accountStatus);

    List<User> findByRoleIn(List<Role> roles);

    boolean existsByUsername(String username);

    void deleteByUsername(String username);

    UserExtension saveUser(UserExtension user);

    Role saveRole(Role role);

    Role findByRoleName(Role role);

}
