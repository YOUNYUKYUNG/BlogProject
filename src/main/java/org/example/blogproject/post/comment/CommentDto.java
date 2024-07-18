package org.example.blogproject.post.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.blogproject.login.dto.UserDto;
import org.example.blogproject.post.dto.PostDto;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long commentId;
    private String content;
    private UserDto user;
    private PostDto post;
    private LocalDateTime createdAt;

}