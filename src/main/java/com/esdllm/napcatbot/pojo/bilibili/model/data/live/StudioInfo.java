package com.esdllm.napcatbot.pojo.bilibili.model.data.live;

import lombok.Data;

import java.util.ArrayList;

@Data
public class StudioInfo {
    private Integer status;
    private ArrayList<Object> master_list;
}
