package org.example.blogproject.post.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.blogproject.login.domain.User;

@Entity
@Table(name = "post_drafts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostDraft {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    private boolean isPrivate = true; // 비공개 상태로 기본 설정
}
