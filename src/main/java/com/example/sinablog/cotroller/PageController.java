package com.example.sinablog.cotroller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@CrossOrigin(allowedHeaders = "*", origins = "*")

public class PageController {


    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/post-details")
    public String postDetails() {
        return "post-details";
    }



    // 📌 صفحات احراز هویت
    @GetMapping("/auth/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/auth/register")
    public String register() {
        return "auth/register";
    }

    // 📌 صفحات مدیریت (Admin)
    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/admin/users")
    public String adminUsers() {
        return "admin/users";
    }

    @GetMapping("/admin/posts")
    public String adminPosts() {
        return "admin/posts/posts"; // مسیر thymeleaf
    }

    @GetMapping("/admin/posts/new")
    public String adminNewPost() {
        return "admin/posts/new";
    }

    @GetMapping("/admin/posts/edit")
    public String adminEditPost() {
        return "admin/posts/edit";
    }
    @GetMapping("/admin/categories")
    public String adminCategories() {
        return "admin/categories";
    }

    @GetMapping("/admin/tags")
    public String adminTags() {
        return "admin/tags";
    }

    @GetMapping("/admin/comments")
    public String adminComments() {
        return "admin/comments";
    }


    @GetMapping("/access-denied")
    public String accessDenied() {
        return "403";
    }

    @GetMapping("/error")
    public String handleError() {
        return "error"; // به error.html redirect می‌شود
    }

    @GetMapping("/403")
    public String handleAccessDenied() {
        return "403"; // به 403.html redirect می‌شود
    }
}