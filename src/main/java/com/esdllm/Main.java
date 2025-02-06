package com.esdllm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@MapperScan("com.esdllm.napcatbot.mapper")
public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        SpringApplication.run(Main.class, args);
    }
}