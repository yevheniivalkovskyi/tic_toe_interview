package com.example.tictactoe.engine.api.sse;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class GameSsePublisher {
    private final Map<String, List<SseEmitter>> emittersByGame = new ConcurrentHashMap<>();

    public SseEmitter register(String gameId) {
        SseEmitter emitter = new SseEmitter(0L);
        emittersByGame.computeIfAbsent(gameId, key -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> remove(gameId, emitter));
        emitter.onTimeout(() -> remove(gameId, emitter));
        emitter.onError(error -> remove(gameId, emitter));

        return emitter;
    }

    public void publish(String gameId, Object payload) {
        List<SseEmitter> emitters = emittersByGame.get(gameId);
        if (emitters == null || emitters.isEmpty()) {
            return;
        }

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(payload);
            } catch (IOException ex) {
                remove(gameId, emitter);
            }
        }
    }

    private void remove(String gameId, SseEmitter emitter) {
        List<SseEmitter> emitters = emittersByGame.get(gameId);
        if (emitters == null) {
            return;
        }
        emitters.remove(emitter);
        if (emitters.isEmpty()) {
            emittersByGame.remove(gameId);
        }
    }
}
