package com.example.tictactoe.session.api.dto;

import com.example.tictactoe.core.dto.ErrorResponse;
import com.example.tictactoe.session.model.MoveRecord;
import com.example.tictactoe.session.model.Session;
import com.example.tictactoe.session.model.SessionStatus;

import java.time.Instant;
import java.util.List;

public class SessionResponse {
    private String sessionId;
    private String gameId;
    private SessionStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private char[][] board;
    private String gameStatus;
    private ErrorResponse error;
    private List<MoveRecord> moves;

    public SessionResponse() {
    }

    public SessionResponse(Session session) {
        this.sessionId = session.getSessionId();
        this.gameId = session.getGameId();
        this.status = session.getStatus();
        this.createdAt = session.getCreatedAt();
        this.updatedAt = session.getUpdatedAt();
        this.board = session.getBoard();
        this.gameStatus = session.getGameStatus();
        this.error = session.getError();
        this.moves = session.getMoves();
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public char[][] getBoard() {
        return board;
    }

    public void setBoard(char[][] board) {
        this.board = board;
    }

    public String getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }

    public ErrorResponse getError() {
        return error;
    }

    public void setError(ErrorResponse error) {
        this.error = error;
    }

    public List<MoveRecord> getMoves() {
        return moves;
    }

    public void setMoves(List<MoveRecord> moves) {
        this.moves = moves;
    }
}
