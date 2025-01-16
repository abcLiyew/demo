package com.esdllm.napcatbot.pojo.bilibili.model.data.video;

import com.esdllm.napcatbot.pojo.bilibili.model.data.card.Official;
import lombok.Data;

import java.util.List;

@Data
public class Staff {
    private Long mid;
    private String title;
    private String name;
    private String face;
    private Vip vip;
    private Official official;
    private Long follower;
    private Integer label_style;
}
