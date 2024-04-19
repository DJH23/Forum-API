package com.LessonLab.forum.Models;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.JoinColumn;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
public class Post extends Content {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thread_id", nullable = false)
    private Thread thread;  // Each post belongs to one thread

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();  // One post can have many comments

    // Constructors
    public Post() {}

    // Getters and Setters
    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    // Additional methods to manage comments
    public void addComment(Comment comment) {
        comments.add(comment);
     //   comment.setPost(this);
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
      //  comment.setPost(null);
    }
}
