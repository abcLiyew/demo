package com.esdllm.bilibiliApi;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Test;


public class DeepSeekApiTest {
    private final String baseUrl= "https://api.siliconflow.cn/v1/chat/completions";

    @Test
    public void test() {
        HttpResponse<String> response = Unirest.post(baseUrl)
                .header("Authorization", "Bearer sk-fkowqeedvznnxnmvujscudkorisojqlzjbmacnmhjmcqhmbt")
                .header("Content-Type", "application/json")
                .body("{\"max_tokens\":128,\"temperature\":0.6,\"top_p\":0.7,\"top_k\":50,\"frequency_penalty\":0,\"chat_id\":\"hPd4UwN\",\"model\":\"deepseek-ai/DeepSeek-R1\",\"messages\":[{\"role\":\"user\",\"content\":\"中国大模型行业2025年将会迎来哪些机遇和挑战\"}]}")
                .asString();

        System.out.println("response = " + response.getBody());
    }
}
