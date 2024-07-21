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

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostRestController {

    private final PostService postService;
    private final UserService userService;

    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> savePost(@RequestBody PostDto postDto, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user"));
            Post post = postService.convertToEntity(postDto);
            post.setUser(user);
            post.setContent(post.getContent().replaceAll("\\<.*?\\>", ""));
            if (post.getTitle() == null || post.getTitle().isEmpty())
                throw new IllegalArgumentException("Post title cannot be null or empty");
            Post savedPost = postService.savePost(post);
            response.put("postId", savedPost.getPostId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/publish/{id}")
    public ResponseEntity<Map<String, Object>> publishPost(@PathVariable Long id, @RequestBody Map<String, Boolean> payload) {
        Map<String, Object> response = new HashMap<>();
        try {
            Post post = postService.findPostById(id).orElseThrow(() -> new IllegalArgumentException("Post not found"));
            post.setPrivate(payload.get("isPrivate"));
            post.setPublished(payload.get("published"));
            post.setDraft(false);
            postService.savePost(post);
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updatePost(@PathVariable Long id, @RequestBody PostDto postDto, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user"));
            Post post = postService.findPostById(id).orElseThrow(() -> new IllegalArgumentException("Post not found"));
            if (!post.getUser().getId().equals(user.getId()))
                throw new IllegalArgumentException("You are not authorized to update this post");
            post.setTitle(postDto.getTitle());
            post.setContent(postDto.getContent().replaceAll("\\<.*?\\>", ""));
            if (post.getTitle() == null || post.getTitle().isEmpty())
                throw new IllegalArgumentException("Post title cannot be null or empty");
            postService.savePost(post);
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deletePost(@PathVariable Long id, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user"));
            Post post = postService.findPostById(id).orElseThrow(() -> new IllegalArgumentException("Post not found"));
            if (!post.getUser().getId().equals(user.getId()))
                throw new IllegalArgumentException("You are not authorized to delete this post");
            postService.deletePost(id);
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/save-draft")
    public ResponseEntity<Map<String, Object>> saveDraft(@RequestBody PostDto postDto, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user"));
            Post post = postService.convertToEntity(postDto);
            post.setUser(user);
            post.setDraft(true);
            Post savedPost = postService.savePost(post);
            response.put("message", "Draft saved successfully");
            response.put("postId", savedPost.getPostId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/delete-draft/{id}")
    public ResponseEntity<Map<String, Object>> deleteDraft(@PathVariable Long id, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user"));
            Post post = postService.findPostById(id).orElseThrow(() -> new IllegalArgumentException("Post not found"));
            if (!post.getUser().getId().equals(user.getId()))
                throw new IllegalArgumentException("You are not authorized to delete this draft");
            postService.deletePost(id);
            response.put("message", "Draft deleted successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
