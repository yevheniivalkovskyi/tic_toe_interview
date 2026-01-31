package com.example.tictactoe.engine.api.dto;

import com.example.tictactoe.core.model.Game;

/**
 * DTO for game response.
 */
public class GameResponse {
    private String gameId;
    private char[][] board;
    private String status;
    private String currentPlayer;
    private String message;

    public GameResponse() {
    }

    public GameResponse(Game game) {
        this.gameId = game.getGameId();
        this.board = game.getBoard();
        this.status = game.getStatus().name();
        this.currentPlayer = String.valueOf(game.getCurrentPlayer());
    }

    public GameResponse(Game game, String message) {
        this(game);
        this.message = message;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public char[][] getBoard() {
        return board;
    }

    public void setBoard(char[][] board) {
        this.board = board;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
