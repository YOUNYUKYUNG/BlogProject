package org.example.blogproject.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String previewImageUrl;

    @Column(nullable = false)
    private boolean published = false;

    @Column(nullable = false)
    private boolean isPrivate = false;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime publishedAt;  // 게시물 출간 시간

    @Column(nullable = false)
    private long viewsCount = 0;

    @Column(nullable = false)
    private long likesCount = 0;

    @Column(nullable = false)
    private long commentsCount = 0;

    @ManyToMany
    @JoinTable(
            name = "post_tags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "series_posts",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "series_id")
    )
    private Set<PostSeries> series = new HashSet<>();
}
