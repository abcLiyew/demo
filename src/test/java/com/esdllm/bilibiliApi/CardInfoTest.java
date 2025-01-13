package com.esdllm.bilibiliApi;

import com.esdllm.exception.BilibiliException;
import com.esdllm.napcatbot.pojo.bilibili.BilibiliCardResp;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardInfoTest {

    private CardInfo cardInfo;
    private ApiBase apiBaseMock;

    @BeforeEach
    void setUp() {
        cardInfo = new CardInfo();
        apiBaseMock = Mockito.mock(ApiBase.class);
    }

    @Test
    void testGetBilibiliLiveResp_Success() throws IOException, BilibiliException {
        // 模拟成功的响应
        // 调用方法
        BilibiliCardResp resp = cardInfo.getBilibiliLiveResp(1L);
        // 验证结果
        assertNotNull(resp);
        assertEquals(0, resp.getCode());
        assertNotNull(resp.getData());
        assertEquals("1", resp.getData().getCard().getMid());
    }

    @Test
    void testGetBilibiliLiveResp_Failure_InvalidResponse() throws IOException {
        // 模拟无效的响应

        // 验证抛出异常
        assertThrows(BilibiliException.class, () -> {
            cardInfo.getBilibiliLiveResp(155855555665552345L);
        });
    }

    @Test
    void testGetBilibiliLiveResp_Failure_NullData() throws IOException {
        // 模拟返回的数据为null

        // 验证抛出异常
        assertThrows(BilibiliException.class, () -> {
            cardInfo.getBilibiliLiveResp(999999999999999999L);
        });
    }

    @Test
    void testGetBilibiliLiveResp_Failure_IOException() throws IOException {
        // 模拟IO异常
        when(apiBaseMock.getCloseableHttpResponse(anyString())).thenThrow(new IOException("Network error"));

        // 验证抛出异常
        assertThrows(IOException.class, () -> {
            cardInfo.getBilibiliLiveResp(999999999999999999L);
        });
    }
}
