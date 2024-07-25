package org.example.blogproject.controller;

import lombok.RequiredArgsConstructor;
import org.example.blogproject.domain.User;
import org.example.blogproject.service.UserService;
import org.example.blogproject.domain.Post;
import org.example.blogproject.dto.PostDto;
import org.example.blogproject.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // 이 부분 추가

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostRestController {

    private final PostService postService; // 게시물 서비스
    private final UserService userService; // 사용자 서비스

    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> savePost(@RequestBody PostDto postDto, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            String username = authentication.getName(); // 인증된 사용자 이름 가져오기
            User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user")); // 사용자 찾기
            Post post = postService.convertToEntity(postDto); // DTO를 엔티티로 변환
            post.setUser(user); // 게시물 작성자 설정
            post.setContent(post.getContent().replaceAll("\\<.*?\\>", "")); // 내용에서 HTML 태그 제거
            if (post.getTitle() == null || post.getTitle().isEmpty())
                throw new IllegalArgumentException("Post title cannot be null or empty"); // 제목이 없으면 예외 발생
            Post savedPost = postService.savePost(post); // 게시물 저장
            response.put("postId", savedPost.getPostId()); // 응답에 게시물 ID 추가
            return ResponseEntity.ok(response); // 성공 응답 반환
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage()); // 예외 메시지를 응답에 추가
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // 오류 응답 반환
        }
    }

    @PostMapping("/publish/{id}")
    public ResponseEntity<Map<String, Object>> publishPost(@PathVariable Long id, @RequestBody Map<String, Boolean> payload) {
        Map<String, Object> response = new HashMap<>();
        try {
            Post post = postService.findPostById(id).orElseThrow(() -> new IllegalArgumentException("Post not found")); // 게시물 찾기
            post.setPrivate(payload.get("isPrivate")); // 게시물 공개 여부 설정
            post.setPublished(payload.get("published")); // 게시물 게시 여부 설정
            post.setDraft(false); // 게시물 드래프트 상태 해제
            postService.savePost(post); // 게시물 저장
            response.put("success", true); // 응답에 성공 메시지 추가
            return ResponseEntity.ok(response); // 성공 응답 반환
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage()); // 예외 메시지를 응답에 추가
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // 오류 응답 반환
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updatePost(@PathVariable Long id, @RequestBody PostDto postDto, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            String username = authentication.getName(); // 인증된 사용자 이름 가져오기
            User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user")); // 사용자 찾기
            Post post = postService.findPostById(id).orElseThrow(() -> new IllegalArgumentException("Post not found")); // 게시물 찾기
            if (!post.getUser().getId().equals(user.getId()))
                throw new IllegalArgumentException("You are not authorized to update this post"); // 작성자가 아니면 예외 발생
            post.setTitle(postDto.getTitle()); // 게시물 제목 설정
            post.setContent(postDto.getContent().replaceAll("\\<.*?\\>", "")); // 내용에서 HTML 태그 제거
            if (post.getTitle() == null || post.getTitle().isEmpty())
                throw new IllegalArgumentException("Post title cannot be null or empty"); // 제목이 없으면 예외 발생
            postService.savePost(post); // 게시물 저장
            response.put("success", true); // 응답에 성공 메시지 추가
            return ResponseEntity.ok(response); // 성공 응답 반환
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage()); // 예외 메시지를 응답에 추가
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // 오류 응답 반환
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deletePost(@PathVariable Long id, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            String username = authentication.getName(); // 인증된 사용자 이름 가져오기
            User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user")); // 사용자 찾기
            Post post = postService.findPostById(id).orElseThrow(() -> new IllegalArgumentException("Post not found")); // 게시물 찾기
            if (!post.getUser().getId().equals(user.getId()))
                throw new IllegalArgumentException("You are not authorized to delete this post"); // 작성자가 아니면 예외 발생
            postService.deletePost(id); // 게시물 삭제
            response.put("success", true); // 응답에 성공 메시지 추가
            return ResponseEntity.ok(response); // 성공 응답 반환
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage()); // 예외 메시지를 응답에 추가
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // 오류 응답 반환
        }
    }

    @PostMapping("/save-draft")
    public ResponseEntity<Map<String, Object>> saveDraft(@RequestBody PostDto postDto, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            String username = authentication.getName(); // 인증된 사용자 이름 가져오기
            User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user")); // 사용자 찾기
            Post post = postService.convertToEntity(postDto); // DTO를 엔티티로 변환
            post.setUser(user); // 게시물 작성자 설정
            post.setDraft(true); // 게시물 드래프트 상태 설정
            Post savedPost = postService.savePost(post); // 드래프트 게시물 저장
            response.put("message", "Draft saved successfully"); // 응답에 성공 메시지 추가
            response.put("postId", savedPost.getPostId()); // 응답에 게시물 ID 추가
            return ResponseEntity.ok(response); // 성공 응답 반환
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage()); // 예외 메시지를 응답에 추가
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // 오류 응답 반환
        }
    }

    @DeleteMapping("/delete-draft/{id}")
    public ResponseEntity<Map<String, Object>> deleteDraft(@PathVariable Long id, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            String username = authentication.getName(); // 인증된 사용자 이름 가져오기
            User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user")); // 사용자 찾기
            Post post = postService.findPostById(id).orElseThrow(() -> new IllegalArgumentException("Post not found")); // 게시물 찾기
            if (!post.getUser().getId().equals(user.getId()))
                throw new IllegalArgumentException("You are not authorized to delete this draft"); // 작성자가 아니면 예외 발생
            postService.deletePost(id); // 드래프트 게시물 삭제
            response.put("message", "Draft deleted successfully"); // 응답에 성공 메시지 추가
            return ResponseEntity.ok(response); // 성공 응답 반환
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage()); // 예외 메시지를 응답에 추가
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // 오류 응답 반환
        }
    }

    @PostMapping("/upload-thumbnail/{postId}")
    public ResponseEntity<Map<String, String>> uploadThumbnail(@PathVariable Long postId, @RequestParam("thumbnail") MultipartFile file) {
        try {
            // 디렉토리가 존재하는지 확인하고 없으면 생성
            Path thumbnailDirPath = Paths.get("src/main/resources/static/image/thumbnails/");
            if (!Files.exists(thumbnailDirPath)) {
                Files.createDirectories(thumbnailDirPath);
            }

            String imageUrl;
            if (file.isEmpty()) {
                // 파일이 업로드되지 않은 경우 기본 이미지 사용
                imageUrl = "/image/thumbnails/none.jpg";
            } else {
                // 파일 저장
                byte[] bytes = file.getBytes();
                Path path = thumbnailDirPath.resolve(file.getOriginalFilename());
                Files.write(path, bytes);

                // 파일 접근 URL 생성
                imageUrl = "/image/thumbnails/" + file.getOriginalFilename();

                // 로그 추가: 이미지 저장 경로 및 URL 확인
                System.out.println("Image saved to: " + path.toAbsolutePath());
                System.out.println("Image URL: " + imageUrl);
            }

            // Post 엔티티에 썸네일 URL 설정
            Post post = postService.findPostById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));
            post.setPreviewImageUrl(imageUrl);
            postService.savePost(post);

            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to upload file"));
        }
    }





}
