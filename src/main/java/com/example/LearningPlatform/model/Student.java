package com.example.LearningPlatform.model;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Student {
    private String studentId;
    private List<String> enrolledCourses;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public List<String> getEnrolledCourses() {
        return enrolledCourses;
    }

    public void setEnrolledCourses(List<String> enrolledCourses) {
        this.enrolledCourses = enrolledCourses;
    }

    public static Student fromMap(Map<String, AttributeValue> item) {
        Student student = new Student();

        student.studentId = item.containsKey("studentId") ? item.get("studentId").s() : null;

        student.enrolledCourses = item.containsKey("enrolledCourses") && item.get("enrolledCourses").hasL() 
            ? item.get("enrolledCourses").l().stream().map(AttributeValue::s).collect(Collectors.toList())
            : new ArrayList<>();

        return student;
    }

    public Map<String, AttributeValue> toMap() {
        Map<String, AttributeValue> map = new HashMap<>();
        
        if (this.studentId != null && !this.studentId.isEmpty()) {
            map.put("studentId", AttributeValue.builder().s(this.studentId).build());
        } else {
            throw new IllegalArgumentException("Student ID cannot be null or empty");
        }
    
        map.put("enrolledCourses", AttributeValue.builder()
                .ss(this.enrolledCourses != null ? this.enrolledCourses : Collections.emptyList())
                .build());
    
    
        return map;
    }
    

}