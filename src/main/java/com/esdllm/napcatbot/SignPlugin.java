package com.esdllm.napcatbot;
 
import com.esdllm.common.SignLevel;
import com.esdllm.napcatbot.pojo.SignInRecordsResp;
import com.esdllm.napcatbot.sign.Sign;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class SignPlugin extends BotPlugin {
    @Resource
    private Sign sign;

    @Override
    public int onAnyMessage(Bot bot, AnyMessageEvent event) {
        SignLevel signLevel = getSignLevel(sign.getEmpirical(event.getUserId(), event.getGroupId()));
        if ("查询".equals(event.getMessage())) {
            // 构建消息
            String sendMsg = MsgUtils.builder().at(event.getUserId())
                    .text(" 当前好感度：" + sign.getEmpirical(event.getUserId(), event.getGroupId())+"\n" +signLevel+"\n"+
                            "距离下一等级："+getUpgrade(sign.getEmpirical(event.getUserId(), event.getGroupId())))
                    .build();

            // 发送消息
            bot.sendMsg( event, sendMsg,false);
        }
        if ("签到".equals(event.getMessage())) {
            SignInRecordsResp signStatus = sign.onAnySignIn(event);
            String sendMsg=MsgUtils.builder()
                .text(" 签到失败！")
                .build();
            if (signStatus != null && signStatus.getRetCode()==0) {
                // 构建消息
                sendMsg = MsgUtils.builder()
                       .at(event.getUserId())
                       .text(" 签到成功\n好感度+"+signStatus.getAddEmpirical()+
                               "\n当前好感度：" + sign.getEmpirical(event.getUserId(), event.getGroupId())+"\n" +signLevel+"\n"+
                               "距离下一等级："+getUpgrade(sign.getEmpirical(event.getUserId(), event.getGroupId())))
                       .build();
            } else {
                // 构建消息
                if (signStatus != null) {
                    sendMsg = MsgUtils.builder()
                          .at(event.getUserId())
                          .text(signStatus.getDesc())
                          .build();
                }
            }
            // 发送消息
            bot.sendMsg(event, sendMsg, false);
        }
        // 返回 MESSAGE_IGNORE 执行 plugin-list 下一个插件，返回 MESSAGE_BLOCK 则不执行下一个插件
        return MESSAGE_IGNORE;
    }

    private SignLevel getSignLevel(Double empirical) {
        if (empirical<=SignLevel.ZERO.getEmpirical()){
            return SignLevel.ZERO;
        }else if (empirical<=SignLevel.ONE.getEmpirical()){
            return SignLevel.ONE;
        }else if (empirical<=SignLevel.TWO.getEmpirical()){
            return SignLevel.TWO;
        }else if (empirical<=SignLevel.THREE.getEmpirical()){
            return SignLevel.THREE;
        }else if (empirical<=SignLevel.FOUR.getEmpirical()){
            return SignLevel.FOUR;
        }else if (empirical<=SignLevel.FIVE.getEmpirical()){
            return SignLevel.FIVE;
        }else if (empirical<=SignLevel.SIX.getEmpirical()){
            return SignLevel.SIX;
        }else if (empirical<=SignLevel.SEVEN.getEmpirical()){
            return SignLevel.SEVEN;
        }else if (empirical<=SignLevel.EIGHT.getEmpirical()){
            return SignLevel.EIGHT;
        }else if (empirical<=SignLevel.NINE.getEmpirical()){
            return SignLevel.NINE;
        }else {
            SignLevel signLevel = SignLevel.ZERO;
            signLevel.setEmpirical(1000000.0);
            signLevel.setOpinion("超级无敌挚友");;
            signLevel.setAttitude("无话不谈");
            return signLevel;
        }
    }
    private Double getUpgrade(Double empirical) {
        SignLevel signLevel = getSignLevel(empirical);
        BigDecimal two = new BigDecimal(signLevel.getEmpirical()-empirical);
        return two.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}