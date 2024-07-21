package org.example.blogproject.controller;

import lombok.RequiredArgsConstructor;
import org.example.blogproject.domain.SocialLoginInfo;
import org.example.blogproject.domain.User;
import org.example.blogproject.dto.LoginRequestDto;
import org.example.blogproject.dto.LoginResponseDto;
import org.example.blogproject.dto.UserDto;
import org.example.blogproject.service.SocialLoginInfoService;
import org.example.blogproject.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final SocialLoginInfoService socialLoginInfoService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/userregform")
    public String userregform(Model model) {
        model.addAttribute("userDto", new UserDto());
        return "users/userregform";
    }

    @PostMapping("/userreg")
    public String userreg(@ModelAttribute("userDto") @Valid UserDto userDto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "users/userregform";
        }
        if (userService.existsByUsername(userDto.getUsername())) {
            result.rejectValue("username", null, "이미 사용중인 아이디입니다.");
            return "users/userregform";
        }
        if (userService.existsByEmail(userDto.getEmail())) {
            result.rejectValue("email", null, "이미 사용중인 이메일입니다.");
            return "users/userregform";
        }
        if (userDto.getPassword() == null || userDto.getPassword().isEmpty()) {
            result.rejectValue("password", null, "비밀번호를 입력해주세요.");
            return "users/userregform";
        }

        userService.registUser(userDto);
        return "redirect:/welcome";
    }

    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userService.findByUsername(username).orElse(null);
            if (user != null) {
                model.addAttribute("user", user);
            }
        }
        return "main/home";
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "users/welcome";
    }

    @GetMapping("/loginform")
    public String loginform(Model model) {
        model.addAttribute("loginRequest", new LoginRequestDto());
        return "users/loginform";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("loginRequest") @Valid LoginRequestDto loginRequest, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "users/loginform";
        }

        User user = userService.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            result.rejectValue("password", null, "Invalid username or password");
            return "users/loginform";
        }

        LoginResponseDto loginResponse = new LoginResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getName()
        );
        model.addAttribute("user", loginResponse);

        return "redirect:/welcome";
    }

    @GetMapping("/registerSocialUser")
    public String showRegisterSocialUserForm(@RequestParam("provider") String provider,
                                             @RequestParam("socialId") String socialId,
                                             @RequestParam("name") String name,
                                             @RequestParam("uuid") String uuid,
                                             Model model) {
        model.addAttribute("provider", provider);
        model.addAttribute("socialId", socialId);
        model.addAttribute("name", name);
        model.addAttribute("uuid", uuid);
        return "users/registerSocialUser";
    }

    @PostMapping("/saveSocialUser")
    public String saveSocialUser(@RequestParam("provider") String provider,
                                 @RequestParam("socialId") String socialId,
                                 @RequestParam("name") String name,
                                 @RequestParam("username") String username,
                                 @RequestParam("email") String email,
                                 @RequestParam("uuid") String uuid,
                                 Model model) {
        Optional<SocialLoginInfo> socialLoginInfoOptional = socialLoginInfoService.findByProviderAndUuidAndSocialId(provider, uuid, socialId);

        if (socialLoginInfoOptional.isPresent()) {
            SocialLoginInfo socialLoginInfo = socialLoginInfoOptional.get();
            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(socialLoginInfo.getCreatedAt(), now);

            if (duration.toMinutes() > 20) {
                return "redirect:/error";
            }

            userService.saveUser(username, name, email, socialId, provider);

            return "redirect:/welcome";
        } else {
            return "redirect:/error";
        }
    }

    @GetMapping("/myprofile")
    public String myprofile(Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        model.addAttribute("user", user);
        return "users/myprofile";
    }

    @PostMapping("/update-username")
    public String updateUsername(@RequestParam String newUsername, Authentication authentication, Model model) {
        String currentUsername = authentication.getName();
        User user = userService.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user"));

        user.setUsername(newUsername);
        userService.save(user);

        String message = URLEncoder.encode("사용자 아이디가 성공적으로 업데이트되었습니다.", StandardCharsets.UTF_8);
        return "redirect:/myprofile?message=" + message;
    }

}
