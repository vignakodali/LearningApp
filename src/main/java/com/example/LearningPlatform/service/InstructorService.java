package com.example.LearningPlatform.service;

import com.example.LearningPlatform.model.Course;
import com.example.LearningPlatform.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InstructorService {
    private static final Logger logger = LoggerFactory.getLogger(InstructorService.class);

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private S3Service s3Service;

    public String createCourse(String title, String description, String instructorId) {
        Course course = new Course();
        course.setCourseId(generateUniqueId());
        course.setTitle(title);
        course.setDescription(description);
        course.setInstructorId(instructorId);
        return courseRepository.saveCourse(course);
    }

    public List<Course> getCoursesByInstructorId(String instructorId) {
        return courseRepository.findCoursesByInstructorId(instructorId);
    }

    public Course getCourseById(String courseId) {
        Course course = courseRepository.findCourseById(courseId);

        if (course == null) {
            logger.warn("Course with ID {} not found", courseId);
            return null;
        }

        if (course.getVideos() == null) course.setVideos(new ArrayList<>());
        if (course.getAssignments() == null) course.setAssignments(new ArrayList<>());
        if (course.getMaterials() == null) course.setMaterials(new ArrayList<>());

        List<String> videoUrls = s3Service.listMaterialsWithPresignedUrls("courses/" + courseId + "/video").stream()
                .map(URL::toString)
                .map(this::getDecodedFileName)
                .collect(Collectors.toList());

        List<String> assignmentUrls = s3Service.listMaterialsWithPresignedUrls("courses/" + courseId + "/assignment").stream()
                .map(URL::toString)
                .map(this::getDecodedFileName)
                .collect(Collectors.toList());

        List<String> materialUrls = s3Service.listMaterialsWithPresignedUrls("courses/" + courseId + "/material").stream()
                .map(URL::toString)
                .map(this::getDecodedFileName)
                .collect(Collectors.toList());

        logger.debug("Processed video URLs for course {}: {}", courseId, videoUrls);
        logger.debug("Processed assignment URLs for course {}: {}", courseId, assignmentUrls);
        logger.debug("Processed material URLs for course {}: {}", courseId, materialUrls);

        course.setVideos(videoUrls);
        course.setAssignments(assignmentUrls);
        course.setMaterials(materialUrls);

        return course;
    }

    public void uploadCourseMaterial(String courseId, MultipartFile file, String type) {
        String keyPrefix = "courses/" + courseId + "/" + type;
        String contentType = file.getContentType();
        try {
            s3Service.uploadFile(keyPrefix, file, contentType);
            logger.info("Uploaded {} to S3 under course {}.", type, courseId);
        } catch (Exception e) {
            logger.error("Failed to upload {} for course {}. Error: {}", type, courseId, e.getMessage(), e);
        }
    }

    public void addYoutubeVideo(String courseId, String youtubeLink) {
        Course course = courseRepository.findCourseById(courseId);
        if (course != null) {
            if (course.getVideos() == null) {
                course.setVideos(new ArrayList<>());
            }

            if (youtubeLink.contains("youtube.com") || youtubeLink.contains("youtu.be")) {
                course.getVideos().add(youtubeLink);
                courseRepository.saveCourse(course);
                logger.debug("Added YouTube video link {} to course {}. Updated videos: {}", youtubeLink, courseId, course.getVideos());
            } else {
                logger.warn("Invalid YouTube URL format: {}", youtubeLink);
            }
        } else {
            logger.warn("Course with ID {} not found", courseId);
        }
    }

    private String getDecodedFileName(String url) {
        try {
            int lastSlashIndex = url.lastIndexOf('/');
            if (lastSlashIndex == -1 || lastSlashIndex + 1 >= url.length()) {
                return url;
            }
            int endIndex = url.contains("?") ? url.indexOf('?') : url.length();
            if (lastSlashIndex + 1 <= endIndex) {
                String encodedFileName = url.substring(lastSlashIndex + 1, endIndex);
                return URLDecoder.decode(encodedFileName, StandardCharsets.UTF_8.name());
            } else {
                return url;
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to decode filename", e);
        }
    }

    private String generateUniqueId() {
        return UUID.randomUUID().toString();
    }
}
