
package com.example.LearningPlatform.repository;

import com.example.LearningPlatform.model.Course;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class CourseRepository {

    private final DynamoDbClient dynamoDbClient;
    private final String tableName = "Courses"; 

    public CourseRepository(
            @Value("${aws.accessKeyId}") String accessKey,
            @Value("${aws.secretAccessKey}") String secretKey,
            @Value("${aws.region}") String region
    ) {
        this.dynamoDbClient = DynamoDbClient.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                )
                .build();
    }
    
    public String saveCourse(Course course) {
        Map<String, AttributeValue> item = course.toMap();
        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build();

        dynamoDbClient.putItem(request);
        return course.getCourseId();
    }

    public Course findCourseById(String courseId) {
        GetItemRequest request = GetItemRequest.builder()
                .tableName(tableName)
                .key(Map.of("courseId", AttributeValue.builder().s(courseId).build()))
                .build();

        Map<String, AttributeValue> item = dynamoDbClient.getItem(request).item();
        return item != null ? Course.fromMap(item) : null;
    }

    public List<Course> findCoursesByInstructorId(String instructorId) {
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName(tableName)
                .filterExpression("instructorId = :instructorId")
                .expressionAttributeValues(Map.of(":instructorId", AttributeValue.builder().s(instructorId).build()))
                .build();

        ScanResponse response = dynamoDbClient.scan(scanRequest);
        List<Course> courses = new ArrayList<>();

        for (Map<String, AttributeValue> item : response.items()) {
            courses.add(Course.fromMap(item));
        }

        return courses;
    }

    public List<Course> findAllCourses() {
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName(tableName)
                .build();

        ScanResponse response = dynamoDbClient.scan(scanRequest);
        List<Course> courses = new ArrayList<>();

        for (Map<String, AttributeValue> item : response.items()) {
            courses.add(Course.fromMap(item));
        }

        return courses;
    }

    public List<Course> findCoursesByIds(List<String> courseIds) {
        List<Course> courses = new ArrayList<>();

        for (String courseId : courseIds) {
            Course course = findCourseById(courseId);
            if (course != null) {
                courses.add(course);
            }
        }

        return courses;
    }
}