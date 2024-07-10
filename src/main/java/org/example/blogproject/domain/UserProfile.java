package org.example.blogproject.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserProfile {
    @Id
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "TEXT")
    private String profileImageUrl;

    @Column(length = 100)
    private String blogTitle;

//    @Column(columnDefinition = "JSONB")
//    private String emailNotifications;
}
