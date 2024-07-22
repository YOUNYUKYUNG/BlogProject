package org.example.blogproject.service;

import org.example.blogproject.domain.Post;
import org.example.blogproject.domain.User;
import org.example.blogproject.dto.PostDto;

import java.util.List;
import java.util.Optional;

public interface PostService {
    // 모든 게시물을 조회하는 메서드
    List<Post> findAllPosts();

    // 게시물 ID로 게시물을 조회하는 메서드
    Optional<Post> findPostById(Long id);

    // 특정 사용자의 게시물을 조회하는 메서드
    List<Post> findPostsByUser(User user);

    // 새로운 게시물을 저장하는 메서드
    Post savePost(Post post);

    // 게시물을 삭제하는 메서드
    void deletePost(Long id);

    // 게시물을 업데이트하는 메서드
    void updatePost(Long id, Post updatedPost);

    // Post 엔티티를 PostDto로 변환하는 메서드
    PostDto convertToDto(Post post);

    // PostDto를 Post 엔티티로 변환하는 메서드
    Post convertToEntity(PostDto postDto);

    // 특정 사용자의 드래프트 게시물을 조회하는 메서드
    List<Post> findDraftsByUser(User user);

    // 모든 게시된 게시물을 조회하는 메서드
    List<Post> findAllPublishedPosts();

    // 최근 게시물을 조회하는 메서드
    List<PostDto> findRecentPosts();
}
