package org.example.blogproject.login.controller;

import lombok.RequiredArgsConstructor;
import org.example.blogproject.login.domain.User;
import org.example.blogproject.login.dto.LoginRequestDto;
import org.example.blogproject.login.dto.LoginResponseDto;
import org.example.blogproject.login.dto.UserDto;
import org.example.blogproject.login.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
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

        userService.registUser(userDto);
        return "redirect:/userregsuccess";
    }

    @GetMapping("/userregsuccess")
    public String userregsuccess() {
        return "users/userregsuccess";
    }

    @GetMapping("/")
    public String home() {
        return "home";
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

        return "users/welcome";
    }

    @GetMapping("/myProfile")
    public String info() {
        return "myProfile";
    }
}
