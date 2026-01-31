package com.example.tictactoe.session.client;

import com.example.tictactoe.session.client.dto.EngineGameResponse;
import com.example.tictactoe.session.client.dto.EngineMoveRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "tic-toe-engine", url = "${engine.base-url:http://localhost:8080}")
public interface EngineClient {

    @GetMapping("/games/{gameId}")
    EngineGameResponse getGame(@PathVariable("gameId") String gameId);

    @PostMapping("/games/{gameId}/move")
    EngineGameResponse makeMove(@PathVariable("gameId") String gameId,
                                @RequestBody EngineMoveRequest request);
}
