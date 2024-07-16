package org.example.blogproject.login.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.blogproject.login.domain.Role;
import org.example.blogproject.login.domain.SocialLoginInfo;
import org.example.blogproject.login.domain.User;
import org.example.blogproject.login.service.SocialLoginInfoService;
import org.example.blogproject.login.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final SocialLoginInfoService socialLoginInfoService;
    private final ApplicationContext applicationContext;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String requestUri = request.getRequestURI();
        String provider = extractProviderFromUri(requestUri);
        if (provider == null) {
            response.sendRedirect("/");
            return;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) auth.getPrincipal();

        int socialId = (int) defaultOAuth2User.getAttributes().get("id");
        String name = (String) defaultOAuth2User.getAttributes().get("name");

        UserService userService = applicationContext.getBean(UserService.class);
        Optional<User> userOptional = userService.findByProviderAndSocialId(provider, String.valueOf(socialId));
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            CustomUserDetails customUserDetails = new CustomUserDetails(
                    user.getUsername(),
                    user.getPassword(),
                    user.getName(),
                    user.getRoles().stream().map(Role::getName).collect(Collectors.toList())
            );

            Authentication newAuth = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);

            response.sendRedirect("/welcome");
        } else {
            SocialLoginInfo socialLoginInfo = socialLoginInfoService.saveSocialLoginInfo(provider, String.valueOf(socialId));
            response.sendRedirect("/registerSocialUser?provider=" + provider + "&socialId=" + socialId + "&name=" + name + "&uuid=" + socialLoginInfo.getUuid());
        }
    }

    private String extractProviderFromUri(String uri) {
        if (uri == null || uri.isBlank()) {
            return null;
        }

        if (!uri.startsWith("/login/oauth2/code/")) {
            return null;
        }

        String[] segments = uri.split("/");
        return segments[segments.length - 1];
    }
}