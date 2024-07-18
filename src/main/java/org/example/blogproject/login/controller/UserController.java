package org.example.blogproject.login.controller;

import lombok.RequiredArgsConstructor;
import org.example.blogproject.login.domain.SocialLoginInfo;
import org.example.blogproject.login.domain.User;
import org.example.blogproject.login.dto.LoginRequestDto;
import org.example.blogproject.login.dto.LoginResponseDto;
import org.example.blogproject.login.dto.UserDto;
import org.example.blogproject.login.service.SocialLoginInfoService;
import org.example.blogproject.login.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

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
    public String home(Model model) {
        return "home";  // 유저 홈 페이지를 보여주는 템플릿 이름
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
                user.getId(),  // 수정된 부분
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
                return "redirect:/error"; // 20분 이상 경과한 경우 에러 페이지로 리다이렉트
            }

            // 유효한 경우 User 정보를 저장합니다.
            userService.saveUser(username, name, email, socialId, provider);

            return "redirect:/welcome";
        } else {
            return "redirect:/error"; // 해당 정보가 없는 경우 에러 페이지로 리다이렉트
        }
    }

    @GetMapping("/myProfile")
    public String myProfile() {
        return "myProfile";
    }
}
