package com.LessonLab.forum.Models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name = "threads")
@Inheritance(strategy = InheritanceType.JOINED)

public class Thread extends Content {

    @Column(nullable = false)
    private String title;

    @Column(length = 1000) // Example length setting
    private String description;

    // One-to-many relationship with Post
    @OneToMany(mappedBy = "thread", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    //@JsonManagedReference
    private List<Post> posts = new ArrayList<>();

    // Constructors
    public Thread() {
    }

    public Thread(String title, String description) {
        this.title = title;
        this.description = description;
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public void addPost(Post post) {
        addToCollection(this.posts, post);
        post.setThread(this);
    }

    public void removePost(Post post) {
        removeFromCollection(this.posts, post);
        post.setThread(null);
    }
}
