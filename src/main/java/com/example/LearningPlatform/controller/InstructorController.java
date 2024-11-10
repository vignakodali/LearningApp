package com.example.LearningPlatform.controller;

import com.example.LearningPlatform.model.Course;
import com.example.LearningPlatform.service.InstructorService;
import com.example.LearningPlatform.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/instructor")
public class InstructorController {

    @Autowired
    private InstructorService instructorService;

    @Autowired
    private S3Service s3Service;
    @GetMapping("/dashboard")
    public String showInstructorDashboard(Model model) {
        String instructorId = "1619";  
        model.addAttribute("courses", instructorService.getCoursesByInstructorId(instructorId));
        return "instructorDashboard";
    }
    @GetMapping("/course/{courseId}")
    public String showCourseDetail(@PathVariable String courseId, Model model) {
        Course course = instructorService.getCourseById(courseId);
        if (course != null) {
            model.addAttribute("course", course);
        } else {
            model.addAttribute("errorMessage", "Course not found.");
        }
        return "courseDetail";
    }
        @PostMapping("/course/{courseId}/uploadVideo")
    public String uploadVideo(@PathVariable String courseId, @RequestParam("video") MultipartFile video) {
        instructorService.uploadCourseMaterial(courseId, video, "video");
        return "redirect:/instructor/course/" + courseId;
    }

    @PostMapping("/course/{courseId}/uploadAssignment")
    public String uploadAssignment(@PathVariable String courseId, @RequestParam("assignment") MultipartFile assignment) {
        instructorService.uploadCourseMaterial(courseId, assignment, "assignment");
        return "redirect:/instructor/course/" + courseId;
    }

    @PostMapping("/course/{courseId}/uploadMaterial")
    public String uploadMaterial(@PathVariable String courseId, @RequestParam("material") MultipartFile material) {
        instructorService.uploadCourseMaterial(courseId, material, "material");
        return "redirect:/instructor/course/" + courseId;
    }

    @PostMapping("/course/{courseId}/deleteMaterial")
    public String deleteMaterial(@PathVariable String courseId, @RequestParam("materialKey") String materialKey, @RequestParam("type") String type) {
        System.out.println("Received request to delete file with key: " + materialKey);

        if (materialKey == null || materialKey.isEmpty()) {
            System.out.println("Error: materialKey is missing.");
            return "redirect:/instructor/course/" + courseId; 
        }

        String decodedKey = URLDecoder.decode(materialKey, StandardCharsets.UTF_8);
        
        String fullKey = String.format("courses/%s/%s/%s", courseId, type, decodedKey);
        System.out.println("Full key for deletion: " + fullKey);

        try {
            s3Service.deleteFile(fullKey); 
            System.out.println("Successfully deleted from S3: " + fullKey);
        } catch (Exception e) {
            System.out.println("Failed to delete file from S3: " + e.getMessage());
        }

        return "redirect:/instructor/course/" + courseId; 
    }

    @PostMapping("/course/{courseId}/addYoutubeLink")
    public String addYoutubeLink(@PathVariable String courseId, @RequestParam("youtubeLink") String youtubeLink) {
        instructorService.addYoutubeVideo(courseId, youtubeLink);
        return "redirect:/instructor/course/" + courseId;
    }
}
