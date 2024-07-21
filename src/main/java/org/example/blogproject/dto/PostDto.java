package org.example.blogproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.blogproject.domain.Post;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private Long postId;
    private String title;
    private String content;
    private boolean isPrivate;
    private boolean published;
    private long likesCount;
    private long viewsCount;
    private String previewImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<String> tags = new HashSet<>();
    private UserDto user;
    private boolean isDraft;
    private String userProfileImageUrl;


    public PostDto(Post post) {
        this.postId = post.getPostId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.isPrivate = post.isPrivate();
        this.published = post.isPublished();
        this.likesCount = post.getLikesCount();
        this.viewsCount = post.getViewsCount();
        this.previewImageUrl = post.getPreviewImageUrl();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
        this.user = new UserDto(post.getUser());
        this.isDraft = post.isDraft();
        this.userProfileImageUrl = post.getUser().getProfileImageUrl();
    }
}
