package com.esdllm.napcatbot;

import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.enums.AtEnum;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class MenuPlugin extends BotPlugin {
    private static final Set<String> MENU_COMMANDS = Set.of(
            "菜单", "功能", "功能菜单", "菜单列表", "功能列表", "help"
    );
    @Override
    @MessageHandlerFilter(at = AtEnum.BOTH)
    public int onAnyMessage(Bot bot, AnyMessageEvent event) {
        String msg = event.getMessage();
        msg = msg.replaceAll("\\[CQ:[^]]*]","");
        msg = msg.trim();
        if (isMenu(msg)) {
            bot.sendMsg(event, """
                    功能菜单：
                    
                    每日签到：
                    签到：签到领取经验值
                    查询：查询签到情况
                    今日运势：获取每日运势
                    今日老婆：获取每日群友老婆(因为我发现了问题所在但是不知道怎么解决，暂时关掉)
                    
                    菜单：
                    功能|功能菜单|菜单|菜单列表|功能列表|help：查询功能菜单
                    
                    直播订阅：
                    添加订阅：添加开播提醒
                    取消订阅：取消开播提醒
                    开播@全体成员：开播时@全体成员(如果机器人不是管理员请勿设置@全体成员，否则会出问题)
                    取消开播@全体成员：取消开播时@全体成员
                    (以上只有管理员可以使用)
                    开播@我:开播时@你
                    取消开播@我：取消开播时@你
                    
                    AI聊天：
                    @机器人并结尾加/或以ai:开头：与机器人聊天
                    
                    b站链接解析：
                    解析并发送视频标题、封面、UP主、播放量、简介等信息\
                    (仅支持视频，直播，动态且第一个链接是b站链接和b站小程序链接)
                    
                    如果发现机器人有什么bug或者想要增加什么新功能请跟[CQ:at,qq=1825330295] 说
                    """, false);
        }
        return MESSAGE_IGNORE;
    }
    private static boolean isMenu(String msg) {
        return MENU_COMMANDS.contains(msg);
    }
}
