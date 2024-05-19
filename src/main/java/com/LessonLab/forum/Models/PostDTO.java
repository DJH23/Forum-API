package com.LessonLab.forum.Models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostDTO {
    @JsonProperty("post")
    private String content;
    private Long commentCount;
    private Long threadId;
    private Boolean showNestedComments;
    private List<CommentDTO> comments;

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

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }

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
