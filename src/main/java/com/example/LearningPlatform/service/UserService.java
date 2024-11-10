package com.example.LearningPlatform.service;
import com.example.LearningPlatform.model.User;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    private final DynamoDbClient dynamoDbClient;
    public UserService() {
        this.dynamoDbClient = DynamoDbClient.builder()
                .region(Region.US_EAST_1) 
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();
    }
    public void createUser(String username, String password, String role) {
        Map<String, AttributeValue> itemValues = new HashMap<>();
        itemValues.put("username", AttributeValue.builder().s(username).build());
        itemValues.put("password", AttributeValue.builder().s(password).build());
        itemValues.put("role", AttributeValue.builder().s(role).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName("Users") 
                .item(itemValues)
                .build();

        dynamoDbClient.putItem(request);
    }
    public User validateUser(String username, String password) {
        Map<String, AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put("username", AttributeValue.builder().s(username).build());

        GetItemRequest request = GetItemRequest.builder()
                .tableName("Users") 
                .key(keyToGet)
                .build();

        try {
            Map<String, AttributeValue> returnedItem = dynamoDbClient.getItem(request).item();
            if (returnedItem != null && password.equals(returnedItem.get("password").s())) {
                User user = new User();
                user.setUsername(returnedItem.get("username").s());
                user.setPassword(returnedItem.get("password").s());
                user.setRole(returnedItem.get("role").s());
                return user;
            }
        } catch (DynamoDbException e) {
            e.printStackTrace();
        }
        return null;
    }
}
