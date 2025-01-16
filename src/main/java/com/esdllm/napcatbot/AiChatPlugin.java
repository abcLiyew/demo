package com.esdllm.napcatbot;

import com.mikuac.shiro.annotation.common.Shiro;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Shiro
@Component
public class AiChatPlugin {
    @Value("${myConfig.bot.doubao.ARK_API_KEY}")
    private String apiKey;
}
