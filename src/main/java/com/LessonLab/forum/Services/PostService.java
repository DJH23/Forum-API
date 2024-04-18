package com.LessonLab.forum.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.LessonLab.forum.Models.Permission;
import com.LessonLab.forum.Models.Post;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Repositories.PostRepository;
import com.LessonLab.forum.Repositories.UserRepository;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {
        return userRepository.findById(1L).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public List<Post> getAllPosts() {
        User currentUser = getCurrentUser();
        if (!currentUser.getRole().getPermissions().contains(Permission.READ_POST)) {
            throw new AccessDeniedException("You do not have permission to read posts.");
        }
        return postRepository.findAll();
    }

    public void createPost(Post post) {
        User currentUser = getCurrentUser();
        if (!currentUser.getRole().getPermissions().contains(Permission.WRITE_POST)) {
            throw new AccessDeniedException("You do not have permission to create posts.");
        }
        postRepository.save(post);
    }

    public void deletePost(Long postId) {
        User currentUser = getCurrentUser();
        if (!currentUser.getRole().getPermissions().contains(Permission.DELETE_POST)) {
            throw new AccessDeniedException("You do not have permission to delete posts.");
        }
        postRepository.deleteById(postId);
    }
}
