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

    // 기존 매핑 메서드들...

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
        return "view";  // 포스트 보기 페이지를 보여주는 템플릿 이름
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

        // 태그 처리
        Set<Tag> tags = postDto.getTags().stream().map(tagName -> {
            Tag tag = tagService.findOrCreateTag(tagName); // 태그를 찾거나 새로 생성하는 서비스 메서드
            return tag;
        }).collect(Collectors.toSet());
        post.setTags(tags);

        Post savedPost = postService.savePost(post);

        Map<String, Long> response = new HashMap<>();
        response.put("postId", savedPost.getPostId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/posts/{postId}")
    public String viewPost(@PathVariable Long postId, Model model) {
        Post post = postService.findPostById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));
        model.addAttribute("post", post);
        return "posts/view";
    }
}
