package com.example.tictactoe.session.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SessionWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, List<WebSocketSession>> sessionsById = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public SessionWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = extractSessionId(session);
        if (sessionId == null || sessionId.isBlank()) {
            session.close(CloseStatus.BAD_DATA);
            return;
        }
        sessionsById.computeIfAbsent(sessionId, key -> new CopyOnWriteArrayList<>()).add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        removeSession(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        removeSession(session);
    }

    public void sendSessionUpdate(String sessionId, Object payload) {
        List<WebSocketSession> sessions = sessionsById.get(sessionId);
        if (sessions == null || sessions.isEmpty()) {
            return;
        }
        String json;
        try {
            json = objectMapper.writeValueAsString(payload);
        } catch (IOException ex) {
            return;
        }
        for (WebSocketSession session : sessions) {
            if (!session.isOpen()) {
                removeSession(session);
                continue;
            }
            try {
                session.sendMessage(new TextMessage(json));
            } catch (IOException ex) {
                removeSession(session);
            }
        }
    }

    private void removeSession(WebSocketSession session) {
        sessionsById.values().forEach(list -> list.remove(session));
        sessionsById.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    private String extractSessionId(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri == null || uri.getQuery() == null) {
            return null;
        }
        String query = uri.getQuery();
        String[] params = query.split("&");
        for (String param : params) {
            String[] pair = param.split("=", 2);
            if (pair.length == 2 && "sessionId".equals(pair[0])) {
                return URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
            }
        }
        return null;
    }
}
