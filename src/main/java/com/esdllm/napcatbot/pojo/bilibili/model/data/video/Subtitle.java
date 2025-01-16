package com.esdllm.napcatbot.pojo.bilibili.model.data.video;

import lombok.Data;

import java.util.List;

@Data
public class Subtitle {
    private Boolean allow_submit;
    private List<Object> list;
}
