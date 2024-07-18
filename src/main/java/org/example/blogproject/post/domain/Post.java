package org.example.blogproject.post.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.blogproject.post.tag.Tag;
import org.example.blogproject.login.domain.User;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private boolean published = false;

    @Column(nullable = false)
    private boolean isPrivate = false;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;

    private String previewImageUrl;

    @Column(nullable = false)
    private long viewsCount;

    @Column(nullable = false)
    private long likesCount;

    private boolean isDraft;

    @ManyToMany
    @JoinTable(
            name = "post_tags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();
}