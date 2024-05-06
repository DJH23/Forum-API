package com.LessonLab.forum.Models;

public class PostDTO {
    private String content;
    private Long threadId; // Assuming thread relation is maintained via ID

    // Getters and setters
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
}
