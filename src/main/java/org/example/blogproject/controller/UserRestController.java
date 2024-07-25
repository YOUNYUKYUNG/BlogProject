package org.example.blogproject.controller;

import lombok.RequiredArgsConstructor;
import org.example.blogproject.domain.User;
import org.example.blogproject.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserRestController {
    private final UserService userService;

    @GetMapping("/check-username")
    public Map<String, Boolean> checkUsername(@RequestParam String username) {
        boolean available = !userService.existsByUsername(username); // 사용자 이름 사용 가능 여부 확인
        return Collections.singletonMap("available", available); // 사용 가능 여부 반환
    }

    @GetMapping("/check-email")
    public Map<String, Boolean> checkEmail(@RequestParam String email) {
        boolean available = !userService.existsByEmail(email); // 이메일 사용 가능 여부 확인
        return Collections.singletonMap("available", available); // 사용 가능 여부 반환
    }

    @GetMapping("/user-profile")
    public Optional<User> getUserProfile(Authentication authentication) {
        String username = authentication.getName(); // 인증된 사용자 이름 가져오기
        return userService.findByUsername(username); // 사용자 프로필 반환
    }
}
