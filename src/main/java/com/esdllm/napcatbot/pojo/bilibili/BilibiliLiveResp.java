package com.esdllm.napcatbot.pojo.bilibili;

import com.esdllm.napcatbot.pojo.bilibili.model.data.LiveRoom;
import lombok.Data;

@Data
public class BilibiliLiveResp {
    int code;
    String message;
    int ttl;
    String msg;
    LiveRoom data;

}
