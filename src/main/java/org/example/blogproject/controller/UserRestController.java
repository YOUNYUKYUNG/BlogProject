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
    private static final String UPLOAD_DIR = "src/main/resources/static/image/profile/"; // 프로필 이미지 업로드 디렉토리

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

    @PostMapping("/upload-profile-image") // 프로필 이미지 업로드 URL에 대한 POST 요청을 처리
    public Map<String, String> uploadProfileImage(@RequestParam("profileImage") MultipartFile file, Authentication authentication) {
        if (file.isEmpty()) {
            return Collections.singletonMap("message", "Please select a file to upload"); // 파일이 없으면 메시지 반환
        }

        try {
            byte[] bytes = file.getBytes(); // 파일 바이트 배열로 읽기
            Path path = Paths.get(UPLOAD_DIR + file.getOriginalFilename()); // 파일 경로 설정
            Files.write(path, bytes); // 파일 쓰기

            String username = authentication.getName(); // 인증된 사용자 이름 가져오기
            userService.updateUserProfileImage(username, "/image/profile/" + file.getOriginalFilename()); // 사용자 프로필 이미지 업데이트

            return Collections.singletonMap("message", "You successfully uploaded '" + file.getOriginalFilename() + "'"); // 성공 메시지 반환
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.singletonMap("message", "Failed to upload file");
        }
    }

    @PostMapping("/delete-profile-image")
    public Map<String, String> deleteProfileImage(Authentication authentication) {
        String username = authentication.getName(); // 인증된 사용자 이름 가져오기
        userService.updateUserProfileImage(username, null); // 사용자 프로필 이미지 삭제
        return Collections.singletonMap("message", "Profile image deleted successfully"); // 성공 메시지 반환
    }

    @GetMapping("/user-profile")
    public Optional<User> getUserProfile(Authentication authentication) {
        String username = authentication.getName(); // 인증된 사용자 이름 가져오기
        return userService.findByUsername(username); // 사용자 프로필 반환
    }
}
