package com.LessonLab.forum.Models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostDTO {
    @JsonProperty("post")
    private String content;
    private Long commentCount;
    private Long threadId;
    private Boolean showNestedComments;

    public PostDTO(String content, Long threadId, Long commentCount) {
        this.content = content;
        this.threadId = threadId;
        this.commentCount = commentCount;
        this.showNestedComments = false; 
    }

    public PostDTO(String content, Long threadId) {
        this.content = content;
        this.threadId = threadId;
    }

    // Getters and setters

    public Long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getThreadId() {
        return threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }

    public Boolean getShowNestedComments() {
        return showNestedComments;
    }

    public void setShowNestedComments(Boolean showNestedComments) {
        this.showNestedComments = showNestedComments;
    }
}
