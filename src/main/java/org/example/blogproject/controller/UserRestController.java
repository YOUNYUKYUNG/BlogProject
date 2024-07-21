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
    private static final String UPLOAD_DIR = "src/main/resources/static/image/profile/";

    @GetMapping("/check-username")
    public Map<String, Boolean> checkUsername(@RequestParam String username) {
        boolean available = !userService.existsByUsername(username);
        return Collections.singletonMap("available", available);
    }

    @GetMapping("/check-email")
    public Map<String, Boolean> checkEmail(@RequestParam String email) {
        boolean available = !userService.existsByEmail(email);
        return Collections.singletonMap("available", available);
    }

    @PostMapping("/upload-profile-image")
    public Map<String, String> uploadProfileImage(@RequestParam("profileImage") MultipartFile file, Authentication authentication) {
        if (file.isEmpty()) {
            return Collections.singletonMap("message", "Please select a file to upload");
        }

        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOAD_DIR + file.getOriginalFilename());
            Files.write(path, bytes);

            String username = authentication.getName();
            userService.updateUserProfileImage(username, "/image/profile/" + file.getOriginalFilename());

            return Collections.singletonMap("message", "You successfully uploaded '" + file.getOriginalFilename() + "'");
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.singletonMap("message", "Failed to upload file");
        }
    }

    @PostMapping("/delete-profile-image")
    public Map<String, String> deleteProfileImage(Authentication authentication) {
        String username = authentication.getName();
        userService.updateUserProfileImage(username, null);
        return Collections.singletonMap("message", "Profile image deleted successfully");
    }

    @GetMapping("/user-profile")
    public Optional<User> getUserProfile(Authentication authentication) {
        String username = authentication.getName();
        return userService.findByUsername(username);
    }
}
