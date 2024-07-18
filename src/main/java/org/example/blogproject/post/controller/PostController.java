package org.example.blogproject.post.controller;

import lombok.RequiredArgsConstructor;
import org.example.blogproject.login.domain.User;
import org.example.blogproject.login.service.UserService;
import org.example.blogproject.post.domain.Post;
import org.example.blogproject.post.dto.PostDto;
import org.example.blogproject.post.service.PostService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserService userService;

    @GetMapping("/")
    public String home(Model model) {
        List<Post> posts = postService.findAllPosts();
        model.addAttribute("posts", posts);
        return "home";
    }

    @GetMapping("/posts/view")
    public String viewPost(Model model, @RequestParam Long id, Authentication authentication) {
        Post post = postService.findPostById(id).orElseThrow(() -> new IllegalArgumentException("Post not found"));
        PostDto postDto = postService.convertToDto(post);

        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user"));

        boolean isAuthor = post.getUser().getId().equals(user.getId());

        model.addAttribute("postDto", postDto);
        model.addAttribute("isAuthor", isAuthor);
        return "posts/view";
    }

    @GetMapping("/write")
    public String postForm(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user"));
        model.addAttribute("username", user.getUsername());
        model.addAttribute("postDto", new PostDto());
        return "posts/write";
    }

    @GetMapping("/edit/{id}")
    public String showEditPostForm(@PathVariable Long id, Model model, Authentication authentication) {
        Post post = postService.findPostById(id).orElseThrow(() -> new IllegalArgumentException("Post not found"));
        PostDto postDto = postService.convertToDto(post);

        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user"));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You are not authorized to edit this post");
        }

        model.addAttribute("postDto", postDto);
        return "posts/edit";
    }

    @PostMapping("/edit/{id}")
    public String updatePost(@PathVariable Long id, @ModelAttribute PostDto postDto, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user"));

        Post post = postService.convertToEntity(postDto);
        post.setUser(user);
        String content = post.getContent() != null ? post.getContent() : "";
        content = content.replaceAll("pattern", "replacement");
        post.setContent(content);

        // 제목이 설정되지 않은 경우 예외 처리
        if (post.getTitle() == null || post.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Post title cannot be null or empty");
        }

        postService.updatePost(id, post);
        return "redirect:/posts/view?id=" + id;
    }

    @GetMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user"));

        Post post = postService.findPostById(id).orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You are not authorized to delete this post");
        }

        postService.deletePost(id);
        return "redirect:/";
    }

    @GetMapping("/posts/preview")
    public String previewPost(@RequestParam Long postId, Model model) {
        Post post = postService.findPostById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));
        PostDto postDto = postService.convertToDto(post);
        model.addAttribute("postDto", postDto);
        return "posts/preview";
    }

    @GetMapping("/posts")
    public String myPosts(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user"));
        List<Post> posts = postService.findPostsByUser(user);
        model.addAttribute("posts", posts);
        return "users/posts";
    }

}
