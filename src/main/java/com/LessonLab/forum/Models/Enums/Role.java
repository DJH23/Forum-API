package com.LessonLab.forum.Models.Enums;

import java.util.Set;

public enum Role {
    ADMIN(Set.of(Permission.READ_POST, Permission.WRITE_POST, Permission.DELETE_POST, Permission.READ_USER, Permission.WRITE_USER, Permission.DELETE_USER, Permission.ACCESS_ADMIN_PANEL)),
    MODERATOR(Set.of(Permission.READ_POST, Permission.WRITE_POST, Permission.DELETE_USER)),
    USER(Set.of(Permission.READ_POST, Permission.WRITE_POST));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }
}
