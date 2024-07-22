package org.example.blogproject.service;

import lombok.RequiredArgsConstructor;
import org.example.blogproject.domain.Post;
import org.example.blogproject.domain.User;
import org.example.blogproject.dto.UserDto;
import org.example.blogproject.dto.PostDto;
import org.example.blogproject.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;

    @Override
    public List<Post> findPostsByUser(User user) {
        return postRepository.findByUser(user); // 사용자의 게시물 찾기
    }

    @Override
    public List<Post> findAllPosts() {
        return postRepository.findAll(); // 모든 게시물 찾기
    }

    @Override
    public Optional<Post> findPostById(Long id) {
        return postRepository.findById(id); // ID로 게시물 찾기
    }

    @Override
    public Post savePost(Post post) {
        return postRepository.save(post); // 게시물 저장
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

        if (updatedPost.isPublished()) {
            post.setDraft(false); // 게시된 경우 드래프트 상태 해제
        }

        postRepository.save(post); // 게시물 저장
    }

    @Override
    public void deletePost(Long id) {
        postRepository.deleteById(id); // ID로 게시물 삭제
    }

    @Override
    public List<Post> findDraftsByUser(User user) {
        return postRepository.findByUserAndIsDraft(user, true); // 사용자의 드래프트 게시물 찾기
    }

    @Override
    public List<Post> findAllPublishedPosts() {
        return postRepository.findByIsDraftFalse(); // 모든 게시된 게시물 찾기
    }

    @Override
    public PostDto convertToDto(Post post) {
        PostDto postDto = new PostDto();
        postDto.setPostId(post.getPostId());
        postDto.setTitle(post.getTitle());
        postDto.setContent(post.getContent().replaceAll("\\<.*?\\>", "")); // HTML 태그 제거
        postDto.setPublished(post.isPublished());
        postDto.setPrivate(post.isPrivate());
        postDto.setPreviewImageUrl(post.getPreviewImageUrl());
        postDto.setViewsCount(post.getViewsCount());
        postDto.setLikesCount(post.getLikesCount());
        postDto.setDraft(post.isDraft());
        postDto.setUser(convertToUserDto(post.getUser()));
        postDto.setCreatedAt(post.getCreatedAt());
        postDto.setUserProfileImageUrl(post.getUser().getProfileImageUrl()); // 추가된 부분
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
        post.setDraft(postDto.isDraft()); // 수정된 부분

        if (postDto.getUser() != null) {
            User user = new User();
            user.setId(postDto.getUser().getId());
            user.setUsername(postDto.getUser().getUsername());
            user.setName(postDto.getUser().getName());
            user.setEmail(postDto.getUser().getEmail());
            user.setProfileImageUrl(postDto.getUser().getProfileImageUrl());
            user.setProvider(postDto.getUser().getProvider());
            user.setSocialId(postDto.getUser().getSocialId());
            post.setUser(user); // 게시물 작성자 설정
        }

        return post;
    }

    public List<PostDto> findRecentPosts() {
        List<Post> posts = postRepository.findByPublishedTrueOrderByCreatedAtDesc();
        return posts.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
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
