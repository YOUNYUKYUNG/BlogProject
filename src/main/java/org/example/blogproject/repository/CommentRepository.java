package org.example.blogproject.repository;

import org.example.blogproject.domain.Comment;
import org.example.blogproject.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);
}
