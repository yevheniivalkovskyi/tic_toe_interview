package com.example.tictactoe.engine.client.dto;

import java.util.List;

public class SessionResponse {
    private String sessionId;
    private String gameId;
    private String status;
    private char[][] board;
    private String gameStatus;
    private List<MoveRecord> moves;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public List<MoveRecord> getMoves() {
        return moves;
    }

    public void setMoves(List<MoveRecord> moves) {
        this.moves = moves;
    }
}
