package com.esdllm.bilibiliApi;

import com.esdllm.napcatbot.pojo.bilibili.model.VideoInfo;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class BilibiliClientTest {
    private BilibiliClient client = new BilibiliClient() ;
    @Test
    public void testGetVideoInfoByBvid() throws IOException {
        String bvid = "BV1TXqAYREDn";
        VideoInfo videoInfo = client.getVideoInfo(bvid);
        System.out.println(videoInfo);
    }
    @Test
    public void testGetVideoInfoByAv() throws IOException {
        Long av = 85440373L;
        VideoInfo videoInfo = client.getVideoInfo(av);
        System.out.println(videoInfo);
    }
    @Test
    public void testGetVideoInfo() throws IOException {
        String bvid = "BV1U7411m75S";
        Long av = client.getVideoAv(bvid);
        System.out.println(av);
    }
    @Test
    public void testGetVideoCoverUrl() {
        String bvid = "BV1U7411m75S";
        String coverUrl = client.getVideoCoverUrl(bvid);
        System.out.println(coverUrl);
        Long av = 85440373L;
        coverUrl = client.getVideoCoverUrl(av);
        System.out.println(coverUrl);
    }

}