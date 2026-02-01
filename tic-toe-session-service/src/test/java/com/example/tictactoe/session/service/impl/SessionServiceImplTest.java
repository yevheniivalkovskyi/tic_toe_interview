package com.example.tictactoe.session.service.impl;

import com.example.tictactoe.core.util.TicToeConstants;
import com.example.tictactoe.session.client.EngineClient;
import com.example.tictactoe.session.client.dto.EngineGameResponse;
import com.example.tictactoe.session.client.dto.EngineMoveRequest;
import com.example.tictactoe.session.model.Session;
import com.example.tictactoe.session.model.SessionStatus;
import com.example.tictactoe.session.ws.SessionUpdatePublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionServiceImplTest {

    @Mock
    private EngineClient engineClient;

    @Mock
    private SessionUpdatePublisher updatePublisher;

    private SessionServiceImpl sessionService;

    @BeforeEach
    void setUp() {
        sessionService = new SessionServiceImpl(engineClient, updatePublisher);
    }

    @Test
    void simulateSession_completesAndRecordsMoves() {
        EngineGameResponse initial = gameResponse("game-1", emptyBoard(),
                TicToeConstants.STATUS_IN_PROGRESS, String.valueOf(TicToeConstants.PLAYER_X));
        EngineGameResponse afterMove = gameResponse("game-1", boardWithMove(0, 0, TicToeConstants.PLAYER_X),
                TicToeConstants.STATUS_X_WINS, String.valueOf(TicToeConstants.PLAYER_O));

        when(engineClient.getGame(anyString())).thenReturn(initial, initial);
        when(engineClient.makeMove(anyString(), any(EngineMoveRequest.class))).thenReturn(afterMove);

        Session session = sessionService.createSession();
        Session simulated = sessionService.simulateSession(session.getSessionId());

        assertNotNull(simulated);
        assertEquals(SessionStatus.COMPLETED, simulated.getStatus());
        assertEquals(TicToeConstants.STATUS_X_WINS, simulated.getGameStatus());
        assertEquals(1, simulated.getMoves().size());
        assertNull(simulated.getError());

        verify(engineClient, times(2)).getGame(anyString());
        verify(engineClient, times(1)).makeMove(anyString(), any(EngineMoveRequest.class));
    }

    @Test
    void createSession_marksFailedOnEngineError() {
        when(engineClient.getGame(anyString())).thenThrow(new RuntimeException("engine down"));

        Session session = sessionService.createSession();

        assertNotNull(session);
        assertEquals(SessionStatus.FAILED, session.getStatus());
        assertNotNull(session.getError());
    }

    private EngineGameResponse gameResponse(String gameId, char[][] board, String status, String currentPlayer) {
        EngineGameResponse response = new EngineGameResponse();
        response.setGameId(gameId);
        response.setBoard(board);
        response.setStatus(status);
        response.setCurrentPlayer(currentPlayer);
        return response;
    }

    private char[][] emptyBoard() {
        char[][] board = new char[3][3];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                board[row][col] = ' ';
            }
        }
        return board;
    }

    private char[][] boardWithMove(int row, int col, char player) {
        char[][] board = emptyBoard();
        board[row][col] = player;
        return board;
    }
}
