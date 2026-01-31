package com.example.tictactoe.session.api;

import com.example.tictactoe.session.api.dto.SessionResponse;
import com.example.tictactoe.session.client.dto.EngineGameResponse;
import com.example.tictactoe.session.model.Session;
import com.example.tictactoe.session.service.SessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    public ResponseEntity<SessionResponse> createSession() {
        Session session = sessionService.createSession();
        return ResponseEntity.status(HttpStatus.CREATED).body(new SessionResponse(session));
    }

    @PostMapping("/{sessionId}/simulate")
    public ResponseEntity<SessionResponse> simulateSession(@PathVariable String sessionId) {
        Session session = sessionService.simulateSession(sessionId);
        if (session == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(new SessionResponse(session));
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<SessionResponse> getSession(@PathVariable String sessionId) {
        Session session = sessionService.getSession(sessionId);
        if (session == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(new SessionResponse(session));
    }

    /**
     * Validate communication with engine service.
     * GET /sessions/{sessionId}/game
     */
    @GetMapping("/{sessionId}/game")
    public ResponseEntity<EngineGameResponse> getEngineGame(@PathVariable String sessionId) {
        EngineGameResponse game = sessionService.getEngineGame(sessionId);
        if (game == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(game);
    }
}
