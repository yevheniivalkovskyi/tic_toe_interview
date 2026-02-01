package com.example.tictactoe.session.ws;

import com.example.tictactoe.session.api.dto.SessionResponse;
import com.example.tictactoe.session.model.Session;
import org.springframework.stereotype.Component;

@Component
public class SessionUpdatePublisher {

    private final SessionWebSocketHandler sessionWebSocketHandler;

    public SessionUpdatePublisher(SessionWebSocketHandler sessionWebSocketHandler) {
        this.sessionWebSocketHandler = sessionWebSocketHandler;
    }

    public void publish(Session session) {
        if (session == null || session.getSessionId() == null) {
            return;
        }
        SessionResponse response = new SessionResponse(session);
        sessionWebSocketHandler.sendSessionUpdate(session.getSessionId(), response);
    }
}
