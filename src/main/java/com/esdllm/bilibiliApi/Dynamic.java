package com.esdllm.bilibiliApi;

import com.alibaba.fastjson2.JSON;
import com.esdllm.exception.BilibiliException;
import com.esdllm.napcatbot.pojo.bilibili.BilibiliDynamicResp;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
public class Dynamic {
    private final String BaseUrl = "https://api.vc.bilibili.com/dynamic_svr/v1/dynamic_svr/get_dynamic_detail?dynamic_id=";

    public BilibiliDynamicResp.Data.Card getDynamicDetail(String dynamicId) throws IOException {
        if(dynamicId == null || dynamicId.isEmpty()){
            throw new BilibiliException("动态ID不能为空");
        }
        BilibiliDynamicResp resp = null;
        String url = BaseUrl + dynamicId;
        CloseableHttpResponse response = ApiBase.getCloseableHttpResponse(url);

        try {
            HttpEntity entity = response.getEntity();
            resp = JSON.parseObject(EntityUtils.toString(entity),BilibiliDynamicResp.class);
            EntityUtils.consume(entity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            response.close();
        }
        if (Objects.isNull(resp) ||resp.getCode() != 0){
            throw new BilibiliException("获取动态详情失败");
        }
        return resp.getData().getCard();
    }
}
