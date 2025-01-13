package com.esdllm.napcatbot.pojo.bilibili.model.data;

import lombok.Data;

/**
 * 用户职业信息
 */
@Data
public class Profession {
    private String name;
    private String department;
    private String title;
    private Integer is_show;

}
