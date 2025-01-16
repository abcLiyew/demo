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

/**
 * @decription 名片信息获取
 * @author 饿死的流浪猫
 */
@Component
public class CardInfo {
    private BilibiliCardResp resp;
    private static final String BaseUrl = "https://api.bilibili.com/x/web-interface/card?mid=";

    /**
     * 获取Bilibili名片信息
     * @param bilibiliUid bilibili用户的Uid
     * @return BilibiliCardResp对象
     */
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

    /**
     * 获取用户稿件数量
     * @param bilibiliUid bilibili用户的Uid
     * @return int 用户稿件数量
     */
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

    /**
     * 获取用户名
     * @param bilibiliUid bilibili用户的Uid
     * @return String 用户名
     */
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
    /**
     * 获取用户头像
     * @param bilibiliUid bilibili用户的Uid
     * @return String 用户头像url
     */
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
    /**
     * 获取用户等级
     * @param bilibiliUid bilibili用户的Uid
     * @return Integer 用户等级
     */
    public Integer getLevel(Long bilibiliUid) {
        if (Objects.isNull(resp)){
            try {
                return getBilibiliLiveResp(bilibiliUid).getData().getCard().getLevel_info().getCurrent_level();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (resp.getData().getCard().getMid().equals(bilibiliUid.toString())){
            return resp.getData().getCard().getLevel_info().getCurrent_level();
        }
        try {
            return getBilibiliLiveResp(bilibiliUid).getData().getCard().getLevel_info().getCurrent_level();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 获取用户签名
     * @param bilibiliUid bilibili用户的Uid
     * @return String 用户签名
     */
    public String getSign(Long bilibiliUid) {
        if (Objects.isNull(resp)) {
            try {
                return getBilibiliLiveResp(bilibiliUid).getData().getCard().getSign();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (resp.getData().getCard().getMid().equals(bilibiliUid.toString())){
            return resp.getData().getCard().getSign();
        }
        try {
            return getBilibiliLiveResp(bilibiliUid).getData().getCard().getSign();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
