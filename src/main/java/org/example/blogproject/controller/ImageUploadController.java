package org.example.blogproject.controller;

import org.example.blogproject.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/image")
public class ImageUploadController {

    private static final String PROFILE_DIR = "src/main/resources/static/image/profile/";
    private static final String THUMBNAIL_DIR = "src/main/resources/static/image/thumbnails/";
    private final UserService userService;

    public ImageUploadController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/upload-profile-image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("profileImage") MultipartFile file, Principal principal) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Please select a file to upload"));
        }

        try {
            // 디렉토리가 존재하는지 확인하고 없으면 생성
            Path profileDirPath = Paths.get(PROFILE_DIR);
            if (!Files.exists(profileDirPath)) {
                Files.createDirectories(profileDirPath);
            }

            // 파일 저장
            byte[] bytes = file.getBytes();
            Path path = profileDirPath.resolve(file.getOriginalFilename());
            Files.write(path, bytes);

            // 파일 접근 URL 생성
            String imageUrl = "/image/profile/" + file.getOriginalFilename();

            // 사용자 프로필 이미지 URL 업데이트
            userService.updateUserProfileImage(principal.getName(), imageUrl);

            return ResponseEntity.ok(Map.of("url", imageUrl));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to upload file"));
        }
    }

    @PostMapping("/upload-thumbnail")
    public ResponseEntity<Map<String, String>> uploadThumbnail(@RequestParam("thumbnail") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Please select a file to upload"));
        }

        try {
            // 디렉토리가 존재하는지 확인하고 없으면 생성
            Path thumbnailDirPath = Paths.get(THUMBNAIL_DIR);
            if (!Files.exists(thumbnailDirPath)) {
                Files.createDirectories(thumbnailDirPath);
            }

            // 파일 저장
            byte[] bytes = file.getBytes();
            Path path = thumbnailDirPath.resolve(file.getOriginalFilename());
            Files.write(path, bytes);

            // 파일 접근 URL 생성
            String imageUrl = "/image/thumbnails/" + file.getOriginalFilename();

            // 로그 추가: 이미지 저장 경로 및 URL 확인
            System.out.println("Image saved to: " + path.toAbsolutePath());
            System.out.println("Image URL: " + imageUrl);

            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to upload file"));
        }
    }
}
