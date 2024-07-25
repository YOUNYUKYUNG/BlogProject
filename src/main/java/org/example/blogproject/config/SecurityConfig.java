package org.example.blogproject.config;

import lombok.RequiredArgsConstructor;
import org.example.blogproject.security.CustomOAuth2AuthenticationSuccessHandler;
import org.example.blogproject.security.CustomUserDetailsService;
import org.example.blogproject.service.SocialLoginInfoService;
import org.example.blogproject.service.SocialUserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration // Spring 설정 클래스를 나타냄
@EnableWebSecurity // Spring Security 설정을 활성화함
@RequiredArgsConstructor // Lombok을 사용하여 생성자를 자동으로 생성
public class SecurityConfig {
    private final SocialUserService socialUserService; // 소셜 사용자 서비스
    private final ApplicationContext applicationContext; // Spring 애플리케이션 컨텍스트
    private final CustomUserDetailsService customUserDetailsService;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/userregform", "/userreg", "/","/recent","/like","/tag",
                                "/series","/chat", "/loginform", "/css/**", "/js/**", "/image/**",
                                "/uploads/**","/tip").permitAll() // 특정 경로에 대한 접근을 허용
                        .requestMatchers("/oauth2/**", "/login/oauth2/code/github", "/registerSocialUser", "/saveSocialUser").permitAll() // OAuth2 경로에 대한 접근을 허용
                        .requestMatchers("/api/**").permitAll() // API 경로 접근 허용
                        .requestMatchers("/posts/view/**").permitAll() // 특정 경로 접근 허용
                        .requestMatchers("/ws","/chat").permitAll()
                        .requestMatchers("/users/myprofile").authenticated()
                        .anyRequest().authenticated() // 그 외의 모든 요청에 대해 인증을 요구
                )
                .userDetailsService(customUserDetailsService)
                .csrf(csrf -> csrf.disable()) // CSRF 보호를 비활성화
                .formLogin(form -> form
                        .loginPage("/loginform") // 로그인 페이지 설정
                        .loginProcessingUrl("/login") // 로그인 처리 URL 설정
                        .defaultSuccessUrl("/welcome", true) // 로그인 성공 시 리다이렉트할 기본 URL 설정
                        .failureUrl("/loginform?error=true") // 로그인 실패 시 리다이렉트할 URL 설정
                )
                .cors(cors -> cors.configurationSource(configurationSource())) // CORS 설정 적용
                .httpBasic(httpBasic -> httpBasic.disable()) // HTTP Basic 인증 비활성화
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/loginform") // OAuth2 로그인 페이지 설정
                        .defaultSuccessUrl("/welcome", true) // OAuth2 로그인 성공 시 리다이렉트할 기본 URL 설정
                        .failureUrl("/loginFailure") // OAuth2 로그인 실패 시 리다이렉트할 URL 설정
                        .userInfoEndpoint(userInfo -> userInfo.userService(this.oauth2UserService())) // 사용자 정보 엔드포인트 설정
                        .successHandler(customOAuth2AuthenticationSuccessHandler()) // OAuth2 인증 성공 핸들러 설정
                );

        return http.build();
    }

    @Bean
    public CustomOAuth2AuthenticationSuccessHandler customOAuth2AuthenticationSuccessHandler() {
        return new CustomOAuth2AuthenticationSuccessHandler(applicationContext.getBean(SocialLoginInfoService.class), applicationContext); // OAuth2 인증 성공 핸들러 빈 생성
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService(); // 기본 OAuth2 사용자 서비스 사용
        return oauth2UserRequest -> {
            OAuth2User oauth2User = delegate.loadUser(oauth2UserRequest); // 사용자 정보를 로드

            String provider = oauth2UserRequest.getClientRegistration().getRegistrationId(); // 제공자 정보 추출
            String socialId = String.valueOf(oauth2User.getAttributes().get("id")); // 소셜 ID 추출
            String username = (String) oauth2User.getAttributes().get("login"); // 사용자명 추출
            String email = (String) oauth2User.getAttributes().get("email"); // 이메일 추출
            String avatarUrl = (String) oauth2User.getAttributes().get("avatar_url"); // 아바타 URL 추출

            socialUserService.saveOrUpdateUser(socialId, provider, username, email, avatarUrl); // 사용자 정보 저장 또는 업데이트

            return oauth2User; // OAuth2 사용자 반환
        };
    }

    public CorsConfigurationSource configurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); // URL 기반 CORS 구성 소스 생성
        CorsConfiguration config = new CorsConfiguration(); // 새로운 CORS 구성 생성
        config.addAllowedOrigin("*"); // 모든 오리진 허용
        config.addAllowedHeader("*"); // 모든 헤더 허용
        config.addAllowedMethod("*"); // 모든 메서드 허용
        config.setAllowedMethods(List.of("GET", "POST", "DELETE")); // 특정 메서드만 허용
        source.registerCorsConfiguration("/**", config); // 모든 경로에 대해 CORS 구성 적용
        return source; // CORS 구성 소스 반환
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt 비밀번호 인코더 빈 생성
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager(); // 인증 관리자 빈 생성
    }

}
