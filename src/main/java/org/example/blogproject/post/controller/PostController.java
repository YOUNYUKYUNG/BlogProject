package org.example.blogproject.post.controller;

import lombok.RequiredArgsConstructor;
import org.example.blogproject.login.domain.User;
import org.example.blogproject.login.service.UserService;
import org.example.blogproject.post.domain.Post;
import org.example.blogproject.post.dto.PostDto;
import org.example.blogproject.post.service.PostService;
import org.example.blogproject.post.tag.Tag;
import org.example.blogproject.post.tag.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final TagService tagService;

    @GetMapping("/")
    public String home(Model model) {
        List<Post> posts = postService.findAllPosts();
        model.addAttribute("posts", posts);
        return "home";
    }

    @GetMapping("/posts/view")
    public String viewPost(Model model, @RequestParam Long id) {
        Post post = postService.findPostById(id).orElseThrow(() -> new IllegalArgumentException("Post not found"));
        model.addAttribute("post", post);
        return "posts/view";  // 포스트 보기 페이지를 보여주는 템플릿 이름
    }

    @GetMapping("/write")
    public String postForm(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user"));
        model.addAttribute("username", user.getUsername());
        model.addAttribute("postDto", new PostDto());
        return "posts/write";
    }

    @PostMapping("/posts/save")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> savePost(@RequestBody PostDto postDto, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user"));

        Post post = new Post();
        post.setUser(user);
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setPreviewImageUrl(postDto.getPreviewImageUrl());
        post.setPublished(postDto.isPublished());
        post.setPrivate(postDto.isPrivate());

        Set<Tag> tags = postDto.getTags().stream().map(tagName -> {
            Tag tag = tagService.findOrCreateTag(tagName);
            return tag;
        }).collect(Collectors.toSet());
        post.setTags(tags);

        Post savedPost = postService.savePost(post);

        Map<String, Long> response = new HashMap<>();
        response.put("postId", savedPost.getPostId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/post/preview")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> previewPost(@RequestBody PostDto postDto, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setPreviewImageUrl(postDto.getPreviewImageUrl()); // 썸네일 이미지 URL 설정
        post.setUser(user);  // 현재 로그인한 사용자 설정

        Post savedPost = postService.savePost(post);

        Map<String, Long> response = new HashMap<>();
        response.put("postId", savedPost.getPostId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/post/preview")
    public String getPostPreview(@RequestParam("postId") Long postId, Model model) {
        Post post = postService.findPostById(postId).orElseThrow(() -> new IllegalArgumentException("포스트를 찾을 수 없습니다."));
        model.addAttribute("post", post);
        return "posts/preview";
    }

}
