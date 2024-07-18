package org.example.blogproject.post.controller;

import lombok.RequiredArgsConstructor;
import org.example.blogproject.login.domain.User;
import org.example.blogproject.login.service.UserService;
import org.example.blogproject.post.domain.Post;
import org.example.blogproject.post.dto.PostDto;
import org.example.blogproject.post.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostRestController {

    private final PostService postService;
    private final UserService userService;

    @PostMapping("/save")
    public ResponseEntity<Map<String, Long>> savePost(@RequestBody PostDto postDto, Authentication authentication) {
        String username = authentication.getName(); // 인증된 사용자 이름 가져오기
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user")); // 사용자 찾기
        Post post = postService.convertToEntity(postDto); // DTO를 엔티티로 변환
        post.setUser(user); // 작성자 설정
        post.setContent(post.getContent().replaceAll("\\<.*?\\>", "")); // HTML 태그 제거
        if (post.getTitle() == null || post.getTitle().isEmpty()) throw new IllegalArgumentException("Post title cannot be null or empty"); // 제목 검증
        Post savedPost = postService.savePost(post); // 포스트 저장
        Map<String, Long> response = new HashMap<>();
        response.put("postId", savedPost.getPostId()); // 응답에 포스트 ID 추가
        return ResponseEntity.ok(response); // 응답 반환
    }

    @PostMapping("/publish/{id}")
    public ResponseEntity<Map<String, Object>> publishPost(@PathVariable Long id, @RequestBody Map<String, Boolean> payload) {
        Post post = postService.findPostById(id).orElseThrow(() -> new IllegalArgumentException("Post not found")); // 포스트 찾기
        post.setPrivate(payload.get("isPrivate")); // 비공개 설정
        post.setPublished(payload.get("published")); // 출간 설정
        post.setDraft(false); // 임시 저장 해제
        postService.savePost(post); // 포스트 저장
        Map<String, Object> response = new HashMap<>();
        response.put("success", true); // 성공 응답
        return ResponseEntity.ok(response); // 응답 반환
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updatePost(@PathVariable Long id, @RequestBody PostDto postDto, Authentication authentication) {
        String username = authentication.getName(); // 인증된 사용자 이름 가져오기
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user")); // 사용자 찾기
        Post post = postService.findPostById(id).orElseThrow(() -> new IllegalArgumentException("Post not found")); // 포스트 찾기
        if (!post.getUser().getId().equals(user.getId())) throw new IllegalArgumentException("You are not authorized to update this post"); // 작성자 확인
        post.setTitle(postDto.getTitle()); // 제목 설정
        post.setContent(postDto.getContent().replaceAll("\\<.*?\\>", "")); // HTML 태그 제거
        if (post.getTitle() == null || post.getTitle().isEmpty()) throw new IllegalArgumentException("Post title cannot be null or empty"); // 제목 검증
        postService.savePost(post); // 포스트 저장
        Map<String, Object> response = new HashMap<>();
        response.put("success", true); // 성공 응답
        return ResponseEntity.ok(response); // 응답 반환
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deletePost(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName(); // 인증된 사용자 이름 가져오기
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user")); // 사용자 찾기
        Post post = postService.findPostById(id).orElseThrow(() -> new IllegalArgumentException("Post not found")); // 포스트 찾기
        if (!post.getUser().getId().equals(user.getId())) throw new IllegalArgumentException("You are not authorized to delete this post"); // 작성자 확인
        postService.deletePost(id); // 포스트 삭제
        Map<String, Object> response = new HashMap<>();
        response.put("success", true); // 성공 응답
        return ResponseEntity.ok(response); // 응답 반환
    }

    @PostMapping("/save-draft")
    public ResponseEntity<Map<String, Object>> saveDraft(@RequestBody PostDto postDto, Authentication authentication) {
        String username = authentication.getName(); // 인증된 사용자 이름 가져오기
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user")); // 사용자 찾기
        Post post = postService.convertToEntity(postDto); // DTO를 엔티티로 변환
        post.setUser(user); // 작성자 설정
        post.setDraft(true); // 임시 저장 설정
        Post savedPost = postService.savePost(post); // 포스트 저장
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Draft saved successfully"); // 메시지 설정
        response.put("postId", savedPost.getPostId()); // 포스트 ID 설정
        return ResponseEntity.ok(response); // 응답 반환
    }

    @DeleteMapping("/delete-draft/{id}")
    public ResponseEntity<String> deleteDraft(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName(); // 인증된 사용자 이름 가져오기
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user")); // 사용자 찾기
        Post post = postService.findPostById(id).orElseThrow(() -> new IllegalArgumentException("Post not found")); // 포스트 찾기
        if (!post.getUser().getId().equals(user.getId())) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete this draft"); // 작성자 확인
        postService.deletePost(id); // 포스트 삭제
        return ResponseEntity.ok("Draft deleted successfully"); // 성공 응답 반환
    }
}
