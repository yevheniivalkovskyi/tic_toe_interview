package com.example.tictactoe.engine.session;

import com.example.tictactoe.core.model.Game;
import com.example.tictactoe.core.model.GameResult;

/**
 * Service interface for game session management.
 * Handles game state persistence and session lifecycle.
 */
public interface GameSessionService {
    /**
     * Create a new game session.
     *
     * @param gameId the game identifier
     * @return the created game
     */
    Game createGameSession(String gameId);

    /**
     * Get a game session by its ID.
     *
     * @param gameId the game identifier
     * @return the game, or null if not found
     */
    Game getGameSession(String gameId);

    /**
     * Save or update a game session.
     *
     * @param game the game to save
     * @return the saved game
     */
    Game saveGameSession(Game game);

    /**
     * Process a move in the game session.
     * This method coordinates between the game engine and session management.
     *
     * @param gameId the game identifier
     * @param row the row position (0-2)
     * @param col the column position (0-2)
     * @param player the player symbol (X or O)
     * @return the result of the move operation
     */
    GameResult processMove(String gameId, int row, int col, char player);
}
