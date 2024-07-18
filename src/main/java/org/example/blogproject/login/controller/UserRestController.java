package org.example.blogproject.login.controller;

import lombok.RequiredArgsConstructor;
import org.example.blogproject.login.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserRestController {
    private final UserService userService;

    @GetMapping("/check-username")
    public Map<String, Boolean> checkUsername(@RequestParam String username) { // 수정된 부분
        boolean available = !userService.existsByUsername(username); // 수정된 부분
        return Collections.singletonMap("available", available);
    }

    @GetMapping("/check-email")
    public Map<String, Boolean> checkEmail(@RequestParam String email) {
        boolean available = !userService.existsByEmail(email);
        return Collections.singletonMap("available", available);
    }
}
