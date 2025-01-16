package com.esdllm.bilibiliApi;

import com.esdllm.common.ShotChainInfo;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ShortChainTest {
    @Test
    public void testShortChain()  {
        //直播
        System.out.println("-------直播--------");
        String shortChain = "https://b23.tv/rrvrIh1";
        ShortChain sc = new ShortChain();
        ShotChainInfo info = sc.getShotChainInfo(shortChain);
        System.out.println("info = " + info);

        //视频
        System.out.println("-------视频--------");
        shortChain = "https://b23.tv/pigt3PQ";
        System.out.println("info = " + sc.getShotChainInfo(shortChain));

        //动态
        System.out.println("-------动态--------");
        shortChain = "https://b23.tv/yfwNtyl";
        System.out.println("info = " + sc.getShotChainInfo(shortChain));
        shortChain = "https://b23.tv/RroYNVv";
        System.out.println("info = " + sc.getShotChainInfo(shortChain));
        //番剧
        System.out.println("-------番剧--------");
        shortChain = "https://b23.tv/ep779775";
        System.out.println("info = " + sc.getShotChainInfo(shortChain));
        //个人空间
        System.out.println("-------个人空间--------");
        shortChain = "https://b23.tv/sDkXotU";
        System.out.println("info = " + sc.getShotChainInfo(shortChain));
        //其他
        System.out.println("-------其他--------");
        shortChain = "https://b23.tv/dltYx0Z";
        System.out.println("info = " + sc.getShotChainInfo(shortChain));
        shortChain = "https://b23.tv/y677777";
        System.out.println("info = " + sc.getShotChainInfo(shortChain));
    }
    @Test
    public void testIsLive()  {
        int seed = 123456789;
        Random random = new Random(seed);
        System.out.println(random.nextInt(100)); //65
        System.out.println(random.nextInt(100)); //0
        System.out.println(random.nextInt(100)); //83
        System.out.println(random.nextInt(100)); //44

    }
}