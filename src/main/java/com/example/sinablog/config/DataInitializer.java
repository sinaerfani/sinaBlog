package com.example.sinablog.config;

import com.example.sinablog.Service.Role.RoleService;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    private final RoleService roleService;

    public DataInitializer(RoleService roleService) {
        this.roleService = roleService;
    }
    @PostConstruct
    public void init() {
        try {
            roleService.initializeDefaultRoles();
        } catch (Exception e) {
            System.err.println("Failed to initialize default roles: " + e.getMessage());
        }
    }
}
