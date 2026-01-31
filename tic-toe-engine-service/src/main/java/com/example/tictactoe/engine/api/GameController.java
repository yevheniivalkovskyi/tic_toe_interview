package com.example.tictactoe.engine.api;

import com.example.tictactoe.core.model.Game;
import com.example.tictactoe.core.model.GameResult;
import com.example.tictactoe.engine.api.dto.GameResponse;
import com.example.tictactoe.engine.api.dto.MoveRequest;
import com.example.tictactoe.engine.api.sse.GameSsePublisher;
import com.example.tictactoe.engine.client.SessionClient;
import com.example.tictactoe.engine.client.dto.SessionResponse;
import feign.FeignException;
import com.example.tictactoe.engine.session.GameSessionService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * REST controller for game operations.
 * Uses GameSessionService to manage game sessions.
 */
@RestController
@RequestMapping("/games")
public class GameController {

    private final GameSessionService gameSessionService;
    private final SessionClient sessionClient;
    private final GameSsePublisher gameSsePublisher;

    public GameController(GameSessionService gameSessionService,
                          SessionClient sessionClient,
                          GameSsePublisher gameSsePublisher) {
        this.gameSessionService = gameSessionService;
        this.sessionClient = sessionClient;
        this.gameSsePublisher = gameSsePublisher;
    }

    /**
     * Make a move in the game.
     * POST /games/{gameId}/move
     */
    @PostMapping("/{gameId}/move")
    public ResponseEntity<GameResponse> makeMove(
            @PathVariable String gameId,
            @Valid @RequestBody MoveRequest moveRequest) {

        char playerSymbol = moveRequest.getPlayerSymbol();
        int row = moveRequest.getRow();
        int col = moveRequest.getColumn();

        GameResult result = gameSessionService.processMove(gameId, row, col, playerSymbol);

        if (!result.isValid()) {
            GameResponse response = new GameResponse(result.getGame(), result.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        GameResponse response = new GameResponse(result.getGame(), result.getMessage());
        gameSsePublisher.publish(gameId, response);
        return ResponseEntity.ok(response);
    }

    /**
     * Get the current state of the game.
     * GET /games/{gameId}
     */
    @GetMapping("/{gameId}")
    public ResponseEntity<GameResponse> getGame(@PathVariable String gameId) {
        Game game = gameSessionService.getGameSession(gameId);

        if (game == null) {
            // Auto-create game session if it doesn't exist
            game = gameSessionService.createGameSession(gameId);
        }

        GameResponse response = new GameResponse(game);
        return ResponseEntity.ok(response);
    }

    /**
     * Stream game updates via SSE.
     * GET /games/{gameId}/stream
     */
    @GetMapping(value = "/{gameId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamGame(@PathVariable String gameId) {
        return gameSsePublisher.register(gameId);
    }

    /**
     * Get session details for a game.
     * GET /games/{gameId}/session
     */
    @GetMapping("/{gameId}/session")
    public ResponseEntity<SessionResponse> getSession(@PathVariable String gameId) {
        try {
            SessionResponse session = sessionClient.getSession(gameId);
            if (session == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(session);
        } catch (FeignException.NotFound ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (FeignException ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }
}
