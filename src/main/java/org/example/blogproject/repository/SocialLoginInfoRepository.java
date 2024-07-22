package org.example.blogproject.repository;

import org.example.blogproject.domain.SocialLoginInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocialLoginInfoRepository extends JpaRepository<SocialLoginInfo, Long> {
   // 제공자, UUID, 소셜 ID로 소셜 로그인 정보를 찾는 메서드
   Optional<SocialLoginInfo> findByProviderAndUuidAndSocialId(String provider, String uuid, String socialId);

}
