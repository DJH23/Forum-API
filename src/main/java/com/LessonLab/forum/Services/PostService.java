package com.LessonLab.forum.Services;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Models.Post;
import com.LessonLab.forum.Models.Thread;
import com.LessonLab.forum.Models.Content;
import com.LessonLab.forum.Repositories.PostRepository;

@Service
public class PostService extends ContentService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ThreadService threadService;

    @Transactional
    public Post addPost(Post post, User user) {
        Thread thread = threadService.getThread(post.getThread().getId());
        post.setThread(thread);
        return (Post) addContent(post, user);
    }

    @Transactional
    public Post updatePost(Long id, String newContent, User user) {
        Post post = getPost(id);
        post.setContent(newContent);
        return (Post) updateContent(id, newContent, user);
    }

    public Post getPost(Long id) {
        return (Post) getContent(id);
    }

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
    
    public List<Post> getMostCommentedPosts(Pageable pageable) {
        try {
            if (pageable == null) {
                throw new IllegalArgumentException("Pageable cannot be null");
            }
            return postRepository.findMostCommentedPosts(pageable);
        } catch (Exception e) {
            // Log the exception and rethrow it
            System.err.println("Error getting most commented posts: " + e.getMessage());
            throw e;
        }
    }

    public List<Post> searchPosts(String searchText) {
        List<Content> contents = searchContent(searchText);
        return contents.stream().map(content -> (Post) content).collect(Collectors.toList());
    }
    
    public Page<Post> getPagedPostsByUser(Long userId, Pageable pageable) {
        Page<Content> contents = getPagedContentByUser(userId, pageable);
        return new PageImpl<>(contents.getContent().stream().map(content -> (Post) content).collect(Collectors.toList()), pageable, contents.getTotalElements());
    }
    
    public List<Post> getPostsByCreatedAtBetween(LocalDateTime start, LocalDateTime end) {
        List<Content> contents = getContentsByCreatedAtBetween(start, end);
        return contents.stream().map(content -> (Post) content).collect(Collectors.toList());
    }
    
    public List<Post> getPostsByContentContaining(String text) {
        List<Content> contents = getContentsByContentContaining(text);
        return contents.stream().map(content -> (Post) content).collect(Collectors.toList());
    }

    @Transactional
    public void deletePost(Long postId, User user) {
        super.deleteContent(postId, user);
    }

}
