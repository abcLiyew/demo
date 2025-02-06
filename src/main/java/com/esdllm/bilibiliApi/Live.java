package com.esdllm.bilibiliApi;

import com.alibaba.fastjson2.JSON;
import com.esdllm.exception.BilibiliException;
import com.esdllm.napcatbot.pojo.bilibili.BilibiliLiveResp;
import com.esdllm.napcatbot.pojo.bilibili.model.data.LiveRoom;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author 饿死的流浪猫
 * @decription 直播间相关接口
 */
@Component
public class Live {
    private  static final String BaseUrl = "https://api.live.bilibili.com/room/v1/Room/get_info?room_id=";

    /***
     * 获取直播间信息
     * @param roomId 直播间房间号
     * @return LiveRoom 直播间信息
     */
    public LiveRoom getLiveRoom(Long roomId)  {
        BilibiliLiveResp resp;
        try {
            resp = getBilibiliLiveResp(roomId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (Objects.isNull(resp.getData())){
            throw new BilibiliException("获取直播间信息失败");
        }
        return resp.getData();
    }
    /**
     * 获取直播间信息
     * @param roomId 直播间房间号
     * @return BilibiliLiveResp 直播间信息
     */
    private BilibiliLiveResp getBilibiliLiveResp(Long roomId) throws BilibiliException, IOException {
        String url = BaseUrl + roomId;

        CloseableHttpResponse response = ApiBase.getCloseableHttpResponse(url);

        BilibiliLiveResp resp;
        try {
            HttpEntity entity = response.getEntity();
            resp = JSON.parseObject(EntityUtils.toString(entity), BilibiliLiveResp.class);
            EntityUtils.consume(entity);
        }catch (Exception e){
            resp = getBilibiliLiveResp(roomId);
        }
        finally {
            response.close();
        }
        if (Objects.isNull(resp.getData())||resp.getCode()!=0){
            throw new BilibiliException("获取直播间信息失败");
        }
        return resp;
    }

    /**
     * 获取直播间状态
     * @param room_id 直播间房间号
     * @return int 直播间状态 0 未开播 1 直播中 2 轮播中
     * @throws IOException 网络异常
     */
    public int getLiveStatus(Long room_id) throws IOException {
        BilibiliLiveResp resp = getBilibiliLiveResp(room_id);
        if (Objects.isNull(resp.getData())){
            throw new BilibiliException("获取直播间信息失败");
        }
        return resp.getData().getLive_status();
    }
    /**
     * 获取直播间地址
     * @param room_id 直播间房间号
     * @return String 直播间地址
     */
    public String getLiveUrl(Long room_id) {
        String baseLiveUrl = "https://live.bilibili.com/";
        return baseLiveUrl + room_id;
    }

    /**
     * 获取直播间标题
     * @param room_id 直播间房间号
     * @return String 直播间标题
     * @throws IOException 网络异常
     */
    public String getLiveTitle(Long room_id) throws IOException {
        BilibiliLiveResp resp = getBilibiliLiveResp(room_id);
        if (Objects.isNull(resp.getData())){
            throw new BilibiliException("获取直播间信息失败");
        }
        return resp.getData().getTitle();
    }
    /**
     * 获取直播间封面
     * @param room_id 直播间房间号
     * @return String 直播间封面url
     * @throws IOException 网络异常
     */
    public String getImageUrl(Long room_id) throws IOException {
        BilibiliLiveResp resp = getBilibiliLiveResp(room_id);
        if (Objects.isNull(resp.getData().getUser_cover())){
            throw new BilibiliException("获取封面失败");
        }
        return resp.getData().getUser_cover();
    }
    /**
     * 获取主播uid
     * @param roomId 直播间房间号
     * @return Long 主播uid
     */
    public Long getUid(Long roomId) {
        BilibiliLiveResp resp;
        try {
            resp = getBilibiliLiveResp(roomId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return resp.getData().getUid();
    }
    /**
     * 获取直播间分区名
     * @param roomId 直播间房间号
     * @return String 直播间分区名
     */
    public String getLiveArea(Long roomId) {
        BilibiliLiveResp bilibiliLiveResp;
        try {
            bilibiliLiveResp = getBilibiliLiveResp(roomId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bilibiliLiveResp.getData().getArea_name();
    }
    /**
     * 获取直播间观看人数
     * @param roomId 直播间房间号
     * @return int 直播间观看人数
     */
    public int getOnline(Long roomId) {
        BilibiliLiveResp bilibiliLiveResp;
        try {
            bilibiliLiveResp = getBilibiliLiveResp(roomId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bilibiliLiveResp.getData().getOnline();
    }
    /**
     * 获取直播间关键帧
     * @param roomId 直播间房间号
     * @return String 直播间关键帧url
     */
    public String getKeyFrame(Long roomId) {
        BilibiliLiveResp bilibiliLiveResp;
        try {
            bilibiliLiveResp = getBilibiliLiveResp(roomId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bilibiliLiveResp.getData().getKeyframe();
    }
    /**
     * 获取直播间标签
     * @param roomId 直播间房间号
     * @return String 直播间标签 ','分割
     */
    public String getTags(Long roomId) {
        BilibiliLiveResp bilibiliLiveResp;
        try {
            bilibiliLiveResp = getBilibiliLiveResp(roomId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bilibiliLiveResp.getData().getTags();
    }
    /**
     * 获取直播间描述
     * @param roomId 直播间房间号
     * @return String 直播间描述
     */
    public String getDescription(Long roomId) {
        BilibiliLiveResp bilibiliLiveResp;
        try {
            bilibiliLiveResp = getBilibiliLiveResp(roomId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bilibiliLiveResp.getData().getDescription();
    }
    /**
     * 获取直播间开播时间
     * @param roomId 直播间房间号
     * @return String 直播间开播时间
     */
    public String getLiveTime(Long roomId) {
        BilibiliLiveResp bilibiliLiveResp;
        try {
            bilibiliLiveResp = getBilibiliLiveResp(roomId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bilibiliLiveResp.getData().getLive_time();
    }
    /**
     * 获取直播间pk状态
     * @param roomId 直播间房间号
     * @return int 直播间pk状态
     */
    public int getPkStatus(Long roomId) {
        BilibiliLiveResp bilibiliLiveResp;
        try {
            bilibiliLiveResp = getBilibiliLiveResp(roomId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bilibiliLiveResp.getData().getPk_status();
    }
    /**
     * 获取直播间热词
     * @param roomId 直播间房间号
     * @return List<String> 直播间热词
     */
    public List<String> getHotWords(Long roomId) {
        BilibiliLiveResp bilibiliLiveResp;
        try {
            bilibiliLiveResp = getBilibiliLiveResp(roomId); // 调用方法获取BilibiliLiveResp对象
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bilibiliLiveResp.getData().getHot_words();
    }
}
