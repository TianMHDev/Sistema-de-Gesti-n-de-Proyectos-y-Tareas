package com.projectmanagement.infrastructure.config;

import com.projectmanagement.application.dto.RegisterRequest;
import com.projectmanagement.application.port.input.AuthUseCase;
import com.projectmanagement.application.port.input.CreateProjectUseCase;
import com.projectmanagement.application.port.input.CreateTaskUseCase;
import com.projectmanagement.domain.model.Project;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.UUID;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(AuthUseCase authUseCase,
            CreateProjectUseCase createProjectUseCase,
            CreateTaskUseCase createTaskUseCase,
            UserDetailsService userDetailsService) {
        return args -> {
            try {
                // Register Demo User
                RegisterRequest regRequest = new RegisterRequest();
                regRequest.setUsername("admin");
                regRequest.setPassword("123456"); // Will be encoded by AuthUseCase
                regRequest.setEmail("admin@test.com");

                authUseCase.register(regRequest);
                System.out.println("INITIALIZER: Registered admin user");

                // Authenticate manually to create projects (since service uses CurrentUserPort)
                // In a real scenario, we might verify if user exists first to avoid duplicates,
                // but AuthUseCase.register throws if exists, so we catch exception.

                // Mock Authentication Context for initial data creation
                UserDetails userDetails = userDetailsService.loadUserByUsername("admin");
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);

                // Create Project
                Project p1 = createProjectUseCase.createProject("Website Redesign", "Overhaul the company website.");
                createTaskUseCase.createTask(p1.getId(), "Design Mockups", "Figma files");
                createTaskUseCase.createTask(p1.getId(), "Setup React Repo", "Init commit");

                Project p2 = createProjectUseCase.createProject("Mobile App", "Android & iOS");
                createTaskUseCase.createTask(p2.getId(), "Market Research", "Competitor analysis");

                System.out.println("INITIALIZER: Created demo projects and tasks");

            } catch (Exception e) {
                System.out.println("INITIALIZER: Data likely already exists or failed: " + e.getMessage());
            }
        };
    }
}
