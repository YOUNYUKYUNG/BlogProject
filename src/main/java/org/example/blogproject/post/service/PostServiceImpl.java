package org.example.blogproject.post.service;

import lombok.RequiredArgsConstructor;
import org.example.blogproject.login.domain.User;
import org.example.blogproject.login.dto.UserDto;
import org.example.blogproject.post.domain.Post;
import org.example.blogproject.post.dto.PostDto;
import org.example.blogproject.post.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;

    @Override
    public List<Post> findPostsByUser(User user) {
        return postRepository.findByUser(user);
    }

    @Override
    public List<Post> findAllPosts() {
        return postRepository.findAll();
    }

    @Override
    public Optional<Post> findPostById(Long id) {
        return postRepository.findById(id);
    }

    @Override
    public Post savePost(Post post) {
        return postRepository.save(post);
    }

    @Override
    public void updatePost(Long id, Post updatedPost) {
        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Post not found"));
        post.setTitle(updatedPost.getTitle());
        post.setContent(updatedPost.getContent());
        post.setPublished(updatedPost.isPublished());
        post.setPrivate(updatedPost.isPrivate());
        post.setPreviewImageUrl(updatedPost.getPreviewImageUrl());
        post.setViewsCount(updatedPost.getViewsCount());
        post.setLikesCount(updatedPost.getLikesCount());
        postRepository.save(post);
    }

    @Override
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    @Override
    public PostDto convertToDto(Post post) {
        PostDto postDto = new PostDto();
        postDto.setPostId(post.getPostId());
        postDto.setTitle(post.getTitle());
        postDto.setContent(post.getContent());
        postDto.setPublished(post.isPublished());
        postDto.setPrivate(post.isPrivate());
        postDto.setPreviewImageUrl(post.getPreviewImageUrl());
        postDto.setViewsCount(post.getViewsCount());
        postDto.setLikesCount(post.getLikesCount());
        postDto.setUser(convertToUserDto(post.getUser()));
        return postDto;
    }

    @Override
    public Post convertToEntity(PostDto postDto) {
        Post post = new Post();
        post.setPostId(postDto.getPostId());
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setPublished(postDto.isPublished());
        post.setPrivate(postDto.isPrivate());
        post.setPreviewImageUrl(postDto.getPreviewImageUrl());
        post.setViewsCount(postDto.getViewsCount());
        post.setLikesCount(postDto.getLikesCount());

        if (postDto.getUser() != null) {
            User user = new User();
            user.setId(postDto.getUser().getId());
            user.setUsername(postDto.getUser().getUsername());
            user.setName(postDto.getUser().getName());
            user.setEmail(postDto.getUser().getEmail());
            user.setProfileImageUrl(postDto.getUser().getProfileImageUrl());
            user.setProvider(postDto.getUser().getProvider());
            user.setSocialId(postDto.getUser().getSocialId());
            post.setUser(user);
        }

        return post;
    }

    private UserDto convertToUserDto(User user) {
        if (user == null) {
            return null;
        }
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setProfileImageUrl(user.getProfileImageUrl());
        userDto.setProvider(user.getProvider());
        userDto.setSocialId(user.getSocialId());
        return userDto;
    }
}
