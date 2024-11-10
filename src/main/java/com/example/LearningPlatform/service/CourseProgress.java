package com.example.LearningPlatform.service;

public class CourseProgress {
    private String courseId;
    private int progressPercentage; 
    private String status; 

    public CourseProgress() {}

    public CourseProgress(String courseId, int progressPercentage, String status) {
        this.courseId = courseId;
        this.progressPercentage = progressPercentage;
        this.status = status;
    }
    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public int getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(int progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
