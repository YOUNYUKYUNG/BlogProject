package org.example.blogproject.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PostSeries {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seriesId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String title;

    @ManyToMany(mappedBy = "series")
    private Set<Post> posts = new HashSet<>();
}
