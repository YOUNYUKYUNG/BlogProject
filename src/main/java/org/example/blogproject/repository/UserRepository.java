package org.example.blogproject.repository;

import org.example.blogproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 사용자 이름으로 사용자 찾기
    Optional<User> findByUsername(String username);

    // 사용자 이름이 존재하는지 확인
    boolean existsByUsername(String username);

    // 이메일이 존재하는지 확인
    boolean existsByEmail(String email);

    // 제공자와 소셜 ID로 사용자 찾기
    Optional<User> findByProviderAndSocialId(String provider, String socialId); // 추가된 부분
}