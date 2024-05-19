package com.LessonLab.forum.Models;

public class ContentUpdateDTO {
    private String newContent;

    // Constructors, getters, and setters
    public ContentUpdateDTO() {
    }

    public ContentUpdateDTO(String newContent) {
        this.newContent = newContent;
    }

    public String getNewContent() {
        return newContent;
    }

    public void setNewContent(String newContent) {
        this.newContent = newContent;
    }
}
