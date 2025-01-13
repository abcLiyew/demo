package com.esdllm.napcatbot.pojo.bilibili.model.data;

import com.esdllm.napcatbot.pojo.bilibili.model.data.fans.Medal;
import lombok.Data;

/**
 * 粉丝牌消息
 */
@Data
public class FansMedal {
    /**
     * 是否显示粉丝牌
     */
    private Boolean show;
    /**
     * 是否戴粉丝牌
     */
    private Boolean wear;
    /**
     * 粉丝牌消息
     */
    private Medal medal;
}
