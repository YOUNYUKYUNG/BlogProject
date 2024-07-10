package org.example.blogproject.controller;

import lombok.RequiredArgsConstructor;
import org.example.blogproject.domain.Post;
import org.example.blogproject.domain.PostDraft;
import org.example.blogproject.domain.User;
import org.example.blogproject.service.PostDraftService;
import org.example.blogproject.service.PostService;
import org.example.blogproject.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final PostDraftService postDraftService;
    private final UserService userService;

    @GetMapping("/blog/new")
    public String createBlogForm(Model model) {
        model.addAttribute("post", new Post());
        return "write";
    }

    @PostMapping("/blog/save")
    public String saveBlogPost(@ModelAttribute("post") Post post, BindingResult result) {
        if (result.hasErrors()) {
            return "write";
        }

        if (post.isPublished()) {
            post.setPublishedAt(LocalDateTime.now());
        }

        postService.save(post);
        return "redirect:/";
    }

    @PostMapping("/blog/draft")
    public String saveDraft(@ModelAttribute("post") Post post, BindingResult result) {
        if (result.hasErrors()) {
            return "write";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        PostDraft draft = new PostDraft();
        draft.setUser(user);
        draft.setTitle(post.getTitle());
        draft.setContent(post.getContent());
        draft.setPreviewImageUrl(post.getPreviewImageUrl());
        draft.setCreatedAt(LocalDateTime.now());

        postDraftService.save(draft);
        return "redirect:/";
    }

    @GetMapping("/blog/drafts")
    public String viewDrafts(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        List<PostDraft> drafts = postDraftService.findByUser(user);
        model.addAttribute("drafts", drafts);
        return "blog/drafts";
    }

    @GetMapping("/blog/draft/{id}")
    public String editDraft(@PathVariable Long id, Model model) {
        PostDraft draft = postDraftService.findById(id);
        model.addAttribute("post", draft);
        return "blog/edit";
    }

    @PostMapping("/blog/draft/update")
    public String updateDraft(@ModelAttribute("post") PostDraft draft, BindingResult result) {
        if (result.hasErrors()) {
            return "blog/edit";
        }

        draft.setUpdatedAt(LocalDateTime.now());
        postDraftService.save(draft);
        return "redirect:/";
    }

    @PostMapping("/blog/draft/publish")
    public String publishDraft(@ModelAttribute("post") PostDraft draft, BindingResult result) {
        if (result.hasErrors()) {
            return "blog/edit";
        }

        Post post = new Post();
        post.setUser(draft.getUser());
        post.setTitle(draft.getTitle());
        post.setContent(draft.getContent());
        post.setPreviewImageUrl(draft.getPreviewImageUrl());
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        postService.save(post);
        postDraftService.deleteById(draft.getDraftId());
        return "redirect:/";
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
