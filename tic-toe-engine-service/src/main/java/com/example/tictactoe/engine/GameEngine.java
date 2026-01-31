package com.example.tictactoe.engine;

import com.example.tictactoe.core.model.Game;
import com.example.tictactoe.core.util.TicToeConstants;
import org.springframework.stereotype.Service;

/**
 * Game Engine Service - Pure game logic and rules.
 * This service contains all game-related business logic without state management.
 */
@Service
public class GameEngine {

    /**
     * Validates if a move is valid according to game rules.
     *
     * @param game the game state
     * @param row the row position (0-2)
     * @param col the column position (0-2)
     * @param player the player symbol (X or O)
     * @return validation result message, null if valid
     */
    public String validateMove(Game game, int row, int col, char player) {
        // Validate game status
        if (game.getStatus() != Game.GameStatus.IN_PROGRESS) {
            return "Game is already finished. Status: " + game.getStatus();
        }

        // Validate player symbol
        if (player != TicToeConstants.PLAYER_X && player != TicToeConstants.PLAYER_O) {
            return "Invalid player symbol. Must be '" + TicToeConstants.PLAYER_X +
                    "' or '" + TicToeConstants.PLAYER_O + "'";
        }

        // Validate it's the player's turn
        if (game.getCurrentPlayer() != player) {
            return "It's not " + player + "'s turn. Current player: " + game.getCurrentPlayer();
        }

        // Validate position bounds
        if (row < 0 || row > 2 || col < 0 || col > 2) {
            return "Invalid position. Row and column must be between 0 and 2";
        }

        // Validate cell is empty
        if (!game.isCellEmpty(row, col)) {
            return "Cell at position (" + row + ", " + col + ") is already occupied";
        }

        return null; // Move is valid
    }

    /**
     * Applies a move to the game board and updates game state.
     *
     * @param game the game state
     * @param row the row position (0-2)
     * @param col the column position (0-2)
     * @param player the player symbol (X or O)
     */
    public void applyMove(Game game, int row, int col, char player) {
        game.makeMove(row, col, player);
    }

    /**
     * Checks if a player has won the game after a move.
     *
     * @param board the game board
     * @param player the player to check
     * @return the win status if player won, null otherwise
     */
    public Game.GameStatus checkWinCondition(char[][] board, char player) {
        // Check rows
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) {
                return player == TicToeConstants.PLAYER_X
                        ? Game.GameStatus.X_WINS
                        : Game.GameStatus.O_WINS;
            }
        }

        // Check columns
        for (int j = 0; j < 3; j++) {
            if (board[0][j] == player && board[1][j] == player && board[2][j] == player) {
                return player == TicToeConstants.PLAYER_X
                        ? Game.GameStatus.X_WINS
                        : Game.GameStatus.O_WINS;
            }
        }

        // Check main diagonal
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            return player == TicToeConstants.PLAYER_X
                    ? Game.GameStatus.X_WINS
                    : Game.GameStatus.O_WINS;
        }

        // Check anti-diagonal
        if (board[0][2] == player && board[1][1] == player && board[2][0] == player) {
            return player == TicToeConstants.PLAYER_X
                    ? Game.GameStatus.X_WINS
                    : Game.GameStatus.O_WINS;
        }

        return null;
    }

    /**
     * Determines the game status after a move.
     * Checks for win condition first, then draw condition.
     *
     * @param game the game state
     * @param player the player who made the move
     * @return the updated game status
     */
    public Game.GameStatus determineGameStatus(Game game, char player) {
        // Check for win condition
        Game.GameStatus winStatus = checkWinCondition(game.getBoard(), player);
        if (winStatus != null) {
            return winStatus;
        }

        // Check for draw
        if (game.isBoardFull()) {
            return Game.GameStatus.DRAW;
        }

        // Game continues
        return Game.GameStatus.IN_PROGRESS;
    }
}
