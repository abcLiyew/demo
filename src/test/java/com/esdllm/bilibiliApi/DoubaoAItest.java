package com.esdllm.bilibiliApi;

import com.esdllm.napcatbot.AiChatPlugin;
import com.volcengine.ark.runtime.model.bot.completion.chat.BotChatCompletionRequest;
import com.volcengine.ark.runtime.model.bot.completion.chat.BotChatCompletionResult;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.service.ArkService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

//import static com.esdllm.napcatbot.AiChatPlugin.getChatMessage;


public class DoubaoAItest {
    @Resource
    private AiChatPlugin aiChatPlugin;
    private String apiKey = "ec0c0c66-c5f0-49c3-ae15-f36e7515604b";
    private String modelId = "bot-20250116140024-wkjs5";
    @Test
    public void testAi(){
        ArkService arkService = ArkService.builder().apiKey(apiKey).build();
        List<ChatMessage> chatMessages = new ArrayList<>();
        //chatMessages.add(getChatMessage("今日科技新闻"));

        BotChatCompletionRequest chatCompletionRequest = BotChatCompletionRequest.builder()
                .model(modelId)// 需要替换为您的推理接入点ID
                .messages(chatMessages) // 设置消息列表
                .build();
        try {
            String message = null;
            // 获取响应并发送内容
            BotChatCompletionResult chatCompletionResult =  arkService.createBotChatCompletion(chatCompletionRequest);
            chatCompletionResult.getChoices()
                    .forEach(choice -> System.out.println(choice.getMessage()));
        } catch (Exception e) {
            System.out.println("请求失败: " + e.getMessage());
        } finally {
            // 关闭服务执行器
            arkService.shutdownExecutor();
        }
    }
}
