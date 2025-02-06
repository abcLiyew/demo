package com.esdllm.napcatbot.deepseek;

import com.alibaba.fastjson2.JSON;
import com.esdllm.napcatbot.pojo.aichat.DeepSeekReq;
import com.esdllm.napcatbot.pojo.aichat.DeepSeekResp;
import jakarta.annotation.Resource;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
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
            if (response.getBody().isEmpty()){
                throw new RuntimeException("未知错误deepSeekResp = " + null);
            }
            deepSeekResp = JSON.parseObject(response.getBody(),DeepSeekResp.class);
            return deepSeekResp;
        }else {
            throw new RuntimeException("Ai聊天调用失败"+ response.getStatus()+"\n"+ response.getStatusText()+ "\n"+response.getBody());
        }
    }
}
