package org.example.mobilebackendjava.controller;

import org.example.mobilebackendjava.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private UserService firebaseService;

    public UserController(UserService firebaseService) {
        this.firebaseService = firebaseService;
    }

    @PostMapping("/saveUser")
    public String saveUser(@RequestParam String id, @RequestParam String name, @RequestParam String email) {
        firebaseService.saveUser(id, name, email);
        return "Saved user " + name;
    }

    @GetMapping("/getUserById")
    public Map<String, Object> getUser(@RequestParam String id) throws Exception {
        return firebaseService.getUserById(id);
    }
}
