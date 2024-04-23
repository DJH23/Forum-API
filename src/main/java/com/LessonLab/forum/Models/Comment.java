package com.LessonLab.forum.Models;

import jakarta.persistence.*;

@Entity
@Table(name = "comments")
public class Comment extends Content {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id")
    private Post post;  // Each comment is associated with one post

    // Constructors
    public Comment() {
        
    }

    public Comment(String content, User user) {
        super(content, user);
    }

    // Getter and setter for post
    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
        // Ensure the comment is added to the post's comment collection
        if (post != null && !post.getComments().contains(this)) {
            post.getComments().add(this);
        }
    }
}
