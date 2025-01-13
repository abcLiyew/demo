package com.esdllm.napcatbot.pojo.bilibili.model.data.card;

import lombok.Data;

import java.util.Map;
import java.util.Objects;

@Data
public class AvatarIcon {
    private Map<String, Objects> icon_resource;
}
