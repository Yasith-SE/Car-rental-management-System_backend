package edu.icet.config;

import edu.icet.websocket.SupportChatWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class SupportChatWebSocketConfig implements WebSocketConfigurer {

    private final SupportChatWebSocketHandler supportChatWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(supportChatWebSocketHandler, "/ws/support-chat")
                .setAllowedOrigins("http://localhost:5173", "http://127.0.0.1:5173");
    }
}
