package org.example.blogproject.post.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.blogproject.login.domain.User;
import org.example.blogproject.login.dto.UserDto;
import org.example.blogproject.post.domain.Post;
import org.example.blogproject.post.dto.PostDto;
import org.example.blogproject.post.repository.PostRepository;
import org.example.blogproject.post.service.PostService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Override
    public List<Post> findAllPosts() {
        return postRepository.findAll();
    }

    @Override
    public Optional<Post> findPostById(Long id) {
        return postRepository.findById(id);
    }

    @Override
    public void updatePost(Long id, Post post) {
        postRepository.save(post);
    }

    @Override
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    @Override
    public Post savePost(Post post) {
        post.setCreatedAt(LocalDateTime.now()); // createdAt 필드 설정
        return postRepository.save(post);
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


        // User 설정
        UserDto userDto = postDto.getUser();
        if (userDto != null) {
            User user = new User();
            user.setId(userDto.getId());
            user.setUsername(userDto.getUsername());
            user.setName(userDto.getName());
            user.setEmail(userDto.getEmail());
            user.setProfileImageUrl(userDto.getProfileImageUrl());
            user.setProvider(userDto.getProvider());
            user.setSocialId(userDto.getSocialId());
            post.setUser(user);
        }

        return post;
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
        postDto.setCreatedAt(post.getCreatedAt());

        // UserDto 추가
        User user = post.getUser();
        UserDto userDto = new UserDto(user.getId(), user.getUsername(), user.getName(), user.getEmail(), null, null, user.getProfileImageUrl(), user.getProvider(), user.getSocialId());
        postDto.setUser(userDto);

        return postDto;
    }
}
