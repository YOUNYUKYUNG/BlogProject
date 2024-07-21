package org.example.blogproject.repository;

import org.example.blogproject.domain.Post;
import org.example.blogproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUser(User user);
    List<Post> findByUserAndIsDraft(User user, boolean isDraft);
    List<Post> findByIsDraftFalse();
    List<Post> findByPublishedTrueOrderByCreatedAtDesc(); // 최근 게시물 recent.html

}
