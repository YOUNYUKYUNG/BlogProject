package org.example.blogproject.post.service;

import org.example.blogproject.login.domain.User;
import org.example.blogproject.post.domain.Post;
import org.example.blogproject.post.dto.PostDto;

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
}
