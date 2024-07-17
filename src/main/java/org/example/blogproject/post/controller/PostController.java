package org.example.blogproject.post.controller;

import lombok.RequiredArgsConstructor;
import org.example.blogproject.login.security.CustomUserDetails;
import org.example.blogproject.post.domain.Post;
import org.example.blogproject.post.service.PostService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping("/blog/write")
    public String createBlogForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String username = customUserDetails.getUsername(); // 로그인한 사용자의 ID 가져오기
        model.addAttribute("username", username);

        model.addAttribute("post", new Post());
        return "blog/write";
    }

    @GetMapping("/blog/publish")
    public String showPublishForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String username = customUserDetails.getUsername(); // 로그인한 사용자의 ID 가져오기
        model.addAttribute("username", username);

        model.addAttribute("postTitle", "");
        model.addAttribute("postContent", "");

        return "blog/publish";
    }

    @PostMapping("/blog/save")
    public String saveBlogPost(@ModelAttribute("post") Post post, BindingResult result) {
        if (result.hasErrors()) {
            return "blog/write";
        }

        if (post.isPublished()) {
            post.setPublishedAt(LocalDateTime.now());
        }

        postService.save(post);
        return "redirect:/";
    }

    @PostMapping("/blog/publish")
    @ResponseBody
    public Map<String, Object> publishPost(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

            Post post = new Post();
            post.setTitle(request.get("title"));
            post.setContent(request.get("content"));
            post.setPublished(true);
            post.setPublishedAt(LocalDateTime.now());
            // set other necessary fields

            postService.save(post);

            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }

        return response;
    }

    @GetMapping("/blog/{id}")
    public String viewBlogPost(@PathVariable Long id, Model model) {
        postService.findById(id).ifPresent(post -> model.addAttribute("post", post));
        return "blog/view";
    }

    @GetMapping("/blog/edit/{id}")
    public String editBlogPost(@PathVariable Long id, Model model) {
        postService.findById(id).ifPresent(post -> model.addAttribute("post", post));
        return "blog/edit";
    }

    @PostMapping("/blog/update")
    public String updateBlogPost(@ModelAttribute("post") Post post, BindingResult result) {
        if (result.hasErrors()) {
            return "blog/edit";
        }

        post.setUpdatedAt(LocalDateTime.now());
        if (post.isPublished()) {
            post.setPublishedAt(LocalDateTime.now());
        }

        postService.save(post);
        return "redirect:/";
    }
}
