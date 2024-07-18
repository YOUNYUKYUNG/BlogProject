package org.example.blogproject.post.controller;

import lombok.RequiredArgsConstructor;
import org.example.blogproject.login.domain.User;
import org.example.blogproject.login.service.UserService;
import org.example.blogproject.post.domain.Post;
import org.example.blogproject.post.dto.PostDto;
import org.example.blogproject.post.service.PostService;
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
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user"));

        Post post = postService.convertToEntity(postDto);
        post.setUser(user);
        String contents = post.getContent().replaceAll("\\<.*?\\>", ""); // HTML 태그 제거
        post.setContent(contents);

        // 제목이 설정되지 않은 경우 예외 처리
        if (post.getTitle() == null || post.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Post title cannot be null or empty");
        }

        Post savedPost = postService.savePost(post);

        Map<String, Long> response = new HashMap<>();
        response.put("postId", savedPost.getPostId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/publish/{id}")
    public ResponseEntity<Map<String, Object>> publishPost(@PathVariable Long id, @RequestBody Map<String, Boolean> payload) {
        Post post = postService.findPostById(id).orElseThrow(() -> new IllegalArgumentException("Post not found"));
        boolean isPrivate = payload.get("isPrivate");
        boolean published = payload.get("published");

        post.setPrivate(isPrivate);
        post.setPublished(published);
        postService.savePost(post);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updatePost(@PathVariable Long id, @RequestBody PostDto postDto, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user"));

        Post post = postService.findPostById(id).orElseThrow(() -> new IllegalArgumentException("Post not found"));

        post.setTitle(postDto.getTitle());
        String contents = postDto.getContent().replaceAll("\\<.*?\\>", ""); // HTML 태그 제거
        post.setContent(contents);
        post.setUser(user);

        // 제목이 설정되지 않은 경우 예외 처리
        if (post.getTitle() == null || post.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Post title cannot be null or empty");
        }

        postService.savePost(post);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        return ResponseEntity.ok(response);
    }
}
