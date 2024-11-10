Learning Platform
This is a Learning Platform project built with Java, Spring Boot, and DynamoDB, enabling users to enroll in courses and view a personalized dashboard. The application includes authentication integration with AWS Cognito.

Table of Contents
Project Overview
Features
Technologies Used
Setup Instructions
Running the Application
API Endpoints
AWS Services Integration
Troubleshooting
License

Project Overview
This project is a web-based platform designed to facilitate student enrollment in courses. Users can log in, view available courses, and enroll in them. The system stores user and course data in DynamoDB and uses AWS Cognito for authentication and user management.

Features
User Authentication: Login integration with AWS Cognito.
Course Management: View all available courses, enroll in courses, and see enrolled courses.
Personal Dashboard: Each user has a dashboard showing enrolled courses.
Instructor & Student Roles: Different views based on user roles.
Technologies Used
Java 
Spring Boot 
AWS DynamoDB for data storage
AWS Cognito for user authentication
Thymeleaf for server-side rendering
Gradle/Maven for dependency management

Setup Instructions
Prerequisites
Java - Ensure Java JDK is installed .
AWS Account - Set up a DynamoDB table and a Cognito User Pool in AWS.
AWS CLI - Install AWS CLI and configure it with your AWS credentials.

Installation
Clone the Repository:

bash
Copy code
git clone https://github.com/vignakodali/LearningApp.git
cd LearningPlatform
Configure AWS Credentials: Ensure your AWS credentials are set up to allow access to DynamoDB and Cognito.

Update Application Properties: Update application.properties with the necessary AWS credentials and Cognito/DynamoDB settings:

properties
Copy code
aws.cognito.clientId=your_client_id
aws.cognito.userPoolId=your_user_pool_id
aws.dynamodb.endpoint=https://dynamodb.region.amazonaws.com
Install Dependencies:

bash
Copy code
./gradlew build
# OR
mvn clean install
Running the Application
Start the Application:

bash
Copy code
./gradlew bootRun
# OR
mvn spring-boot:run
Access the Web Application: Visit http://localhost:8080 to access the platform.

API Endpoints
Authentication
POST /login: Logs in the user with credentials verified via AWS Cognito.
Courses
GET /student/courses: View all available courses.
POST /student/enroll/{courseId}: Enroll in a course by courseId.
GET /student/my-courses: View enrolled courses for the logged-in user.
Dashboard
GET /student/dashboard: Displays the user's dashboard with enrolled courses.
AWS Services Integration
DynamoDB: Stores user and course information.
Cognito: Manages user login and authentication.
DynamoDB Schema
Student Table: Stores studentId, username, and enrolledCourses (array of course IDs).
Course Table: Stores course details (such as courseId, courseName, etc.).
Cognito Setup
Ensure your Cognito User Pool has an app client configured for login. This client ID should match the aws.cognito.clientId specified in application.properties.

Troubleshooting
DynamoDB Schema Mismatch: If you encounter errors related to DynamoDB schema (e.g., The provided key element does not match the schema), verify that the attributes in DynamoDB match the application's expected attributes.

Cognito Authentication Failure: Ensure that the Cognito clientId and userPoolId are correctly specified in the application.properties file.

Session Errors: If session attributes such as studentId or username are not persisting, check session handling in UserController and StudentController.
