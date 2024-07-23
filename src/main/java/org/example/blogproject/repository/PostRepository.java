package org.example.blogproject.repository;

import org.example.blogproject.domain.Post;
import org.example.blogproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    // 특정 사용자의 모든 게시물을 가져오는 메서드
    List<Post> findByUser(User user);

    // 특정 사용자의 드래프트 게시물을 가져오는 메서드
    List<Post> findByUserAndIsDraft(User user, boolean isDraft);

    // 드래프트가 아닌 모든 게시물을 가져오는 메서드
    List<Post> findByIsDraftFalse();

    // 게시된 게시물을 생성일 기준으로 내림차순으로 가져오는 메서드
    List<Post> findByPublishedTrueOrderByCreatedAtDesc(); // 최근 게시물 recent.html

    // 조회수가 높은 순으로 게시된 게시물 찾기
    List<Post> findByPublishedTrueOrderByViewsCountDesc();
}
