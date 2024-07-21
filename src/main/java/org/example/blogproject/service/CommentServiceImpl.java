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

        Comment savedComment = commentRepository.save(comment);

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
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        if (!comment.getUser().equals(user)) {
            throw new IllegalArgumentException("You can only edit your own comments");
        }
        comment.setContent(commentDto.getContent());
        comment.setUpdatedAt(LocalDateTime.now());

        Comment updatedComment = commentRepository.save(comment);

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
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        if (!comment.getUser().equals(user)) {
            throw new IllegalArgumentException("You can only delete your own comments");
        }
        commentRepository.delete(comment);
    }

    @Override
    public List<CommentDto> getCommentsByPost(Post post) {
        List<Comment> comments = commentRepository.findByPost(post);
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
