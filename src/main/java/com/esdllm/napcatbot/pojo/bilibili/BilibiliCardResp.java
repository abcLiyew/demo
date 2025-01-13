package com.esdllm.napcatbot.pojo.bilibili;

import com.esdllm.napcatbot.pojo.bilibili.model.BilibiliData;
import com.esdllm.napcatbot.pojo.bilibili.model.data.Card;
import lombok.Data;

@Data
public class BilibiliCardResp {
    int code;
    String message;
    int ttl;
    String msg;
    BilibiliData data;

}
