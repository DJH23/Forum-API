package com.LessonLab.forum.Services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.LessonLab.forum.Models.UserExtension;
import com.LessonLab.forum.Models.Post;
import com.LessonLab.forum.Models.PostDTO;
import com.LessonLab.forum.Models.Thread;
import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Models.Content;
import com.LessonLab.forum.Repositories.PostRepository;
import com.LessonLab.forum.Repositories.ThreadRepository;

@Service
public class PostService extends ContentService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ThreadRepository threadRepository;

    public List<Post> getPostsByThread(Thread thread) {
        try {
            if (thread == null) {
                throw new IllegalArgumentException("Thread cannot be null");
            }
            return postRepository.findByThread(thread);
        } catch (Exception e) {
            // Log the exception and rethrow it
            System.err.println("Error getting posts by thread: " + e.getMessage());
            throw e;
        }
    }

    public List<Post> getPostsByCommentContent(String content) {
        try {
            if (content == null) {
                throw new IllegalArgumentException("Content cannot be null");
            }
            return postRepository.findByCommentContent(content);
        } catch (Exception e) {
            // Log the exception and rethrow it
            System.err.println("Error getting posts by comment content: " + e.getMessage());
            throw e;
        }
    }

    public List<PostDTO> getMostCommentedPostDTOs(Pageable pageable, boolean includeNested) {
        try {
            if (pageable == null) {
                throw new IllegalArgumentException("Pageable cannot be null");
            }
            List<PostDTO> posts = postRepository.findMostCommentedPostDTOs(pageable);
            posts.forEach(post -> post.setShowNestedComments(includeNested));
            return posts;
        } catch (Exception e) {
            System.err.println("Error getting most commented posts: " + e.getMessage());
            throw e;
        }
    }

    public Page<Post> getRecentContents(Pageable pageable) {
        Page<Content> contents = super.contentRepository.findRecentContents(pageable);
        List<Post> posts = contents.stream()
                .filter(content -> content instanceof Post)
                .map(content -> (Post) content)
                .collect(Collectors.toList());
        return new PageImpl<>(posts, pageable, posts.size());
    }

    public Post addPostToThread(Long threadId, String content, User user) {
        Thread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new RuntimeException("Thread not found"));
        Post newPost = new Post(content, user, thread);
        return postRepository.save(newPost);
    }

    public List<Post> listContent(boolean includeNested) {
        if (includeNested) {
            return postRepository.findAllWithComments();
        } else {
            return postRepository.findAllWithoutComments();
        }
    }

    public Page<Post> getPagedPostsByUser(Long userId, Pageable pageable) {
        return postRepository.findPostsByUserId(userId, pageable);
    }
}
