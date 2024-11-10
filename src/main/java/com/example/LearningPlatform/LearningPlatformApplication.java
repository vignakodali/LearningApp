package com.example.LearningPlatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.LearningPlatform")
public class LearningPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(LearningPlatformApplication.class, args);
	}

}
