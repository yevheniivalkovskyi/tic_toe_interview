package com.example.tictactoe.core.model;

import com.example.tictactoe.core.util.TicToeConstants;

/**
 * Domain model representing a Tic Tac Toe game.
 */
public class Game {
    private String gameId;
    private char[][] board;
    private GameStatus status;
    private char currentPlayer;
    private int moveCount;

    public enum GameStatus {
        IN_PROGRESS,
        X_WINS,
        O_WINS,
        DRAW
    }

    public Game() {
        this.board = new char[3][3];
        initializeBoard();
        this.status = GameStatus.IN_PROGRESS;
        this.currentPlayer = TicToeConstants.PLAYER_X;
        this.moveCount = 0;
    }

    public Game(String gameId) {
        this();
        this.gameId = gameId;
    }

    private void initializeBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
            }
        }
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

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public char getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(char currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public void setMoveCount(int moveCount) {
        this.moveCount = moveCount;
    }

    public void makeMove(int row, int col, char player) {
        board[row][col] = player;
        moveCount++;
        togglePlayer();
    }

    private void togglePlayer() {
        currentPlayer = (currentPlayer == TicToeConstants.PLAYER_X)
                ? TicToeConstants.PLAYER_O
                : TicToeConstants.PLAYER_X;
    }

    public boolean isCellEmpty(int row, int col) {
        return board[row][col] == ' ';
    }

    public boolean isBoardFull() {
        return moveCount >= 9;
    }

    @Override
    public String toString() {
        return "Game{" +
                "gameId='" + gameId + '\'' +
                ", status=" + status +
                ", currentPlayer=" + currentPlayer +
                ", moveCount=" + moveCount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return gameId != null && gameId.equals(game.gameId);
    }

    @Override
    public int hashCode() {
        return gameId != null ? gameId.hashCode() : 0;
    }
}
