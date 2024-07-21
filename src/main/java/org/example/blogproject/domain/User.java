package org.example.blogproject.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;//아이디

    @Column(nullable = false, length = 100)
    private String password;

    @Transient
    private String passwordConfirm;  // 비밀번호 확인을 위해 추가한 필드

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false,length = 100)
    private String email;

    @Column(name="registration_date", nullable = false, updatable = false)
    private LocalDateTime registrationDate = LocalDateTime.now();

    @Column
    private String profileImageUrl;

    @Column(name = "social_id")
    private String socialId;
    private String provider;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;


}