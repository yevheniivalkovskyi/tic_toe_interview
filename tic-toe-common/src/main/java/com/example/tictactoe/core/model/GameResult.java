package com.example.tictactoe.core.model;

/**
 * Result of a game move operation.
 */
public class GameResult {
    private Game game;
    private boolean valid;
    private String message;

    public GameResult(Game game, boolean valid, String message) {
        this.game = game;
        this.valid = valid;
        this.message = message;
    }

    public static GameResult success(Game game) {
        return new GameResult(game, true, "Move successful");
    }

    public static GameResult failure(Game game, String message) {
        return new GameResult(game, false, message);
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
