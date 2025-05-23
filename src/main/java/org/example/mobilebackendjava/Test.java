package org.example.mobilebackendjava;

import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class Test {
    @GetMapping("/hello")
    public String sayHello() {
        return "Xin chào từ API Spring Boot!";
    }
}
