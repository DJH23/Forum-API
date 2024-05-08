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
import com.LessonLab.forum.Models.PostDTO;
import com.LessonLab.forum.Models.Thread;
import com.LessonLab.forum.Models.Content;
import com.LessonLab.forum.Repositories.PostRepository;
import com.LessonLab.forum.Repositories.ThreadRepository;

@Service
public class PostService extends ContentService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ThreadRepository threadRepository;

    @Autowired
    private ThreadService threadService;

    /*
     * @Transactional
     * public Post addPost(Post post, User user) {
     * Thread thread = threadService.getThread(post.getThread().getContentId());
     * post.setThread(thread);
     * return (Post) addContent(post, user);
     * }
     */
    /*
     * @Transactional
     * public Post updatePost(Long id, String newContent, User user) {
     * Post post = (Post) contentRepository.findById(id).get();
     * Content updatedContent = updateContent(post.getContentId(), newContent,
     * user);
     * post.setContent(updatedContent.getContent());
     * return post;
     * }
     */

    /*
     * public Post getPost(Long id) {
     * return (Post) getContent(id);
     * }
     */

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

    public Page<Post> getRecentContents(Pageable pageable) {
        Page<Content> contents = super.contentRepository.findRecentContents(pageable);
        List<Post> posts = contents.stream()
                .filter(content -> content instanceof Post)
                .map(content -> (Post) content)
                .collect(Collectors.toList());
        return new PageImpl<>(posts, pageable, posts.size());
    }

    /*
     * public List<Post> searchPosts(String searchText) {
     * List<Content> contents = searchContent(searchText);
     * return contents.stream().map(content -> (Post)
     * content).collect(Collectors.toList());
     * }
     */

    /*
     * public Page<Post> getPagedPostsByUser(Long userId, Pageable pageable) {
     * Page<Content> contents = getPagedContentByUser(userId, pageable);
     * return new PageImpl<>(contents.getContent().stream().map(content -> (Post)
     * content).collect(Collectors.toList()), pageable,
     * contents.getTotalElements());
     * }
     */

    /*
     * public List<Post> getPostsByCreatedAtBetween(LocalDateTime start,
     * LocalDateTime end) {
     * List<Content> contents = getContentsByCreatedAtBetween(start, end);
     * return contents.stream().map(content -> (Post)
     * content).collect(Collectors.toList());
     * }
     */

    /*
     * public List<Post> getPostsByContentContaining(String text) {
     * List<Content> contents = getContentsByContentContaining(text);
     * return contents.stream().map(content -> (Post)
     * content).collect(Collectors.toList());
     * }
     */

    /*
     * @Transactional
     * public void deletePost(Long postId, User user) {
     * super.deleteContent(postId, user);
     * }
     */

    /*
     * public List<Post> listPosts() {
     * List<Content> contents = super.listContent();
     * return contents.stream()
     * .filter(content -> content instanceof Post)
     * .map(content -> (Post) content)
     * .collect(Collectors.toList());
     * }
     */

    /*
     * public void handlePostVote(Long postId, Long userId, boolean isUpVote) {
     * super.handleVote(postId, userId, isUpVote);
     * }
     */

    public Post addContent(PostDTO dto, User user) {
        Post post = convertToPostEntity(dto, user);
        return postRepository.save(post);
    }

    private Post convertToPostEntity(PostDTO dto, User user) {
        Thread thread = threadRepository.findById(dto.getThreadId())
                .orElseThrow(() -> new RuntimeException("Thread not found"));
        return new Post(dto.getContent(), user, thread);
    }

    public List<Post> listContent(boolean includeNested) {
        if (includeNested) {
            return postRepository.findAllWithComments();
        } else {
            return postRepository.findAllWithoutComments();
        }
    }
}
