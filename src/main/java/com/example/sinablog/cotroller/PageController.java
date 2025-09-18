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
}
