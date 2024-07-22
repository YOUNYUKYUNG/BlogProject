package org.example.blogproject.repository;

import org.example.blogproject.domain.Comment;
import org.example.blogproject.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post); // 특정 게시물에 대한 모든 댓글을 가져오는 메서드
}
