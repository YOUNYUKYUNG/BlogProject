package org.example.blogproject.service;

import org.example.blogproject.domain.Post;
import org.example.blogproject.domain.User;
import org.example.blogproject.dto.CommentDto;

import java.util.List;
//댓글 저장
public interface CommentService {
    // 댓글 추가 메서드
    CommentDto addComment(CommentDto commentDto, User user, Post post);

    // 댓글 수정 메서드
    CommentDto editComment(Long id, CommentDto commentDto, User user);

    // 댓글 삭제 메서드
    void deleteComment(Long id, User user);

    // 특정 게시물에 대한 댓글 조회 메서드
    List<CommentDto> getCommentsByPost(Post post);
}