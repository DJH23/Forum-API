package com.LessonLab.forum.Repositories;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.LessonLab.forum.Models.Role;
import com.LessonLab.forum.Models.User;

/**
 * The RoleRepository interface extends JpaRepository to allow for CRUD
 * operations
 * on Role entities in the database.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Method to find a Role entity by its name field
     *
     * @param name The name of the Role entity to search for
     * @return The found Role entity or null if not found
     */
    Role findByName(String name);

    /**
     * Method to find a Role entity by its enum value
     *
     * @param role The enum value of the Role entity to search for
     * @return The found Role entity or null if not found
     */
    Role findByName(Role role);

    /**
     * Method to find users by their roles
     *
     * @param roles Collection of roles to search for
     * @return List of users having any of the specified roles
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r IN :roles")
    List<User> findUsersByRolesIn(@Param("roles") Collection<Role> roles);
}
