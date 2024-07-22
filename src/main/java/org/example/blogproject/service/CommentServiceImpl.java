package org.example.blogproject.service;

import lombok.RequiredArgsConstructor;
import org.example.blogproject.domain.Comment;
import org.example.blogproject.domain.Post;
import org.example.blogproject.domain.User;
import org.example.blogproject.dto.CommentDto;
import org.example.blogproject.dto.PostDto;
import org.example.blogproject.dto.UserDto;
import org.example.blogproject.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    @Override
    public CommentDto addComment(CommentDto commentDto, User user, Post post) {
        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());
        comment.setUser(user);
        comment.setPost(post);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment); // 댓글 저장

        // 저장된 댓글을 DTO로 변환하여 반환
        return new CommentDto(
                savedComment.getCommentId(),
                savedComment.getContent(),
                new UserDto(savedComment.getUser()),
                new PostDto(savedComment.getPost()),
                savedComment.getCreatedAt(),
                savedComment.getUpdatedAt()
        );
    }

    @Override
    public CommentDto editComment(Long id, CommentDto commentDto, User user) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found")); // 댓글 찾기
        if (!comment.getUser().equals(user)) {
            throw new IllegalArgumentException("You can only edit your own comments"); // 사용자가 댓글 작성자가 아닌 경우 예외 발생
        }
        comment.setContent(commentDto.getContent());
        comment.setUpdatedAt(LocalDateTime.now()); // 댓글 수정 시간 설정

        Comment updatedComment = commentRepository.save(comment); // 댓글 수정 및 저장

        // 수정된 댓글을 DTO로 변환하여 반환
        return new CommentDto(
                updatedComment.getCommentId(),
                updatedComment.getContent(),
                new UserDto(updatedComment.getUser()),
                new PostDto(updatedComment.getPost()),
                updatedComment.getCreatedAt(),
                updatedComment.getUpdatedAt()
        );
    }

    @Override
    public void deleteComment(Long id, User user) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found")); // 댓글 찾기
        if (!comment.getUser().equals(user)) {
            throw new IllegalArgumentException("You can only delete your own comments"); // 사용자가 댓글 작성자가 아닌 경우 예외 발생
        }
        commentRepository.delete(comment); // 댓글 삭제
    }

    @Override
    public List<CommentDto> getCommentsByPost(Post post) {
        List<Comment> comments = commentRepository.findByPost(post); // 게시물에 대한 모든 댓글 조회
        // 댓글 목록을 DTO 목록으로 변환하여 반환
        return comments.stream().map(comment -> new CommentDto(
                comment.getCommentId(),
                comment.getContent(),
                new UserDto(comment.getUser()),
                new PostDto(comment.getPost()),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        )).collect(Collectors.toList());
    }
}