package com.esdllm.bilibiliApi;

import com.alibaba.fastjson2.JSON;
import com.esdllm.exception.BilibiliException;
import com.esdllm.napcatbot.pojo.bilibili.BilibiliLiveResp;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
public class LiveRoom {
    private  static final String BaseUrl = "https://api.live.bilibili.com/room/v1/Room/get_info?room_id=";

    private BilibiliLiveResp getBilibiliLiveResp(Long roomId) throws BilibiliException, IOException {
        String url = BaseUrl + roomId;

        CloseableHttpResponse response = ApiBase.getCloseableHttpResponse(url);

        BilibiliLiveResp resp;
        try {
            HttpEntity entity = response.getEntity();
            resp = JSON.parseObject(EntityUtils.toString(entity), BilibiliLiveResp.class);
            EntityUtils.consume(entity);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        finally {
            response.close();
        }
        if (Objects.isNull(resp.getData())||resp.getCode()!=0){
            throw new BilibiliException("获取直播间信息失败");
        }
        return resp;
    }

    public int getLiveStatus(Long room_id) throws IOException {
        BilibiliLiveResp resp = getBilibiliLiveResp(room_id);
        if (Objects.isNull(resp.getData())){
            throw new BilibiliException("获取直播间信息失败");
        }
        return resp.getData().getLive_status();
    }
    public String getLiveUrl(Long room_id) throws IOException {
        BilibiliLiveResp resp = getBilibiliLiveResp(room_id);
        if (Objects.isNull(resp.getData())){
            throw new BilibiliException("获取直播间信息失败");
        }
        String baseLiveUrl = "https://live.bilibili.com/";
        return baseLiveUrl + resp.getData().getRoom_id();
    }
    public String getLiveTitle(Long room_id) throws IOException {
        BilibiliLiveResp resp = getBilibiliLiveResp(room_id);
        if (Objects.isNull(resp.getData())){
            throw new BilibiliException("获取直播间信息失败");
        }
        return resp.getData().getTitle();
    }

    public String getImageUrl(Long room_id) throws IOException {
        BilibiliLiveResp resp = getBilibiliLiveResp(room_id);
        if (Objects.isNull(resp.getData().getUser_cover())){
            throw new BilibiliException("获取封面失败");
        }
        return resp.getData().getUser_cover();
    }

    public Long getUid(Long roomId) {
        BilibiliLiveResp resp;
        try {
            resp = getBilibiliLiveResp(roomId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return resp.getData().getUid();
    }
    public String getLiveArea(Long roomId) {
        BilibiliLiveResp bilibiliLiveResp;
        try {
            bilibiliLiveResp = getBilibiliLiveResp(roomId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bilibiliLiveResp.getData().getArea_name();
    }
}
