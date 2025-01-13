package com.esdllm.bilibiliApi;

import com.alibaba.fastjson2.JSON;
import com.esdllm.exception.BilibiliException;
import com.esdllm.napcatbot.pojo.bilibili.BilibiliCardResp;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
public class CardInfo {
    private BilibiliCardResp resp;
    private static final String BaseUrl = "https://api.bilibili.com/x/web-interface/card?mid=";
    BilibiliCardResp getBilibiliLiveResp(Long bilibiliUid) throws BilibiliException, IOException {
        if (Objects.isNull(bilibiliUid)){
        throw new BilibiliException("uid不能为空");
        }
        if (bilibiliUid <= 0){
            throw new BilibiliException("uid不能小于0");
        }
        if (resp!=null&&resp.getData().getCard().getMid().equals(bilibiliUid.toString())){
            return resp;
        }
        String url = BaseUrl + bilibiliUid;

        CloseableHttpResponse response = ApiBase.getCloseableHttpResponse(url);

        BilibiliCardResp resp;
        try {
            HttpEntity entity = response.getEntity();
            resp = JSON.parseObject(EntityUtils.toString(entity), BilibiliCardResp.class);
            EntityUtils.consume(entity);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        finally {
            response.close();
        }
        if (Objects.isNull(resp.getData())||resp.getCode()!=0){
            throw new BilibiliException("获取卡片信息失败");
        }
        this.resp = resp;
        return resp;
    }
    public Integer getArchiveCount(Long bilibiliUid)  {
        if (Objects.isNull(resp)){
            try {
                return getBilibiliLiveResp(bilibiliUid).getData().getArchive_count();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (resp.getData().getCard().getMid().equals(bilibiliUid.toString())){
            return resp.getData().getArchive_count();
        }
        try {
            return getBilibiliLiveResp(bilibiliUid).getData().getArchive_count();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public String getUserName(Long bilibiliUid)  {
        if (Objects.isNull(resp)) {
            try {
                return getBilibiliLiveResp(bilibiliUid).getData().getCard().getName();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (resp.getData().getCard().getMid().equals(bilibiliUid.toString())){
            return resp.getData().getCard().getName();
        }
        try {
            return getBilibiliLiveResp(bilibiliUid).getData().getCard().getName();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public String getFace(Long bilibiliUid) {
        if (Objects.isNull(resp)){
            try {
                return getBilibiliLiveResp(bilibiliUid).getData().getCard().getFace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (resp.getData().getCard().getMid().equals(bilibiliUid.toString())){
            return resp.getData().getCard().getFace();
        }
        try {
            return getBilibiliLiveResp(bilibiliUid).getData().getCard().getFace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
