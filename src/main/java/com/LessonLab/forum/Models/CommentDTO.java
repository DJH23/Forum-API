package com.LessonLab.forum.Models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommentDTO {
    @JsonProperty("comment")
    private String content;
    private Long postId;

    // Getters and setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }
}
