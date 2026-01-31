package com.example.tictactoe.engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.example.tictactoe")
@EnableFeignClients(basePackages = "com.example.tictactoe.engine.client")
public class TicToeEngineServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicToeEngineServiceApplication.class, args);
    }
}
