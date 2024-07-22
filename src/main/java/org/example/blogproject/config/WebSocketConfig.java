package org.example.blogproject.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    //security와 함께쓰기위해서 추가
    @Autowired
    private CustomHandshakeInterceptor customHandshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new MyWebSocketHandler(), "/ws").setAllowedOrigins("*")
                //security와 함께쓰기위해서 추가
                .addInterceptors(customHandshakeInterceptor);
    }


}
