package org.example.blogproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor // 모든 필드를 매개변수로 갖는 생성자 생성
@NoArgsConstructor // 기본 생성자 생성
public class CommentDto {
    private Long commentId;
    private String content;
    private UserDto user;
    private PostDto post;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
