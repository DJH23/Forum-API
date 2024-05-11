package com.LessonLab.forum.Repositories;

import java.util.List;

import com.LessonLab.forum.Models.UserExtension;
import com.LessonLab.forum.Models.Enums.Account;
import com.LessonLab.forum.Models.Enums.Status;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserExtensionRepository extends JpaRepository<UserExtension, Long> {

    List<UserExtension> findByStatus(Status status);

    List<UserExtension> findByAccountStatus(Account accountStatus);

}
