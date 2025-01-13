package com.esdllm.napcatbot.pojo.bilibili.model.data.card;

import lombok.Data;

/**
 * 头像挂机消息
 */
@Data
public class Pendant {
    private Integer pid;
    private String name;
    private String image;
    private Integer expire;
}
