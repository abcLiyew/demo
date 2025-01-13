package com.esdllm;

import com.esdllm.napcatbot.BilibiliPushPlugin;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotContainer;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static java.lang.Thread.sleep;

@Service
public class MyAsyncService {
    @Resource
    private BilibiliPushPlugin bilibiliPushPlugin;
    @Resource
    private BotContainer botContainer;
    @Async
    public void asyncMethod() throws IOException, InterruptedException {
        // 在这里编写你的异步方法逻辑
        sleep(1000);
        Bot bot = botContainer.robots.get(123456);
        bilibiliPushPlugin.onTimer(bot);
    }
}