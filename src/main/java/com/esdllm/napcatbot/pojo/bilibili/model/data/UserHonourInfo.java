package com.esdllm.napcatbot.pojo.bilibili.model.data;

import lombok.Data;

import java.util.List;

/**
 * 用户荣誉信息
 */
@Data
public class UserHonourInfo {
    private Integer mid;
    private Integer colour;
    private List<Object> tags;
    private Integer is_latest_100honour;
}
