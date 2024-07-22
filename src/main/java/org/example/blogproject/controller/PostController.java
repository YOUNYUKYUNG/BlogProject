package org.example.blogproject.controller;

import lombok.RequiredArgsConstructor;
import org.example.blogproject.domain.Post;
import org.example.blogproject.domain.User;
import org.example.blogproject.dto.PostDto;
import org.example.blogproject.dto.CommentDto;
import org.example.blogproject.service.CommentService;
import org.example.blogproject.service.UserService;
import org.example.blogproject.service.PostService;
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
    private final CommentService commentService;

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        List<Post> posts = postService.findAllPublishedPosts(); // 모든 게시된 게시물 찾기
        model.addAttribute("posts", posts); // 모델에 게시물 추가
        addAuthenticatedUserToModel(authentication, model); // 인증된 사용자 정보를 모델에 추가
        return "main/home";
    }

    @GetMapping("/posts/view")
    public String viewPost(Model model, @RequestParam Long id, Authentication authentication) {
        Post post = postService.findPostById(id).orElseThrow(() -> new IllegalArgumentException("Post not found")); // 게시물 찾기
        PostDto postDto = postService.convertToDto(post); // 게시물 DTO로 변환

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName(); // 인증된 사용자 이름 가져오기
            User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user")); // 사용자 찾기

            boolean isAuthor = post.getUser().getId().equals(user.getId()); // 사용자가 게시물 작성자인지 확인
            model.addAttribute("isAuthor", isAuthor); // 모델에 작성자 여부 추가
        } else {
            model.addAttribute("isAuthor", false); // 인증되지 않은 사용자는 작성자가 아님
        }

        List<CommentDto> comments = commentService.getCommentsByPost(post); // 게시물에 대한 댓글 가져오기

        model.addAttribute("postDto", postDto); // 모델에 게시물 DTO 추가
        model.addAttribute("comments", comments); // 모델에 댓글 추가
        addAuthenticatedUserToModel(authentication, model); // 인증된 사용자 정보를 모델에 추가
        return "posts/view";
    }

    @GetMapping("/write")
    public String postForm(Model model, Authentication authentication) {
        String username = authentication.getName(); // 인증된 사용자 이름 가져오기
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user")); // 사용자 찾기
        model.addAttribute("username", user.getUsername()); // 모델에 사용자 이름 추가
        model.addAttribute("postDto", new PostDto()); // 모델에 빈 게시물 DTO 추가
        addAuthenticatedUserToModel(authentication, model); // 인증된 사용자 정보를 모델에 추가
        return "posts/write";
    }

    @GetMapping("/edit/{id}")
    public String showEditPostForm(@PathVariable Long id, Model model, Authentication authentication) {
        Post post = postService.findPostById(id).orElseThrow(() -> new IllegalArgumentException("Post not found")); // 게시물 찾기
        PostDto postDto = postService.convertToDto(post); // 게시물 DTO로 변환

        String username = authentication.getName(); // 인증된 사용자 이름 가져오기
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user")); // 사용자 찾기

        if (!post.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You are not authorized to edit this post"); // 작성자가 아니면 예외 발생
        }

        model.addAttribute("postDto", postDto); // 모델에 게시물 DTO 추가
        addAuthenticatedUserToModel(authentication, model); // 인증된 사용자 정보를 모델에 추가
        return "posts/edit";
    }

    @PostMapping("/edit/{id}")
    public String updatePost(@PathVariable Long id, @ModelAttribute PostDto postDto, Authentication authentication) {
        String username = authentication.getName(); // 인증된 사용자 이름 가져오기
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user")); // 사용자 찾기

        Post post = postService.convertToEntity(postDto); // DTO를 엔티티로 변환
        post.setUser(user); // 게시물 작성자 설정

        if (postDto.isPublished()) {
            post.setDraft(false); // 게시물이 게시되었으면 드래프트 상태 해제
        }

        // 제목이 설정되지 않은 경우 예외 처리
        if (post.getTitle() == null || post.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Post title cannot be null or empty"); // 제목이 없으면 예외 발생
        }

        postService.updatePost(id, post); // 게시물 업데이트
        return "redirect:/posts/view?id=" + id;
    }

    @GetMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName(); // 인증된 사용자 이름 가져오기
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user")); // 사용자 찾기

        Post post = postService.findPostById(id).orElseThrow(() -> new IllegalArgumentException("Post not found")); // 게시물 찾기

        if (!post.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You are not authorized to delete this post"); // 작성자가 아니면 예외 발생
        }

        postService.deletePost(id); // 게시물 삭제
        return "redirect:/";
    }

    @GetMapping("/posts/preview")
    public String previewPost(@RequestParam Long postId, Model model) {
        Post post = postService.findPostById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found")); // 게시물 찾기
        PostDto postDto = postService.convertToDto(post); // 게시물 DTO로 변환
        model.addAttribute("postDto", postDto); // 모델에 게시물 DTO 추가
        return "posts/preview";
    }

    @GetMapping("/posts")
    public String myPosts(Model model, Authentication authentication) {
        String username = authentication.getName(); // 인증된 사용자 이름 가져오기
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user")); // 사용자 찾기
        List<Post> posts = postService.findPostsByUser(user); // 사용자의 게시물 찾기
        model.addAttribute("user", user); // 모델에 사용자 추가
        model.addAttribute("posts", posts); // 모델에 게시물 목록 추가
        addAuthenticatedUserToModel(authentication, model); // 인증된 사용자 정보를 모델에 추가
        return "users/posts";
    }

    @GetMapping("/drafts")
    public String getDrafts(Model model, Authentication authentication) {
        String username = authentication.getName(); // 인증된 사용자 이름 가져오기
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user")); // 사용자 찾기
        List<Post> drafts = postService.findDraftsByUser(user); // 사용자의 드래프트 찾기
        model.addAttribute("user", user); // 모델에 사용자 추가
        model.addAttribute("drafts", drafts); // 모델에 드래프트 목록 추가
        addAuthenticatedUserToModel(authentication, model); // 인증된 사용자 정보를 모델에 추가
        return "posts/drafts";
    }

    private void addAuthenticatedUserToModel(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName(); // 인증된 사용자 이름 가져오기
            User user = userService.findByUsername(username).orElse(null); // 사용자 찾기
            model.addAttribute("user", user); // 모델에 사용자 추가
        }
    }

    @GetMapping("/recent")
    public String showRecentPosts(Model model, Authentication authentication) {
        List<PostDto> recentPosts = postService.findRecentPosts(); // 최근 게시물 찾기
        model.addAttribute("posts", recentPosts); // 모델에 게시물 목록 추가
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName(); // 인증된 사용자 이름 가져오기
            User user = userService.findByUsername(username).orElse(null); // 사용자 찾기
            model.addAttribute("user", user); // 모델에 사용자 추가
        }
        return "main/recent";
    }

}
