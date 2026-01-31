package com.example.tictactoe.engine.client;

import com.example.tictactoe.engine.client.dto.SessionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "tic-toe-session", url = "${session.base-url:http://localhost:8081}")
public interface SessionClient {

    @GetMapping("/sessions/{sessionId}")
    SessionResponse getSession(@PathVariable("sessionId") String sessionId);
}
