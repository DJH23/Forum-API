package com.LessonLab.forum.Models;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.util.ArrayList;
import java.util.List;

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

    // Add a method to add a post to the thread
    public void addPost(Post post) {
        posts.add(post);
        post.setThread(this);
    }

    // Remove a post from the thread
    public void removePost(Post post) {
        posts.remove(post);
        post.setThread(null);
    }  
}
