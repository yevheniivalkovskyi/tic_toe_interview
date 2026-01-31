package com.example.tictactoe.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class TicToeEurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicToeEurekaServerApplication.class, args);
    }
}
