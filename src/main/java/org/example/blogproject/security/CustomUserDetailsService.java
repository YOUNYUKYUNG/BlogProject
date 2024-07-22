package org.example.blogproject.security;

import lombok.RequiredArgsConstructor;
import org.example.blogproject.domain.User;
import org.example.blogproject.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 사용자 이름으로 사용자 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        // UserDetails 객체 생성 및 반환
        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword()) // 인코딩된 비밀번호 설정
                .authorities(user.getRoles().stream()
                        .map(role -> role.getRoleName()) // 사용자 권한 설정
                        .toArray(String[]::new))
                .accountExpired(false) // 계정 만료 여부 설정
                .accountLocked(false) // 계정 잠금 여부 설정
                .credentialsExpired(false) // 자격 증명 만료 여부 설정
                .disabled(false) // 계정 비활성화 여부 설정
                .build();
    }
}