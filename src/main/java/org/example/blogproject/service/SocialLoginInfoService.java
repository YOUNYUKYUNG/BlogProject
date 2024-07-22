package org.example.blogproject.service;

import lombok.RequiredArgsConstructor;
import org.example.blogproject.domain.SocialLoginInfo;
import org.example.blogproject.repository.SocialLoginInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SocialLoginInfoService {
    private final SocialLoginInfoRepository socialLoginInfoRepository;

    @Transactional(readOnly = false)
    public SocialLoginInfo saveSocialLoginInfo(String provider, String socialId){
        SocialLoginInfo socialLoginInfo = new SocialLoginInfo();
        socialLoginInfo.setProvider(provider);
        socialLoginInfo.setSocialId(socialId);

        return socialLoginInfoRepository.save(socialLoginInfo); // 소셜 로그인 정보 저장
    }

    @Transactional(readOnly = true)
    public Optional<SocialLoginInfo> findByProviderAndUuidAndSocialId(String provider, String uuid, String socialId){
        return socialLoginInfoRepository.findByProviderAndUuidAndSocialId(provider, uuid, socialId); // 제공자, UUID, 소셜 ID로 소셜 로그인 정보 조회
    }
}
