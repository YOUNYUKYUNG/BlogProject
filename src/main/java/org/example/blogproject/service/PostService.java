package org.example.blogproject.service;

import org.example.blogproject.domain.Post;
import org.example.blogproject.domain.User;
import org.example.blogproject.dto.PostDto;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface PostService {
    List<Post> findAllPosts(); // 모든 게시물을 조회하는 메서드
    Optional<Post> findPostById(Long id); // 게시물 ID로 게시물을 조회하는 메서드
    List<Post> findPostsByUser(User user); // 특정 사용자의 게시물을 조회하는 메서드
    Post savePost(Post post); // 새로운 게시물을 저장하는 메서드
    void deletePost(Long id); // 게시물을 삭제하는 메서드
    void updatePost(Long id, Post updatedPost); // 게시물을 업데이트하는 메서드
    PostDto convertToDto(Post post); // Post 엔티티를 PostDto로 변환하는 메서드
    Post convertToEntity(PostDto postDto); // PostDto를 Post 엔티티로 변환하는 메서드
    List<Post> findDraftsByUser(User user); // 특정 사용자의 드래프트 게시물을 조회하는 메서드
    List<Post> findAllPublishedPosts(); // 모든 게시된 게시물을 조회하는 메서드
    List<PostDto> findRecentPosts(); // 최근 게시물을 조회하는 메서드
    Optional<Post> incrementViewsCount(Long postId); // 조회수를 증가시키는 메서드
    List<Post> findTopViewedPosts(); // 조회수가 높은 순으로 게시된 게시물을 조회하는 메서드
}