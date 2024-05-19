package com.LessonLab.forum.Controllers.interfaces;

import java.util.List;

import com.LessonLab.forum.Models.User;

/**
 * Interface for UserController. Contains methods for handling user related
 * operations
 */
public interface UserControllerInterface {
    /**
     * Retrieves a list of all users
     *
     * @return list of all users
     */
    List<User> getUsers();

    /**
     * Saves a new user
     *
     * @param user the user to be saved
     * @return the saved user
     */
    User saveUser(User user);
}
