package com.esdllm.napcatbot.pojo.bilibili.model.data.card;

import lombok.Data;

@Data
public class Vip {
    private Integer vipType;
    private String dueRemark;
    private Integer accessStatus;
    private Integer vipStatus;
    private String vipStatusWarn;
    private Integer theme_type;

}
