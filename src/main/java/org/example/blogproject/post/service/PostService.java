package org.example.blogproject.post.service;

import org.example.blogproject.post.domain.Post;
import org.example.blogproject.post.dto.PostDto;

import java.util.List;
import java.util.Optional;

public interface PostService {
    List<Post> findAllPosts();
    Optional<Post> findPostById(Long id);
    Post savePost(Post post);
    void updatePost(Long id, Post post);
    void deletePost(Long id);
    Post convertToEntity(PostDto postDto);
    PostDto convertToDto(Post post);


}
