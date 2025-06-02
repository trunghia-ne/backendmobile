package org.example.mobilebackendjava.controller;

import org.example.mobilebackendjava.model.User;
import org.example.mobilebackendjava.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users") // số nhiều để đúng RESTful
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET /api/users => Lấy danh sách tất cả người dùng
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // GET /api/users/{id} => Lấy thông tin 1 user theo id
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    // PUT /api/users/{id} => Cập nhật thông tin user
    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable String id, @RequestBody User user) {
        userService.updateUser(id, user);
        return ResponseEntity.ok("Cập nhật người dùng thành công");
    }
}
