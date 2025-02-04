package com.esdllm.napcatbot.deepseek;

import com.alibaba.fastjson2.JSON;
import com.esdllm.napcatbot.pojo.aichat.DeepSeekReq;
import com.esdllm.napcatbot.pojo.aichat.DeepSeekResp;
import jakarta.annotation.Resource;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class AiChatDeepSeekService {
    @Resource
    private DeepSeekReq deepSeekReq;
    @Resource
    private DeepSeekResp deepSeekResp;

    public DeepSeekResp getResp(String baseUrl, String token, String model, List<DeepSeekReq.Message> message,int maxTokens){
        deepSeekReq.setMessages(message);
        deepSeekReq.setModel(model);
        deepSeekReq.setMax_tokens(maxTokens);
        HttpResponse<String> response = Unirest.post(baseUrl)
                .header("Authorization","Bearer " +token)
                .header("Content-Type", "application/json")
                .body(JSON.toJSON(deepSeekReq).toString())
                .asString();
        if (response.getStatus() == 200) {
            deepSeekResp = JSON.parseObject(response.getBody(),DeepSeekResp.class);
            return deepSeekResp;
        }else {
            throw new RuntimeException("Ai聊天调用失败");
        }
    }
}
