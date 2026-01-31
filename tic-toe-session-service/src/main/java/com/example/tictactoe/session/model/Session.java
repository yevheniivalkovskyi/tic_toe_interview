package com.example.tictactoe.session.model;

import com.example.tictactoe.core.dto.ErrorResponse;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Session {
    private String sessionId;
    private String gameId;
    private SessionStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private char[][] board;
    private String gameStatus;
    private ErrorResponse error;
    private List<MoveRecord> moves;

    public Session() {
        this.moves = new ArrayList<>();
        this.status = SessionStatus.CREATED;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public Session(String sessionId, String gameId) {
        this();
        this.sessionId = sessionId;
        this.gameId = gameId;
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

    public void touch() {
        this.updatedAt = Instant.now();
    }
}
