package com.example.tictactoe.engine.session.impl;

import com.example.tictactoe.core.model.Game;
import com.example.tictactoe.core.model.GameResult;
import com.example.tictactoe.engine.GameEngine;
import com.example.tictactoe.engine.session.GameSessionService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of GameSessionService using in-memory storage.
 * Manages game sessions and coordinates with the game engine.
 */
@Service
public class GameSessionServiceImpl implements GameSessionService {

    private final GameEngine gameEngine;
    private final Map<String, Game> gameSessions = new ConcurrentHashMap<>();

    public GameSessionServiceImpl(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    @Override
    public Game createGameSession(String gameId) {
        Game game = new Game(gameId);
        gameSessions.put(gameId, game);
        return game;
    }

    @Override
    public Game getGameSession(String gameId) {
        return gameSessions.get(gameId);
    }

    @Override
    public Game saveGameSession(Game game) {
        gameSessions.put(game.getGameId(), game);
        return game;
    }

    @Override
    public GameResult processMove(String gameId, int row, int col, char player) {
        // Get or create game session
        Game game = gameSessions.get(gameId);
        if (game == null) {
            game = createGameSession(gameId);
        }

        // Use game engine to validate the move
        String validationError = gameEngine.validateMove(game, row, col, player);
        if (validationError != null) {
            return GameResult.failure(game, validationError);
        }

        // Use game engine to apply the move
        gameEngine.applyMove(game, row, col, player);

        // Use game engine to determine game status
        Game.GameStatus newStatus = gameEngine.determineGameStatus(game, player);
        game.setStatus(newStatus);

        // Save the updated game session
        saveGameSession(game);

        return GameResult.success(game);
    }
}
