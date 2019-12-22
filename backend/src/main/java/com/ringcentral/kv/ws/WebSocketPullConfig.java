package com.ringcentral.kv.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketPullConfig implements WebSocketConfigurer {
    
    private static final String WS_ROUTE = "/messages";
    
    @Autowired
    private WebSocketMessagesPullHandler webSocketMessagesPullHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry
                .addHandler(webSocketMessagesPullHandler, WS_ROUTE);
    }
}
