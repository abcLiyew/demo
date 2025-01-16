package com.esdllm.bilibiliApi;


import com.esdllm.exception.BilibiliException;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.Test;


import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

// 测试LiveRoom类的单元测试
public class LiveRoomTest {

    Live liveRoom = new Live();

    // 测试正常情况
    @Test
    public void testGetLiveStatus_HappyPath() throws Exception {
        Long bilibiliUid = 1937838858L; // 假设的正常UID
        int expectedStatus = 0; // 假设返回的直播状态

        // 这里模拟调用getLiveStatus方法并返回预期值
        // 使用Mockito或其他模拟库进行依赖注入，这里为了简洁直接调用
        int actualStatus = liveRoom.getLiveStatus(bilibiliUid);

        // 断言返回的直播状态与预期相符
        assertEquals(expectedStatus, actualStatus);
    }

    // 测试UID为null的情况
    @Test
    public void testGetLiveStatus_NullUid() {
        Long bilibiliUid = null;

        // 断言抛出异常
        assertThrows(BilibiliException.class, () -> {
            liveRoom.getLiveStatus(bilibiliUid);
        });
    }

    // 测试非法UID（负数）
    @Test
    public void testGetLiveStatus_InvalidUid() {
        Long bilibiliUid = -1L; // 非法UID

        // 断言抛出异常
        assertThrows(BilibiliException.class, () -> {
            liveRoom.getLiveStatus(bilibiliUid);
        });
    }

    // 测试网络异常情况
    @Test
    public void testGetLiveStatus_NetworkException() {
        Long bilibiliUid = 99999999999L; // 假设不存在的UID

        // 这里可以使用Mockito模拟HttpClient抛出IOException
        assertThrows(RuntimeException.class, () -> {
            liveRoom.getLiveStatus(bilibiliUid);
        });
    }

    // 测试返回的BilibiliResp对象为null的情况
    @Test
    public void testGetLiveStatus_NullResponse() throws Exception {
        Long bilibiliUid = 789012L; // 假设返回null的UID

        // 这里同样可以使用Mockito来模拟返回null的情况
        assertThrows(BilibiliException.class, () -> {
            liveRoom.getLiveStatus(bilibiliUid);
        });
    }
    @Test
    public void httpTest() throws IOException {
        String url = "https://b23.tv/hw4u0yJ";
//        CloseableHttpResponse response = ApiBase.getCloseableHttpResponse(url);
//        HttpEntity entity = response.getEntity();
//        //获取响应头信息
//        System.out.println(response.getStatusLine().getStatusCode());
//        System.out.println(response.getStatusLine().getReasonPhrase());
        //获取响应内容
        HttpResponse response = ApiBase.getHttpResponseNotRedirect(url);
        String location = response.getHeaders("location")[0].getValue();
        System.out.println("location = " + location);
        String[] split = location.split("/");
        for (String s : split) {
            System.out.println(s);
        }
        System.out.println("split[split.length-2] = " + split[split.length - 2]);
        int indexOf = split[split.length - 1].indexOf("?");
        String BvId = split[split.length-1].substring(0, indexOf);
        System.out.println(BvId);


    }
    @Test
    public void testGetLive_time(){
        Long roomId = 1937838858L;
        String liveTime = liveRoom.getLiveTime(roomId);
        System.out.println(liveTime);
    }
}
