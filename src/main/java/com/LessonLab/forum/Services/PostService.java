package com.LessonLab.forum.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.LessonLab.forum.Models.Post;
import com.LessonLab.forum.Repositories.PostRepository;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public List<Post> getPostsByThread(Long threadId) {
        return postRepository.findByThreadId(threadId);
    }

    @Transactional
    public Post createOrUpdatePost(Post post) {
        return postRepository.save(post);
    }

   /*  @Transactional
    public Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with ID: " + postId));
    }*/

}
