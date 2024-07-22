package org.example.blogproject.controller;

import lombok.RequiredArgsConstructor;
import org.example.blogproject.domain.Comment;
import org.example.blogproject.domain.Post;
import org.example.blogproject.domain.User;
import org.example.blogproject.dto.CommentDto;
import org.example.blogproject.service.CommentService;
import org.example.blogproject.service.PostService;
import org.example.blogproject.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentRestController {
    private final CommentService commentService;
    private final UserService userService;
    private final PostService postService;

    @PostMapping("/add")
    public ResponseEntity<CommentDto> addComment(@RequestBody CommentDto commentDto, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user"));
        Post post = postService.findPostById(commentDto.getPost().getPostId()).orElseThrow(() -> new IllegalArgumentException("Post not found"));

        CommentDto savedComment = commentService.addComment(commentDto, user, post);
        return ResponseEntity.ok(savedComment); // 저장된 댓글을 응답으로 반환
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDto>> getCommentsByPost(@PathVariable Long postId) {
        Post post = postService.findPostById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));
        List<CommentDto> comments = commentService.getCommentsByPost(post);
        return ResponseEntity.ok(comments); // 조회된 댓글 목록을 응답으로 반환
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<CommentDto> editComment(@PathVariable Long id, @RequestBody CommentDto commentDto, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user"));
        CommentDto updatedComment = commentService.editComment(id, commentDto, user);
        return ResponseEntity.ok(updatedComment); // 수정된 댓글을 응답으로 반환
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user"));
        commentService.deleteComment(id, user);
        return ResponseEntity.noContent().build(); // 응답 본문 없이 상태 코드 204 반환
    }
}