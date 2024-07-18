package org.example.blogproject.login.controller;

import lombok.RequiredArgsConstructor;
import org.example.blogproject.login.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserRestController {
    private final UserService userService;

    @GetMapping("/check-username") // HTTP GET 요청을 처리하며, /check-username 엔드포인트에 매핑
    public Map<String, Boolean> checkUsername(@RequestParam String username) { // 쿼리 파라미터로 username을 받음
        boolean available = !userService.existsByUsername(username); // username의 사용 가능 여부를 확인
        return Collections.singletonMap("available", available); // 사용 가능 여부를 맵으로 반환
    }

    @GetMapping("/check-email") // HTTP GET 요청을 처리하며, /check-email 엔드포인트에 매핑
    public Map<String, Boolean> checkEmail(@RequestParam String email) { // 쿼리 파라미터로 email을 받음
        boolean available = !userService.existsByEmail(email); // email의 사용 가능 여부를 확인
        return Collections.singletonMap("available", available); // 사용 가능 여부를 맵으로 반환
    }
}
