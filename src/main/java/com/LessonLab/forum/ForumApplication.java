package com.LessonLab.forum;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EntityScan("com.LessonLab.forum.Models")  // Ensure this is correct and includes all model packages
@EnableJpaRepositories(basePackages = "com.LessonLab.forum.Repositories")
public class ForumApplication {
    public static void main(String[] args) {
        SpringApplication.run(ForumApplication.class, args);
    }
}
