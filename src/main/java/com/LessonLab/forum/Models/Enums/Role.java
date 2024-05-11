package com.LessonLab.forum.Models.Enums;

import java.util.Set;

public enum Role {
    ADMIN(Set.of(Permission.READ_CONTENT, Permission.WRITE_CONTENT, Permission.DELETE_CONTENT, Permission.READ_USER,
            Permission.WRITE_USER, Permission.DELETE_USER, Permission.ACCESS_ADMIN_PANEL)),
    MODERATOR(Set.of(Permission.READ_CONTENT, Permission.WRITE_CONTENT, Permission.DELETE_USER)),
    USER(Set.of(Permission.READ_CONTENT, Permission.WRITE_CONTENT)), /* GUEST */;

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }
}
