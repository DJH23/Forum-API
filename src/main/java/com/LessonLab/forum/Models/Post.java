package com.LessonLab.forum.Models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "posts")
@DiscriminatorValue("Post")
public class Post extends Content {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)

    @JoinColumn(name = "thread_id")
    @JsonBackReference
    private Thread thread; // Each post belongs to one thread

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Comment> comments = new ArrayList<>(); // One post can have many comments

    // No-args constructor
    public Post() {

    }

    // Constructor for the first post in a thread
    public Post(String content, UserExtension user, Thread thread) {
        super(content, user);
        this.thread = thread;
        thread.getPosts().add(this); // Add this post to the thread's posts list
    }

    // Constructor for subsequent posts in a thread
    public Post(String content, UserExtension user) {
        super(content, user);
        // No thread is passed, so we don't set it here
    }

    // Getters and Setters
    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        if (this.thread != null) {
            this.thread.getPosts().remove(this);
        }
        this.thread = thread;
        if (thread != null && !thread.getPosts().contains(this)) {
            thread.getPosts().add(this);
        }
    }

    @JsonProperty("postId")
    public Long getContentId() {
        return super.getContentId();
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    // Additional methods to manage comments
    public void addComment(Comment comment) {
        addToCollection(comments, comment);
        comment.setPost(this);
    }

    public void removeComment(Comment comment) {
        removeFromCollection(comments, comment);
        comment.setPost(null);
    }

}
