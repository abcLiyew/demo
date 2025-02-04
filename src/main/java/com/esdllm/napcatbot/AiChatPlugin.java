package com.esdllm.napcatbot;

import com.esdllm.common.ChatMessageUtils;
import com.esdllm.common.ModelEnum;
import com.esdllm.napcatbot.deepseek.AiChatDeepSeekService;
import com.esdllm.napcatbot.pojo.aichat.DeepSeekReq;
import com.esdllm.napcatbot.pojo.aichat.DeepSeekResp;
import com.mikuac.shiro.annotation.AnyMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.enums.AtEnum;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
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
    @Value("${myConfig.bot.aiChat.model_id}")
    private String modelId;
    @Value("${myConfig.bot.aiChat.base_url}")
    private String baseUrl;
    @Value("${myConfig.bot.aiChat.max_tokens}")
    private Integer maxTokens;

    @Resource
    private AiChatDeepSeekService aiChatDeepSeekService;
    @Resource
    private DeepSeekResp deepSeekResp;

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
                .text("回答尽可能简短,不要使用markdown格式");
        chatMessages.add(system.build());
    }

    /**
     * 机器人接收消息并向ai发请求
     * @param  bot 机器人实例
     * @param  event 消息事件
     */
    @AnyMessageHandler
    @MessageHandlerFilter(cmd = "((?i)^(ai:|ai：).*)|(.*/)$",at = AtEnum.NEED)
    public void onReceiveMessage(Bot bot, AnyMessageEvent event) {
        // 过滤消息
        String msg = event.getMessage();
        msg = msg.replaceAll("\\[CQ:[^]]*]","");
        msg = msg.trim();
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

        DeepSeekResp resp;
        try {
            resp = aiChatDeepSeekService.getResp(baseUrl, token, modelId, chatMessages, maxTokens);
        }catch (Exception e){
            log.error(e.getMessage());
            bot.sendMsg(event,"服务器繁忙，请稍后再试。",false);
            chatMessages.remove(chatMessages.size() - 1);
            return;
        }
        // 处理ai返回的结果
        String sendMsg = "";
        for (DeepSeekResp.Choices choice : resp.getChoices()) {
            if (choice.getMessage().getRole().equals("assistant")) {
                String text = choice.getMessage().getContent();
                sendMsg = text.replaceAll("[#|*]","");
                sendMsg+="\n\n\n消耗"+resp.getUsage().getTotal_tokens()+"token(请求"+resp.getUsage().getPrompt_tokens()+"，回答"+resp.getUsage().getCompletion_tokens()+")";
                chatMessages.add(choice.getMessage());
                while (chatMessages.size() > 20) { // 系统消息+10轮对话
                    chatMessages.remove(1); // 始终保留索引0的系统消息
                }
                bot.sendMsg(event,sendMsg,false);
            }
        }


    }
    @AnyMessageHandler
    @MessageHandlerFilter(cmd = "(获取思考过程.*)$",at = AtEnum.NEED)
    public void onGetReasoningMessage(Bot bot, AnyMessageEvent event) {
        if (!modelId.equals(ModelEnum.DeepSeek_R1.getName())){
            bot.sendMsg(event,"当前模型不支持获取思考过程。",false);
            return;
        }
        String reasoningContent = chatMessages.get(chatMessages.size() - 1).getReasoning_content();
        if (!chatMessages.get(chatMessages.size()-1).getRole().equals("assistant")&& chatMessages.size()>2){
            reasoningContent = chatMessages.get(chatMessages.size() - 2).getReasoning_content();
        }
        if (Objects.isNull(reasoningContent)){
            bot.sendMsg(event,"消息列表是空的。",false);
        }else {
            bot.sendMsg(event,reasoningContent,false);
        }
    }
    @AnyMessageHandler
    @MessageHandlerFilter(cmd = "(清空消息列表.*)$|(开启新对话.*)$",at = AtEnum.NEED)
    public void onNewChat(Bot bot, AnyMessageEvent event){
        chatMessages.clear();
        setSystemMessage();
        bot.sendMsg(event,"已清空消息列表。",false);
    }

    @Scheduled(cron = "0 0/30 * * * *")
    public void clearChatList(){
        this.clear();
        this.setSystemMessage();
    }
}
