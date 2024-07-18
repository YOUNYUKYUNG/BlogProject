package org.example.blogproject.post.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.blogproject.login.domain.User;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostDraft {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long draftId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private boolean isPrivate;
}
