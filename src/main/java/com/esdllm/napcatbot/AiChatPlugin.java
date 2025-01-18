package com.esdllm.napcatbot;

import com.mikuac.shiro.annotation.AnyMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.enums.AtEnum;
import com.volcengine.ark.runtime.model.bot.completion.chat.BotChatCompletionRequest;
import com.volcengine.ark.runtime.model.bot.completion.chat.BotChatCompletionResult;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Shiro
@Component
public class AiChatPlugin {
    // 从获取API密钥
    @Value("${myConfig.bot.doubao.ARK_API_KEY}")
    private String apiKey;
    @Value("${myConfig.bot.doubao.ARK_API_ID}")
    private String modelId;

    BotChatCompletionRequest chatCompletionRequest;
    // 初始化消息列表
    public List<ChatMessage> chatMessages = new ArrayList<>();
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
        // 创建ArkService实例
        ArkService arkService = ArkService.builder().timeout(Duration.ofSeconds(300)).apiKey(apiKey).build();

        // 将用户消息添加到消息列表
        chatMessages.add(getChatMessage(msg));
        //创建聊天
         chatCompletionRequest = BotChatCompletionRequest .builder()
                .model(modelId)// 需要替换为您的推理接入点ID
                .messages(chatMessages) // 设置消息列表
                .build();
        try {
            String message = null;
            // 获取响应并发送内容
            BotChatCompletionResult chatCompletionResult =  arkService.createBotChatCompletion(chatCompletionRequest);
            chatCompletionResult.getChoices()
                    .forEach(choice -> bot.sendMsg(event, (choice.getMessage().getContent().toString().replace("*","")),false));
        } catch (Exception e) {
            System.out.println("请求失败: " + e.getMessage());
        } finally {
            // 关闭服务执行器
            arkService.shutdownExecutor();
        }
    }

    private static ChatMessage getChatMessage(String aiMsg) {
        return ChatMessage.builder()
                .role(ChatMessageRole.USER) // 设置消息角色为用户
                .content(aiMsg) // 设置消息内容
                .build();
    }
    @AnyMessageHandler
    @MessageHandlerFilter(cmd = "开启新对话", at = AtEnum.NEED)
    public void onClearMessage(Bot bot, AnyMessageEvent event){
        // 清空消息列表
        chatMessages.clear();
        bot.sendMsg(event, "已清空消息列表", false);
    }
}
