package org.example.blogproject.login.controller;

import lombok.RequiredArgsConstructor;
import org.example.blogproject.login.domain.User;
import org.example.blogproject.login.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/userregform")
    public String userregform(Model model) {
        model.addAttribute("user", new User());
        return "users/userregform";
    }

    @PostMapping("/userreg")
    public String userreg(@ModelAttribute("user") User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "users/userregform";
        }
        if (userService.existsByUsername(user.getUsername())) {
            result.rejectValue("username", null, "이미 사용중인 아이디입니다.");
            return "users/userregerror";
        }
        if (userService.existsByEmail(user.getEmail())) {
            result.rejectValue("email", null, "이미 사용중인 이메일입니다.");
            return "users/userregerror";
        }

        userService.registUser(user);
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
    public String loginform() {
        return "users/loginform";
    }

    @GetMapping("/info")
    public String info() {
        return "users/info";
    }
}
