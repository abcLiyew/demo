package com.esdllm.napcatbot.pojo.bilibili.model.data.video;

import lombok.Data;

@Data
public class Pages {
    private Long cid;
    private Integer page;
    private String from;
    private String part;
    private Long duration;
    private String vid;
    private String weblink;
    private Dimension dimension;
}
