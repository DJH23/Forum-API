package com.LessonLab.forum.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Models.Post;
import com.LessonLab.forum.Models.Thread;
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
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + id));
    }

    public List<Post> getPostsByThread(Thread thread) {
        return postRepository.findByThread(thread);
    }

    public List<Post> getPostsByCommentContent(String content) {
        return postRepository.findByCommentContent(content);
    }

    public List<Post> getMostCommentedPosts(Pageable pageable) {
        return postRepository.findMostCommentedPosts(pageable);
    }

    @Transactional
    public void deletePost(Long postId, User user) {
        super.deleteContent(postId, user);
    }

}
