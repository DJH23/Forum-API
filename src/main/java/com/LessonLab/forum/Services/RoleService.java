package com.LessonLab.forum.Services;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.LessonLab.forum.Models.Role;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Repositories.RoleRepository;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public List<Map<String, String>> getUsersByRole(String roleName) {
        if (roleName == null) {
            throw new IllegalArgumentException("Role name cannot be null");
        }
        Role role = roleRepository.findByName(roleName);
        if (role == null) {
            throw new IllegalArgumentException("Role not found");
        }
        List<User> users = roleRepository.findUsersByRolesIn(Collections.singleton(role));
        if (users.isEmpty()) {
            return Collections.emptyList();
        }
        return users.stream()
                .map(user -> {
                    Map<String, String> userMap = new HashMap<>();
                    userMap.put("id", user.getId().toString());
                    userMap.put("username", user.getUsername());
                    return userMap;
                })
                .collect(Collectors.toList());
    }
}
