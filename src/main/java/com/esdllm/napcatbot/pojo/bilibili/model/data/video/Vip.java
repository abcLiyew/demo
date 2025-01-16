package com.esdllm.napcatbot.pojo.bilibili.model.data.video;

import lombok.Data;

@Data
public class Vip {
    private Integer type;
    private Integer status;
    private Long due_date;
    private Integer vip_pay_type;
    private Integer theme_type;
    private Object label;

}
