package org.example.blogproject.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.blogproject.login.dto.UserDto;

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
    private UserDto user; // User 필드 추가
    private boolean isDraft;
}
