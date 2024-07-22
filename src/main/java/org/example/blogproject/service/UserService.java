package org.example.blogproject.service;

import lombok.RequiredArgsConstructor;
import org.example.blogproject.domain.User;
import org.example.blogproject.domain.Role;
import org.example.blogproject.dto.UserDto;
import org.example.blogproject.repository.RoleRepository;
import org.example.blogproject.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registUser(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword())); // 비밀번호 인코딩
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());

        Role userRole = roleRepository.findByRoleName("USER"); // 기본 사용자 역할 설정
        user.setRoles(Collections.singleton(userRole));

        return userRepository.save(user); // 사용자 저장
    }

    @Transactional(readOnly = false)
    public User saveUser(String username, String name, String email, String socialId, String provider) {
        User user = new User();
        user.setUsername(username);
        user.setName(name);
        user.setEmail(email);
        user.setSocialId(socialId);
        user.setProvider(provider);
        user.setPassword(""); // 비밀번호는 소셜 로그인 사용자의 경우 비워둠
        return userRepository.save(user); // 사용자 저장
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username); // 사용자 이름으로 사용자 찾기
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username); // 사용자 이름 존재 여부 확인
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email); // 이메일 존재 여부 확인
    }

    @Transactional(readOnly = true)
    public Optional<User> findByProviderAndSocialId(String provider, String socialId) {
        return userRepository.findByProviderAndSocialId(provider, socialId); // 제공자와 소셜 ID로 사용자 찾기
    }

    @Transactional
    public User save(User user) {
        return userRepository.save(user); // 사용자 저장
    }


    @Transactional
    public void updateUserProfileImage(String username, String profileImageUrl) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setProfileImageUrl(profileImageUrl); // 프로필 이미지 URL 업데이트
            userRepository.save(user); // 사용자 저장
        }
    }
}