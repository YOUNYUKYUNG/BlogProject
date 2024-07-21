package org.example.blogproject.service;

import org.example.blogproject.domain.Post;
import org.example.blogproject.domain.User;
import org.example.blogproject.dto.PostDto;

import java.util.List;
import java.util.Optional;

public interface PostService {
    List<Post> findAllPosts();
    Optional<Post> findPostById(Long id);
    List<Post> findPostsByUser(User user);
    Post savePost(Post post);
    void deletePost(Long id);
    void updatePost(Long id, Post updatedPost);
    PostDto convertToDto(Post post);
    Post convertToEntity(PostDto postDto);
    List<Post> findDraftsByUser(User user);
    List<Post> findAllPublishedPosts();
    List<PostDto> findRecentPosts();
}
