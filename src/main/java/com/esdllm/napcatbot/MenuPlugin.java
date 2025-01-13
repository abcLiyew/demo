package com.esdllm.napcatbot;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import org.springframework.stereotype.Component;

@Component
public class MenuPlugin extends BotPlugin {
    @Override
    public int onAnyMessage(Bot bot, AnyMessageEvent event) {
        String msg = event.getMessage();
        if (isMenu(msg)) {
            bot.sendMsg(event, "功能菜单：\n" +
                    "签到：每日签到\n" +
                    "查询：查询经验值\n" +
                    "功能菜单：查询功能菜单\n"+
                    "添加订阅：后接房间号添加哔哩哔哩直播间开播提醒\n" +
                    "取消订阅：后接房间号取消哔哩哔哩直播间提醒\n", false);
        }
        return MESSAGE_IGNORE;
    }
    private static boolean isMenu(String msg) {
        return "菜单".equals(msg) || "功能".equals(msg) || "功能菜单".equals(msg) || "功能列表".equals(msg) || "菜单列表".equals(msg);
    };
}
