package com.example.tictactoe.session;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.example.tictactoe")
@EnableFeignClients(basePackages = "com.example.tictactoe.session.client")
public class TicToeSessionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicToeSessionServiceApplication.class, args);
    }

}
