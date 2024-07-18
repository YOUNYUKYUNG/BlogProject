package org.example.blogproject.login.service;

import lombok.RequiredArgsConstructor;
import org.example.blogproject.login.domain.Role;
import org.example.blogproject.login.domain.User;
import org.example.blogproject.login.dto.UserDto;
import org.example.blogproject.login.repository.RoleRepository;
import org.example.blogproject.login.repository.UserRepository;
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
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());

        Role userRole = roleRepository.findByRoleName("USER");
        user.setRoles(Collections.singleton(userRole));

        return userRepository.save(user);
    }

    @Transactional(readOnly = false)
    public User saveUser(String username, String name, String email, String socialId, String provider) {
        User user = new User();
        user.setUsername(username);
        user.setName(name);
        user.setEmail(email);
        user.setSocialId(socialId);
        user.setProvider(provider);
        user.setPassword(""); // 비밀번호는 소셜 로그인 사용자의 경우 비워둡니다.
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByProviderAndSocialId(String provider, String socialId) {
        return userRepository.findByProviderAndSocialId(provider, socialId);
    }
}
