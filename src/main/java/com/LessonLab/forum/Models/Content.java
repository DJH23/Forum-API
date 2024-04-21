package com.LessonLab.forum.Models;

import java.util.List;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   // @NotNull
    @Lob  // Assuming content could be large, use Lob if it is expected to be large text
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    // Default constructor
    public Content() {
    }

    // Constructor that initializes content
    public Content(String content) {
        this.content = content;
    }

    protected <T> void addToCollection(List<T> collection, T item) {
        if (!collection.contains(item)) {
            collection.add(item);
        }
    }

    protected <T> void removeFromCollection(List<T> collection, T item) {
        collection.remove(item);
    }

    private int upvotes = 0;
    private int downvotes = 0;

    public void upVote() {
        this.upvotes += 1;
    }

    public void downVote() {
        this.downvotes += 1;
    }

    public boolean checkThreshold(int threshold) {
        return this.downvotes > threshold;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    public int getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(int downvotes) {
        this.downvotes = downvotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

