/*package org.example.blogproject.post.controller;


import lombok.RequiredArgsConstructor;
import org.example.blogproject.login.domain.User;
import org.example.blogproject.login.service.UserService;
import org.example.blogproject.post.domain.PostDraft;
import org.example.blogproject.post.dto.PostDto;
import org.example.blogproject.post.service.PostDraftService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts/drafts")
public class PostDraftController {

    private final PostDraftService postDraftService;
    private final UserService userService;

    @GetMapping
    public String listDrafts(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user"));
        List<PostDraft> drafts = postDraftService.findByUser(user);
        model.addAttribute("drafts", drafts);
        return "posts/drafts";
    }

    @PostMapping("/save")
    @ResponseBody
    public PostDraft saveDraft(@RequestBody PostDto postDto, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user"));

        PostDraft draft = new PostDraft();
        draft.setUser(user);
        draft.setTitle(postDto.getTitle());
        draft.setContent(postDto.getContent());

        return postDraftService.save(draft);
    }

    @GetMapping("/{id}")
    public String editDraftForm(@PathVariable Long id, Model model) {
        PostDraft draft = postDraftService.findById(id).orElseThrow(() -> new IllegalArgumentException("Draft not found"));
        PostDto postDto = new PostDto(draft.getTitle(), draft.getContent(), null, false, false);
        model.addAttribute("postDto", postDto);
        return "posts/write";
    }

    @PostMapping("/edit/{id}")
    public String updateDraft(@PathVariable Long id, @ModelAttribute PostDto postDto) {
        postDraftService.updatePostDraft(id, postDto);
        return "redirect:/posts/drafts";
    }

    @PostMapping("/delete/{id}")
    public String deleteDraft(@PathVariable Long id) {
        postDraftService.deletePostDraft(id);
        return "redirect:/posts/drafts";
    }

    @PostMapping("/publish/{id}")
    public String publishPost(@PathVariable Long id) {
        postDraftService.publish(id);
        return "redirect:/posts/view?id=" + id;
    }
}
*/