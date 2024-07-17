package org.example.blogproject.config;

import lombok.RequiredArgsConstructor;
import org.example.blogproject.login.security.CustomOAuth2AuthenticationSuccessHandler;
import org.example.blogproject.login.service.SocialLoginInfoService;
import org.example.blogproject.login.service.SocialUserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
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

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final SocialUserService socialUserService;
    private final ApplicationContext applicationContext;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/userregform", "/userreg", "/", "/loginform", "/css/**", "/js/**", "/image/**").permitAll()
                        .requestMatchers("/oauth2/**", "/login/oauth2/code/github", "/registerSocialUser", "/saveSocialUser").permitAll()
                        .requestMatchers("/api/**").permitAll() // API 경로 접근 허용
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form
                        .loginPage("/loginform")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/welcome", true)
                        .failureUrl("/loginform?error=true")
                )
                .cors(cors -> cors.configurationSource(configurationSource()))
                .httpBasic(httpBasic -> httpBasic.disable())
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/loginform")
                        .defaultSuccessUrl("/welcome", true)
                        .failureUrl("/loginFailure")
                        .userInfoEndpoint(userInfo -> userInfo.userService(this.oauth2UserService()))
                        .successHandler(customOAuth2AuthenticationSuccessHandler())
                );

        return http.build();
    }

    @Bean
    public CustomOAuth2AuthenticationSuccessHandler customOAuth2AuthenticationSuccessHandler() {
        return new CustomOAuth2AuthenticationSuccessHandler(applicationContext.getBean(SocialLoginInfoService.class), applicationContext);
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        return oauth2UserRequest -> {
            OAuth2User oauth2User = delegate.loadUser(oauth2UserRequest);

            String token = oauth2UserRequest.getAccessToken().getTokenValue();
            String provider = oauth2UserRequest.getClientRegistration().getRegistrationId();
            String socialId = String.valueOf(oauth2User.getAttributes().get("id"));
            String username = (String) oauth2User.getAttributes().get("login");
            String email = (String) oauth2User.getAttributes().get("email");
            String avatarUrl = (String) oauth2User.getAttributes().get("avatar_url");

            socialUserService.saveOrUpdateUser(socialId, provider, username, email, avatarUrl);

            return oauth2User;
        };
    }

    public CorsConfigurationSource configurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowedMethods(List.of("GET", "POST", "DELETE"));
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
