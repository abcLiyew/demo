package com.esdllm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.IOException;

@SpringBootApplication
@EnableAsync
@MapperScan("com.esdllm.napcatbot.mapper")
public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        ConfigurableApplicationContext context  = SpringApplication.run(Main.class, args);
        MyAsyncService myAsyncService = context.getBean(MyAsyncService.class);
        myAsyncService.asyncMethod();
    }
}