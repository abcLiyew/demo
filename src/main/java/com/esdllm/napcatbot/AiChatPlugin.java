package com.esdllm.napcatbot;

import com.esdllm.common.ChatMessageUtils;
import com.esdllm.common.ModelEnum;
import com.esdllm.napcatbot.deepseek.AiChatDeepSeekService;
import com.esdllm.napcatbot.pojo.aichat.DeepSeekReq;
import com.esdllm.napcatbot.pojo.aichat.DeepSeekResp;
import com.mikuac.shiro.annotation.AnyMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.common.Order;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.enums.AtEnum;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Shiro
@Component
public class AiChatPlugin {
    // 从获取API密钥
    @Value("${myConfig.bot.aiChat.token}")
    private String token;
    private final String modelId = ModelEnum.DeepSeek_R1_Official.getName();
    private final String standbyModelId = ModelEnum.DeepSeek_V3_Official.getName();
    @Value("${myConfig.bot.aiChat.base_url}")
    private String baseUrl;
    @Value("${myConfig.bot.aiChat.max_tokens}")
    private Integer maxTokens;

    private String reasoningContent = null;

    @Resource
    private AiChatDeepSeekService aiChatDeepSeekService;

    // 初始化消息列表
    private final List<DeepSeekReq.Message> chatMessages = new ArrayList<>();
    private final Object lock = new Object(); // 新增锁对象

    public void clear() {
        synchronized (lock) {
            chatMessages.clear();
        }
    }
    // 初始化系统消息
    @PostConstruct
    public void setSystemMessage() {
        ChatMessageUtils system = ChatMessageUtils.builder().role("system")
                .text("不要使用markdown格式，用户当前都是哔哩哔哩一个女友势虚拟主播的粉丝" +
                        "这里是粉丝群,你是群里的一位群友，聊天过程中尽量不要提起主播，除非有人问" +
                        "不要欢迎，也不用提到她的直播，就正常的聊天互动即可。注意不要喊用户主人");
        chatMessages.add(system.build());
    }

