package com.example.LearningPlatform.model;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Course {

    private String courseId;
    private String title;
    private String description;
    private String instructorId;
    private List<String> videos = new ArrayList<>(); 
    private List<String> assignments = new ArrayList<>();
    private List<String> materials = new ArrayList<>();

    public Course() {}

    public Course(String courseId, String title, String description, String instructorId) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.instructorId = instructorId;
    }


    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }

    public List<String> getVideos() {
        return videos;
    }

    public void setVideos(List<String> videos) {
        this.videos = videos;
    }

    public List<String> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<String> assignments) {
        this.assignments = assignments;
    }

    public List<String> getMaterials() {
        return materials;
    }

    public void setMaterials(List<String> materials) {
        this.materials = materials;
    }

    public Map<String, AttributeValue> toMap() {
        Map<String, AttributeValue> item = new HashMap<>();

        if (courseId != null) {
            item.put("courseId", AttributeValue.builder().s(courseId).build());
        }
        if (title != null) {
            item.put("title", AttributeValue.builder().s(title).build());
        }
        if (description != null) {
            item.put("description", AttributeValue.builder().s(description).build());
        }
        if (instructorId != null) {
            item.put("instructorId", AttributeValue.builder().s(instructorId).build());
        }
        if (videos != null) {
            item.put("videos", AttributeValue.builder().l(
                    videos.stream()
                            .map(v -> AttributeValue.builder().s(v).build())
                            .collect(Collectors.toList())
            ).build());
        }
        if (assignments != null) {
            item.put("assignments", AttributeValue.builder().l(
                    assignments.stream()
                            .map(a -> AttributeValue.builder().s(a).build())
                            .collect(Collectors.toList())
            ).build());
        }
        if (materials != null) {
            item.put("materials", AttributeValue.builder().l(
                    materials.stream()
                            .map(m -> AttributeValue.builder().s(m).build())
                            .collect(Collectors.toList())
            ).build());
        }

        return item;
    }

    public static Course fromMap(Map<String, AttributeValue> item) {
        Course course = new Course();

        if (item.containsKey("courseId") && item.get("courseId").s() != null) {
            course.setCourseId(item.get("courseId").s());
        }
        if (item.containsKey("title") && item.get("title").s() != null) {
            course.setTitle(item.get("title").s());
        }
        if (item.containsKey("description") && item.get("description").s() != null) {
            course.setDescription(item.get("description").s());
        }
        if (item.containsKey("instructorId") && item.get("instructorId").s() != null) {
            course.setInstructorId(item.get("instructorId").s());
        }
        if (item.containsKey("videos") && item.get("videos").l() != null) {
            course.setVideos(
                item.get("videos").l().stream()
                    .map(AttributeValue::s)
                    .collect(Collectors.toList())
            );
        }
        if (item.containsKey("assignments") && item.get("assignments").l() != null) {
            course.setAssignments(
                item.get("assignments").l().stream()
                    .map(AttributeValue::s)
                    .collect(Collectors.toList())
            );
        }
        if (item.containsKey("materials") && item.get("materials").l() != null) {
            course.setMaterials(
                item.get("materials").l().stream()
                    .map(AttributeValue::s)
                    .collect(Collectors.toList())
            );
        }

        return course;
    }
}
