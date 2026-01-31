package com.example.tictactoe.session.service;

import com.example.tictactoe.session.model.Session;
import com.example.tictactoe.session.client.dto.EngineGameResponse;

public interface SessionService {
    Session createSession();

    Session getSession(String sessionId);

    Session simulateSession(String sessionId);

    EngineGameResponse getEngineGame(String sessionId);
}
