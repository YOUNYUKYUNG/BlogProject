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
    private final PasswordEncoder passwordEncoder; // 비밀번호 인코더

    @GetMapping("/userregform")
    public String userregform(Model model) {
        model.addAttribute("userDto", new UserDto()); // 새로운 UserDto 객체를 모델에 추가
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

        userService.registUser(userDto); // 사용자 등록
        return "redirect:/welcome";
    }

    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName(); // 인증된 사용자 이름 가져오기
            User user = userService.findByUsername(username).orElse(null); // 사용자 찾기
            if (user != null) {
                model.addAttribute("user", user); // 모델에 사용자 추가
            }
        }
        return "main/home";
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "users/welcome";
    }

    @GetMapping("/tip")
    public String tip() {
        return "main/tip";
    }

    @GetMapping("/like")
    public String like() {
        return "main/like";
    }

    @GetMapping("/tag")
    public String tag() {
        return "main/tag";
    }

    @GetMapping("/series")
    public String series() {
        return "main/series";
    }

    @GetMapping("/loginform")
    public String loginform(Model model) {
        model.addAttribute("loginRequest", new LoginRequestDto()); // 새로운 LoginRequestDto 객체를 모델에 추가
        return "users/loginform";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("loginRequest") @Valid LoginRequestDto loginRequest, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "users/loginform";
        }

        User user = userService.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password")); // 사용자 찾기

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            result.rejectValue("password", null, "Invalid username or password");
            return "users/loginform"; // 비밀번호 불일치 시 로그인 폼 페이지로 이동
        }

        LoginResponseDto loginResponse = new LoginResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getName()
        );
        model.addAttribute("user", loginResponse); // 모델에 로그인 응답 추가

        return "redirect:/welcome";
    }

    @GetMapping("/registerSocialUser") // 소셜 사용자 등록 폼
    public String showRegisterSocialUserForm(@RequestParam("provider") String provider,
                                             @RequestParam("socialId") String socialId,
                                             @RequestParam("name") String name,
                                             @RequestParam("uuid") String uuid,
                                             Model model) {
        model.addAttribute("provider", provider); // 모델에 소셜 로그인 제공자 추가
        model.addAttribute("socialId", socialId); // 모델에 소셜 ID 추가
        model.addAttribute("name", name); // 모델에 이름 추가
        model.addAttribute("uuid", uuid); // 모델에 UUID 추가
        return "users/registerSocialUser"; // 소셜 사용자 등록 폼 페이지로 이동
    }

    @PostMapping("/saveSocialUser")
    public String saveSocialUser(@RequestParam("provider") String provider,
                                 @RequestParam("socialId") String socialId,
                                 @RequestParam("name") String name,
                                 @RequestParam("username") String username,
                                 @RequestParam("email") String email,
                                 @RequestParam("uuid") String uuid) {
        Optional<SocialLoginInfo> socialLoginInfoOptional = socialLoginInfoService.findByProviderAndUuidAndSocialId(provider, uuid, socialId);

        if (socialLoginInfoOptional.isPresent()) {
            SocialLoginInfo socialLoginInfo = socialLoginInfoOptional.get();
            LocalDateTime now = LocalDateTime.now(); // 현재 시간
            Duration duration = Duration.between(socialLoginInfo.getCreatedAt(), now); // 생성 시간과 현재 시간의 차이

            if (duration.toMinutes() > 20) {
                return "redirect:/error";
            }

            userService.saveUser(username, name, email, socialId, provider); // 사용자 저장

            return "redirect:/welcome";
        } else {
            return "redirect:/error";
        }
    }

    @GetMapping("/myprofile")
    public String myprofile(Authentication authentication, Model model) {
        String username = authentication.getName(); // 인증된 사용자 이름 가져오기
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username)); // 사용자 찾기
        model.addAttribute("user", user); // 모델에 사용자 추가
        return "users/myprofile";
    }

    @PostMapping("/update-username")
    public String updateUsername(@RequestParam String newUsername, Authentication authentication, Model model) {
        String currentUsername = authentication.getName(); // 인증된 사용자 이름 가져오기
        User user = userService.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user")); // 사용자 찾기

        user.setUsername(newUsername); // 사용자 아이디 업데이트
        userService.save(user); // 사용자 저장

        String message = "사용자 아이디가 성공적으로 업데이트되었습니다.";
        model.addAttribute("message", message); // 성공 메시지 추가

        // 수정된 사용자 정보를 다시 모델에 추가
        model.addAttribute("user", user);

        return "users/myprofile"; // myprofile.html로 돌아감
    }

}
