package org.example.mobilebackendjava.controller;

import org.example.mobilebackendjava.model.User;
import org.example.mobilebackendjava.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Phương thức lấy tất cả User từ Cloude Firestore bằng id
    @GetMapping("/getAllUsers")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Phương thức lấy User từ Cloude Firestore bằng id
    @GetMapping("/{id}")
    public User getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }

    // Cập nhật tt User
    @PutMapping("/{id}")
    public void updateUser(@PathVariable String id, @RequestBody User user) {
        userService.updateUser(id, user);
    }

}
