package com.example.LearningPlatform.repository;

import com.example.LearningPlatform.model.Student;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.Map;

@Repository
public class StudentRepository {

    private final DynamoDbClient dynamoDbClient;
    private final String tableName = "Students";

    public StudentRepository(
            @Value("${aws.accessKeyId}") String accessKey,
            @Value("${aws.secretAccessKey}") String secretKey,
            @Value("${aws.region}") String region
    ) {
        this.dynamoDbClient = DynamoDbClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    public String saveStudent(Student student) {
        Map<String, AttributeValue> item = student.toMap();
    
        item.forEach((key, value) -> {
            System.out.println("Key: " + key + ", Value: " + (value != null ? value.toString() : "null"));
            if (value == null || (value.s() != null && value.s().isEmpty())) {
                throw new IllegalArgumentException("Attribute '" + key + "' is empty or null, which is not allowed by DynamoDB.");
            }
        });
    
        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build();
    
        dynamoDbClient.putItem(request);
        return student.getStudentId();
    }
    

    public Student findStudentById(String studentId) {
        GetItemRequest request = GetItemRequest.builder()
                .tableName(tableName)
                .key(Map.of("studentId", AttributeValue.builder().s(studentId).build()))
                .build();

        Map<String, AttributeValue> item = dynamoDbClient.getItem(request).item();
        return item != null ? Student.fromMap(item) : null;
    }
}