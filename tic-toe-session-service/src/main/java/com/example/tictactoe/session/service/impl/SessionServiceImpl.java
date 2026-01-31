package com.example.tictactoe.session.service.impl;

import com.example.tictactoe.core.dto.ErrorResponse;
import com.example.tictactoe.core.util.TicToeConstants;
import com.example.tictactoe.session.client.EngineClient;
import com.example.tictactoe.session.client.dto.EngineGameResponse;
import com.example.tictactoe.session.client.dto.EngineMoveRequest;
import com.example.tictactoe.session.model.MoveRecord;
import com.example.tictactoe.session.model.Session;
import com.example.tictactoe.session.model.SessionStatus;
import com.example.tictactoe.session.service.SessionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionServiceImpl implements SessionService {
    private final EngineClient engineClient;
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public SessionServiceImpl(EngineClient engineClient) {
        this.engineClient = engineClient;
    }

    @Override
    public Session createSession() {
        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(sessionId, sessionId);
        session.setStatus(SessionStatus.CREATED);
        sessions.put(sessionId, session);

        try {
            EngineGameResponse game = engineClient.getGame(sessionId);
            if (game != null) {
                updateFromEngine(session, game);
            }
        } catch (Exception ex) {
            failSession(session, "Failed to initialize session: " + ex.getMessage());
        }

        return session;
    }

    @Override
    public Session getSession(String sessionId) {
        Session session = sessions.get(sessionId);
        if (session == null) {
            return null;
        }

        try {
            EngineGameResponse game = engineClient.getGame(session.getGameId());
            if (game != null) {
                updateFromEngine(session, game);
            }
        } catch (Exception ex) {
            failSession(session, "Failed to fetch game state: " + ex.getMessage());
        }

        return session;
    }

    @Override
    public Session simulateSession(String sessionId) {
        Session session = sessions.get(sessionId);
        if (session == null) {
            return null;
        }

        session.setStatus(SessionStatus.IN_PROGRESS);

        while (true) {
            EngineGameResponse current;
            try {
                current = engineClient.getGame(session.getGameId());
            } catch (Exception ex) {
                failSession(session, "Failed to fetch game state: " + ex.getMessage());
                break;
            }
            if (current == null) {
                failSession(session, "Engine returned empty game state");
                break;
            }
            updateFromEngine(session, current);

            if (!TicToeConstants.STATUS_IN_PROGRESS.equalsIgnoreCase(current.getStatus())) {
                session.setStatus(SessionStatus.COMPLETED);
                break;
            }

            int[] move = findNextMove(current.getBoard());
            if (move == null) {
                session.setStatus(SessionStatus.COMPLETED);
                break;
            }

            String currentPlayer = current.getCurrentPlayer();
            EngineMoveRequest moveRequest = new EngineMoveRequest(currentPlayer, move[0], move[1]);
            EngineGameResponse moveResult;
            try {
                moveResult = engineClient.makeMove(session.getGameId(), moveRequest);
            } catch (Exception ex) {
                failSession(session, "Failed to make move: " + ex.getMessage());
                break;
            }
            if (moveResult == null) {
                failSession(session, "Engine returned empty move response");
                break;
            }
            updateFromEngine(session, moveResult);
            session.getMoves().add(new MoveRecord(currentPlayer, move[0], move[1]));

            if (!TicToeConstants.STATUS_IN_PROGRESS.equalsIgnoreCase(moveResult.getStatus())) {
                session.setStatus(SessionStatus.COMPLETED);
                break;
            }
        }

        session.touch();
        return session;
    }

    @Override
    public EngineGameResponse getEngineGame(String sessionId) {
        Session session = sessions.get(sessionId);
        if (session == null) {
            return null;
        }
        try {
            return engineClient.getGame(session.getGameId());
        } catch (Exception ex) {
            failSession(session, "Failed to fetch game state: " + ex.getMessage());
            return null;
        }
    }

    private void updateFromEngine(Session session, EngineGameResponse game) {
        session.setGameId(game.getGameId());
        session.setBoard(game.getBoard());
        session.setGameStatus(game.getStatus());
        session.touch();
    }

    private void failSession(Session session, String message) {
        session.setStatus(SessionStatus.FAILED);
        session.setError(new ErrorResponse(TicToeConstants.ERROR_SESSION, message, null));
        session.touch();
    }

    private int[] findNextMove(char[][] board) {
        List<int[]> available = new ArrayList<>();
        if (board == null) {
            return null;
        }
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col] == ' ') {
                    available.add(new int[]{row, col});
                }
            }
        }
        if (available.isEmpty()) {
            return null;
        }
        return available.get(0);
    }
}
