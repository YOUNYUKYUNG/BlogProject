package org.example.blogproject.service;

import org.example.blogproject.domain.Post;
import org.example.blogproject.domain.User;
import org.example.blogproject.dto.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto addComment(CommentDto commentDto, User user, Post post);
    CommentDto editComment(Long id, CommentDto commentDto, User user);
    void deleteComment(Long id, User user);
    List<CommentDto> getCommentsByPost(Post post);
}
