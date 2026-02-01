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
import com.example.tictactoe.session.ws.SessionUpdatePublisher;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

@Service
public class SessionServiceImpl implements SessionService {
    private final EngineClient engineClient;
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private final Map<String, Semaphore> simulationLocks = new ConcurrentHashMap<>();
    private final SessionUpdatePublisher updatePublisher;

    public SessionServiceImpl(EngineClient engineClient, SessionUpdatePublisher updatePublisher) {
        this.engineClient = engineClient;
        this.updatePublisher = updatePublisher;
    }

    @Override
    public Session createSession() {
        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(sessionId, sessionId);
        session.setStatus(SessionStatus.CREATED);
        sessions.put(sessionId, session);

        try {
            EngineGameResponse game = fetchGame(sessionId);
            if (game != null) {
                updateFromEngine(session, game);
            }
        } catch (Exception ex) {
            failSession(session, "Failed to initialize session: " + ex.getMessage());
        }

        updatePublisher.publish(session);
        return session;
    }

    @Override
    public Session getSession(String sessionId) {
        Session session = sessions.get(sessionId);
        if (session == null) {
            return null;
        }

        try {
            EngineGameResponse game = fetchGame(session.getGameId());
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

        Semaphore lock = simulationLocks.computeIfAbsent(sessionId, key -> new Semaphore(1));
        if (!lock.tryAcquire()) {
            return session;
        }

        try {
            session.setStatus(SessionStatus.IN_PROGRESS);
            updatePublisher.publish(session);

            while (true) {
                EngineGameResponse current;
                try {
                    current = fetchGame(session.getGameId());
                } catch (Exception ex) {
                    failSession(session, "Failed to fetch game state: " + ex.getMessage());
                    break;
                }
                if (current == null) {
                    failSession(session, "Engine returned empty game state");
                    break;
                }
                updateFromEngine(session, current);
                updatePublisher.publish(session);

                if (!TicToeConstants.STATUS_IN_PROGRESS.equalsIgnoreCase(current.getStatus())) {
                    session.setStatus(SessionStatus.COMPLETED);
                    updatePublisher.publish(session);
                    break;
                }

                int[] move = findNextMove(current.getBoard());
                if (move == null) {
                    session.setStatus(SessionStatus.COMPLETED);
                    updatePublisher.publish(session);
                    break;
                }

                String currentPlayer = current.getCurrentPlayer();
                EngineMoveRequest moveRequest = new EngineMoveRequest(currentPlayer, move[0], move[1]);
                EngineGameResponse moveResult;
                try {
                    moveResult = makeMove(session.getGameId(), moveRequest);
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
                updatePublisher.publish(session);

                if (!TicToeConstants.STATUS_IN_PROGRESS.equalsIgnoreCase(moveResult.getStatus())) {
                    session.setStatus(SessionStatus.COMPLETED);
                    updatePublisher.publish(session);
                    break;
                }

            }
        } finally {
            lock.release();
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
        updatePublisher.publish(session);
    }

    @CircuitBreaker(name = "engineService", fallbackMethod = "fetchGameFallback")
    private EngineGameResponse fetchGame(String gameId) {
        return engineClient.getGame(gameId);
    }

    @CircuitBreaker(name = "engineService", fallbackMethod = "makeMoveFallback")
    private EngineGameResponse makeMove(String gameId, EngineMoveRequest request) {
        return engineClient.makeMove(gameId, request);
    }

    @SuppressWarnings("unused")
    private EngineGameResponse fetchGameFallback(String gameId, Throwable throwable) {
        return null;
    }

    @SuppressWarnings("unused")
    private EngineGameResponse makeMoveFallback(String gameId, EngineMoveRequest request, Throwable throwable) {
        return null;
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
        Collections.shuffle(available);
        return available.get(0);
    }
}
