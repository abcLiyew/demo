package com.esdllm;

import com.esdllm.napcatbot.BilibiliPushPlugin;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotContainer;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Thread.sleep;

@Service
public class MyAsyncService {

    @Value("${myConfig.bot.qq}")
    private  Long qq;
    @Resource
    private BilibiliPushPlugin bilibiliPushPlugin;
    @Resource
    private BotContainer botContainer;
    @Async
    public void asyncMethod() throws IOException, InterruptedException {
        // 在这里编写你的异步方法逻辑
        sleep(1000);
        Bot bot = botContainer.robots.get(qq);
        bilibiliPushPlugin.onTimer(bot);
    }
    /*@Scheduled(cron = "55 59 23 * * *")
    public void groupSign(){
        // 在这里编写你的定时任务逻辑
        while (true) {
            Bot bot = botContainer.robots.get(1825330295L);
            bot.sendGroupSign(985903541L);
            bot.sendGroupSign(679079419L);
            Date newDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("ss");
            String time = sdf.format(newDate);
            if (time.equals("01")){
                break;
            }
        }
    }*/
}