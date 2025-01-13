package com.esdllm.napcatbot.pojo.bilibili.model.data.card;

import lombok.Data;

@Data
public class LevelInfo {
    private Integer current_level;
    private Integer current_min;
    private Integer current_exp;
    private Integer next_exp;
}
