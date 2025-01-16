package com.esdllm.napcatbot;
 
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.esdllm.common.SignLevel;
import com.esdllm.napcatbot.mapper.SignInRecordsMapper;
import com.esdllm.napcatbot.pojo.SignInRecordsResp;
import com.esdllm.napcatbot.pojo.database.SignInRecords;
import com.esdllm.napcatbot.sign.Sign;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

@Component
public class SignPlugin extends BotPlugin {
    @Resource
    private Sign sign;
    @Resource
    private SignInRecordsMapper signInRecordsMapper;

    @Override
    public int onAnyMessage(Bot bot, AnyMessageEvent event) {
        //今日运势
        if ("今日运势".equals(event.getMessage())){
            return todayFortune(bot,event);
        }
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

    private int todayFortune(Bot bot, AnyMessageEvent event) {
        LocalDate today = LocalDate.now();
        LocalDateTime todayMidnight = today.atTime(12,0,59);
        long timeStamp = todayMidnight.toInstant(java.time.ZoneOffset.of("+8")).toEpochMilli();
        LambdaQueryWrapper<SignInRecords> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SignInRecords::getQqUid,event.getUserId());
        if (!Objects.isNull(event.getGroupId())) {
            queryWrapper.eq(SignInRecords::getGroupId,event.getGroupId());
        }else {
            queryWrapper.isNull(SignInRecords::getGroupId);
        }
        SignInRecords signInRecords = signInRecordsMapper.selectOne(queryWrapper);
        if (signInRecords == null){
            signInRecords = new SignInRecords();
            signInRecords.setQqUid(event.getUserId());
            signInRecords.setGroupId(event.getGroupId());

            signInRecordsMapper.insert(signInRecords);
        }
        // 计算随机数
        Random random = new Random(timeStamp);
        int fortune = 0;
        for (int i = 0; i<signInRecords.getSid(); i++){
            if (i == signInRecords.getSid()-1){
                fortune = random.nextInt(100);
            }
            random.nextInt(100);
        }
        String sendMsg = null;
        if (fortune < 20){
            sendMsg = MsgUtils.builder().at(event.getUserId()).text(" 猫猫测试中╰(*°▽°*)╯...您今天的运势为：" +
                    "\n"+ "运势不佳，请继续努力！\n今日运势为："+fortune+"\n点评：大凶！欢迎来极端黑暗的世界！" +
                    "\n").build();

        }else if (fortune < 40){
            sendMsg = MsgUtils.builder().at(event.getUserId()).text(" 猫猫测试中╰(*°▽°*)╯...您今天的运势为：\n"
                    +  "运势一般，请继续努力！\n今日运势为："+fortune+"\n点评：凶带吉！欢迎来黑暗的世界！" +
                    "\n").build();
        }else if (fortune < 60){
            sendMsg = MsgUtils.builder().at(event.getUserId()).text(" 猫猫测试中╰(*°▽°*)╯...您今天的运势为：\n"+
                    "运势良好，请继续努力！\n今日运势为："+fortune+"\n点评：吉带凶！欢迎来曙光的世界！" +
                    "\n").build();
        }else if (fortune < 80){
            sendMsg = MsgUtils.builder().at(event.getUserId()).text(" 猫猫测试中╰(*°▽°*)╯...您今天的运势为：\n"
                    + "运势优秀，可以躺平！\n今日运势为："+fortune+"\n点评：吉！欢迎来光明的世界！" +
                    "\n").build();
        }else {
            sendMsg = MsgUtils.builder().at(event.getUserId()).text(" 猫猫测试中╰(*°▽°*)╯...您今天的运势为：\n"
                    + "运势完美，直接发财！\n今日运势为："+fortune+"\n点评：大吉！欢迎来完美的世界！" +
                    "\n").build();
        }
        bot.sendMsg(event,sendMsg,false);
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