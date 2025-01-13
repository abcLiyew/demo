package com.esdllm.napcatbot.pojo.bilibili.model.data.elec;

import lombok.Data;

@Data
public class ShowInfo {
    private Boolean show;
    private Integer state;
    private String title;
    private String icon;
    private String jump_url;
}