    /**
     * 机器人接收消息并向ai发请求
     * @param  bot 机器人实例
     * @param  event 消息事件
     */
    @AnyMessageHandler
    @MessageHandlerFilter(at = AtEnum.NEED,groups={679079419L})
    public void onReceiveMessage(Bot bot, AnyMessageEvent event) {
        // 过滤消息
        String msg = event.getMessage();
        msg = msg.replaceAll("\\[CQ:[^]]*]","");
        msg = msg.trim();
        if (!msg.toLowerCase().startsWith("ai:")&& !msg.startsWith("ai：")&&!msg.endsWith("/")){
            return;
        }
        if (msg.startsWith("ai")){
            msg = msg.substring(3);
        }
        msg = msg.trim();
        if (msg.endsWith("/")) {
            msg = msg.substring(0, msg.length() - 1);
        }
        // 过滤空消息
        if(msg.isEmpty()){
            return;
        }
        // 构建消息列表
        DeepSeekReq.Message message = ChatMessageUtils.builder()
                .role("user")
                .text(msg)
                .build();
        chatMessages.add(message);
        //处理消息列表，如果有连续两条或以上同一角色消息，则合并为一条消息
        for (int i = chatMessages.size() - 1; i >= 0; i--){
            if(i<2){
                break;
            }
            if (chatMessages.get(i).getRole().equals(chatMessages.get(i-1).getRole())){
                String content = chatMessages.get(i).getContent();
                content += "\n" + chatMessages.get(i-1).getContent();
                chatMessages.get(i).setContent(content);
                chatMessages.remove(i-1);
            }
        }
        if (!chatMessages.get(1).getRole().equals("user")){
            chatMessages.remove(1);
        }
        DeepSeekResp resp;
        String sendMsg = "";
        try {
            resp = aiChatDeepSeekService.getResp(baseUrl, token, modelId, chatMessages, maxTokens);
        }catch (Exception e){
            log.error(e.getMessage());
            try {
                sendMsg = "当前R1不可用，以下为V3的回答：\n";
                resp = aiChatDeepSeekService.getResp(baseUrl, token, standbyModelId, chatMessages, maxTokens);
            } catch (Exception ex) {
                log.error("V3也不可用。{}", ex.getMessage());
                sendMsg = "deepseek-R1和V3的api都寄了，稍后再试试吧";
                bot.sendMsg(event,sendMsg,false);
                return;
            }
        }
        // 处理ai返回的结果

        for (DeepSeekResp.Choices choice : resp.getChoices()) {
            if (choice.getMessage().getRole().equals("assistant")) {
                String text = choice.getMessage().getContent();
                sendMsg += text.replaceAll("[#|*]","");
                sendMsg+="\n\n\n内容由AI生成，可能存在不实信息，请注意甄别";
                DeepSeekReq.Message choiceMessage = choice.getMessage();
                reasoningContent = choiceMessage.getReasoning_content();
                choiceMessage.setReasoning_content(null);
                chatMessages.add(choiceMessage);
                bot.sendMsg(event,sendMsg,false);
            }
        }


    }
    @AnyMessageHandler
    @MessageHandlerFilter(cmd = "(获取思考过程.*)$",at = AtEnum.NEED,senders = 1825330295)
    public void onGetReasoningMessage(Bot bot, AnyMessageEvent event) {
        if (!modelId.equals(ModelEnum.DeepSeek_R1.getName())&& !modelId.equals(ModelEnum.DeepSeek_R1_Official.getName())){
            bot.sendMsg(event,"当前模型不支持获取思考过程。",false);
            return;
        }
        if (chatMessages.size() <3){
            bot.sendMsg(event,"消息列表是空的。",false);
            return;
        }
        if (Objects.isNull(reasoningContent)){
            bot.sendMsg(event,"本次回答的思考过程是空。",false);
        }else {
            bot.sendMsg(event,reasoningContent,false);
        }
    }
    @AnyMessageHandler
    @MessageHandlerFilter(cmd = "(清空消息列表.*)$|(开启新对话.*)$",at = AtEnum.NEED,senders = 1825330295)
    public void onNewChat(Bot bot, AnyMessageEvent event){
        chatMessages.clear();
        reasoningContent = null;
        setSystemMessage();
        bot.sendMsg(event,"已清空消息列表。",false);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void clearChatList(){
        this.clear();
        this.setSystemMessage();
    }
    @AnyMessageHandler
    @MessageHandlerFilter(cmd = "(获取消息列表.*)$",at = AtEnum.NEED,senders = 1825330295)
    public void getMessageList(Bot bot, AnyMessageEvent event){
        StringBuilder sengMsg = new StringBuilder();
        for (DeepSeekReq.Message message : chatMessages) {
            sengMsg.append(message.getRole()).append("：").append(message.getContent()).append("\n");
        }
        bot.sendMsg(event,sengMsg.toString(),false);
    }
    @AnyMessageHandler
    @MessageHandlerFilter(cmd = ".*入机.*|.*人机.*",at = AtEnum.OFF,groups = {679079419L})
    @Order(1)
    public void deleteMessage(Bot bot, AnyMessageEvent event){
        String msg = event.getMessage();
        msg = msg.replaceAll("\\[CQ:[^]]*]","");
        msg = msg.trim();
        msg = msg.trim();
        long myQq = 1825330295L;
        if (msg.equals("我是入机")||event.getMessage().equals("我是人机")){
            if (event.getUserId().equals(myQq)){
                String sengMsg = MsgUtils.builder().at(myQq).text(
                        " 是的，你是入机"
                ).build();
                bot.sendMsg(event,sengMsg,false);
                return;
            }else {
                String sengMsg = MsgUtils.builder().text("你不是入机，")
                        .at(myQq)
                        .text(" 才是入机")
                        .build();
                bot.sendMsg(event,sengMsg,false);
                return;
            }
        }
        if (msg.equals("你是入机")||event.getMessage().equals("你是人机")){
            if (event.getUserId().equals(myQq)){
                String sengMsg = MsgUtils.builder().at(myQq).text(" 你才是入机").build();
                bot.sendMsg(event,sengMsg,false);
                return;
            }
            String sengMsg = MsgUtils.builder().text("我不是入机，")
                    .at(myQq)
                    .text(" 才是入机")
                    .build();
            bot.sendMsg(event,sengMsg,false);
            return;
        }
        if (event.getUserId().equals(myQq)){
            String sendMsg = MsgUtils.builder().text("你是入机").at(myQq).build();
            bot.sendMsg(event,sendMsg,false);
        }else {
            String sendMsg = MsgUtils.builder().text("我不是入机，入机是这个").at(myQq).build();
            bot.sendMsg(event,sendMsg,false);
        }
    }
}
