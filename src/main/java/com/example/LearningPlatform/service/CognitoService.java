package com.example.LearningPlatform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.HashMap;
import java.util.Map;

@Service
public class CognitoService {

    private final CognitoIdentityProviderClient cognitoClient;
    private final String userPoolId = "us-east-1_juiwI2wsp"; 
    private final String clientId = "36fjrr8pl8i9rtouvqfoj4i5vl"; 
    public CognitoService() {
        this.cognitoClient = CognitoIdentityProviderClient.builder()
                .region(Region.US_EAST_1) 
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();
    }

    @Autowired
    public CognitoService(CognitoIdentityProviderClient cognitoClient) {
        this.cognitoClient = cognitoClient;
    }

    public boolean signUpUser(String username, String email, String password, String role) {
        try {
            SignUpRequest signUpRequest = SignUpRequest.builder()
                    .clientId(clientId)
                    .username(username)
                    .password(password)
                    .userAttributes(
                            AttributeType.builder().name("email").value(email).build(),
                            AttributeType.builder().name("custom:role").value(role).build() 
                    )
                    .build();

            cognitoClient.signUp(signUpRequest);
            return true;
        } catch (CognitoIdentityProviderException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String authenticateUser(String username, String password) {
        try {
            Map<String, String> authParams = new HashMap<>();
            authParams.put("USERNAME", username);
            authParams.put("PASSWORD", password);

            AdminInitiateAuthRequest authRequest = AdminInitiateAuthRequest.builder()
                    .userPoolId(userPoolId)
                    .clientId(clientId)
                    .authFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH) 
                    .authParameters(authParams)
                    .build();

            AdminInitiateAuthResponse response = cognitoClient.adminInitiateAuth(authRequest);
            if (response.authenticationResult() != null) {
                Map<String, String> userAttributes = getUserAttributes(username);
                return userAttributes.get("custom:role"); 
            }

        } catch (CognitoIdentityProviderException e) {
            e.printStackTrace();
        }

        return null; 
    }

    public boolean confirmUser(String username, String confirmationCode) {
        try {
            ConfirmSignUpRequest confirmRequest = ConfirmSignUpRequest.builder()
                    .clientId(clientId)
                    .username(username)
                    .confirmationCode(confirmationCode)
                    .build();

            cognitoClient.confirmSignUp(confirmRequest);
            return true;
        } catch (CognitoIdentityProviderException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Map<String, String> getUserAttributes(String username) {
        AdminGetUserRequest getUserRequest = AdminGetUserRequest.builder()
                .userPoolId(userPoolId)
                .username(username)
                .build();

        AdminGetUserResponse response = cognitoClient.adminGetUser(getUserRequest);

        Map<String, String> attributes = new HashMap<>();
        for (AttributeType attribute : response.userAttributes()) {
            attributes.put(attribute.name(), attribute.value());
        }

        return attributes;
    }
}
