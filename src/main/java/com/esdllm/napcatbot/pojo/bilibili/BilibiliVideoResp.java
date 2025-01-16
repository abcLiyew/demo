package com.esdllm.napcatbot.pojo.bilibili;

import com.esdllm.napcatbot.pojo.bilibili.model.BilibiliData;
import com.esdllm.napcatbot.pojo.bilibili.model.VideoInfo;
import lombok.Data;

@Data
public class BilibiliVideoResp {
    int code;
    String message;
    int ttl;
    String msg;
    VideoInfo data;

}
