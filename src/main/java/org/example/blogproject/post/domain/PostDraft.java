package org.example.blogproject.post.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.blogproject.login.domain.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class PostDraft {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long draftId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String previewImageUrl;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private LocalDateTime updatedAt;
}
