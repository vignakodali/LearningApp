package com.example.LearningPlatform.controller;

import com.example.LearningPlatform.model.Course;
import com.example.LearningPlatform.service.StudentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/courses")
    public String viewCourses(Model model) {
        List<Course> courses = studentService.getAllCourses();
        model.addAttribute("courses", courses);
        return "student/courses"; 
    }

    @PostMapping("/enroll/{courseId}")
    public String enrollInCourse(@PathVariable String courseId, HttpSession session) {
        String studentId = (String) session.getAttribute("studentId");

        if (studentId == null || studentId.isEmpty()) {
            throw new IllegalArgumentException("Student ID is required for enrollment");
        }
        if (courseId == null || courseId.isEmpty()) {
            throw new IllegalArgumentException("Course ID is required for enrollment");
        }

        System.out.println("Attempting to enroll studentId: " + studentId + " in courseId: " + courseId);

        studentService.enrollInCourse(studentId, courseId);
        return "redirect:/student/my-courses";
    }

    @GetMapping("/my-courses")
    public String viewEnrolledCourses(HttpSession session, Model model) {
        String studentId = (String) session.getAttribute("studentId");
        if (studentId != null) {
            List<Course> courses = studentService.getEnrolledCourses(studentId);
            model.addAttribute("courses", courses);
            return "student/my-courses";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/dashboard")
    public String showStudentDashboard(HttpSession session, Model model) {
        String studentId = (String) session.getAttribute("studentId");
        if (studentId != null) {
            model.addAttribute("enrolledCourses", studentService.getEnrolledCourses(studentId));
            return "studentDashboard";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/explore-courses")
    public String exploreCourses(Model model) {
        List<Course> courses = studentService.getAllCourses();
        model.addAttribute("courses", courses);
        return "exploreCourses";
    }
}

