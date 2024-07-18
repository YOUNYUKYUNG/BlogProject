package org.example.blogproject.post.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostDto {
    private String title;
    private String content;
    private List<String> tags;
    private boolean isPublished;
    private boolean isPrivate;
}