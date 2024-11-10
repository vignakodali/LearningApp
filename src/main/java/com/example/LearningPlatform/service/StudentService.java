package com.example.LearningPlatform.service;

import com.example.LearningPlatform.model.Course;
import com.example.LearningPlatform.model.Student;
import com.example.LearningPlatform.repository.CourseRepository;
import com.example.LearningPlatform.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudentService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    public List<Course> getAllCourses() {
        return courseRepository.findAllCourses();
    }

    public void enrollInCourse(String studentId, String courseId) {
        if (studentId == null || studentId.isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be null or empty");
        }
    
        Student student = studentRepository.findStudentById(studentId);
    
        if (student == null) {
            student = new Student();
            student.setStudentId(studentId);  
            student.setEnrolledCourses(new ArrayList<>());
        }
    
        if (!student.getEnrolledCourses().contains(courseId)) {
            student.getEnrolledCourses().add(courseId);
            studentRepository.saveStudent(student);
        }
    }
        public List<Course> getEnrolledCourses(String studentId) {
        Student student = studentRepository.findStudentById(studentId);
        if (student == null || student.getEnrolledCourses() == null) {
            return new ArrayList<>();
        }
        return courseRepository.findCoursesByIds(student.getEnrolledCourses());
    }
}

