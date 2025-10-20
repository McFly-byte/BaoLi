package com.baoli.pricer.context;

import com.baoli.pricer.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    public StompAuthChannelInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                try {
                    Jws<Claims> claims = jwtUtil.validate(token);
                    String username = claims.getBody().getSubject();
                    Authentication auth =
                            new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
                    accessor.setUser(auth); // 🔑 绑定用户到 WebSocket 会话
                } catch (Exception e) {
                    throw new IllegalArgumentException("无效的 JWT", e);
                }
            } else {
                throw new IllegalArgumentException("缺少 Authorization Header");
            }
        }
        return message;
    }
}

