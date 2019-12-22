package com.kafka.viewer.ws;

import com.kafka.viewer.service.KafkaWsPuller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class WebSocketMessagesPullHandler implements WebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(WebSocketMessagesPullHandler.class);
    
    @Autowired
    private KafkaWsPuller kafkaWsPuller;
    
    private static Map<String, String> parseQuery(String query) {
        return Stream
                .of(query.split("&"))
                .map(part -> part.split("="))
                .collect(Collectors.toMap(splitPart -> splitPart[0], splitPart -> splitPart[1]));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) {
        String addr = Objects.requireNonNull(webSocketSession.getRemoteAddress()).toString();

        URI uri = webSocketSession.getUri();

        assert uri != null : "Connection requires query to start";

        String query = uri.getQuery();

        Map<String, String> queryMap = parseQuery(query);

        log.info("WebSocket connection ESTABLISHED, addr - {}", addr);
        
        kafkaWsPuller.assignKafkaPull(webSocketSession, queryMap);
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) {
    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) {
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) {
        String addr = Objects.requireNonNull(webSocketSession.getRemoteAddress()).toString();
        
        kafkaWsPuller.closeKafkaPull(webSocketSession);

        log.info("WebSocket connection CLOSED, addr - {}", addr);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
