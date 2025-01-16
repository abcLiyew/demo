package com.esdllm.napcatbot.pojo.bilibili.model.data.video;

import lombok.Data;

@Data
public class Stat {
    private Long aid;
    private Long view;
    private Long danmaku;
    private Long reply;
    private Long favorite;
    private Long coin;
    private Long share;
    private Long now_rank;
    private Long his_rank;
    private Long like;
    private Long dislike;
    private String evaluation;
    private Integer vt;
}
