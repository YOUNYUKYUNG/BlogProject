package org.example.blogproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebSocketController {
    @GetMapping("/chat")
    public String chat(){
        return "main/chat";
    }
}
