package com.LessonLab.forum.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.*;

@Entity
@Table(name = "comments")
public class Comment extends Content {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "contentId")
    @JsonBackReference
    private Post post; // Each comment is associated with one post

    public Comment() {
    }

    // Existing constructor for other uses
    public Comment(String content, User user) {
        super(content, user);
    }

    // New constructor to be used in service for creating a new comment with a post
    // relation
    public Comment(String content, User user, Post post) {
        super(content, user);
        this.post = post;
        if (post != null && !post.getComments().contains(this)) {
            post.getComments().add(this);
        }
    }

    // Getters and setters

    @JsonProperty("commentId")
    public Long getContentId() {
        return super.getContentId();
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
        if (post != null && !post.getComments().contains(this)) {
            post.getComments().add(this);
        }
    }
}
