package com.esdllm.napcatbot;

import com.esdllm.bilibiliApi.*;
import com.esdllm.napcatbot.pojo.bilibili.BilibiliDynamicResp;
import com.esdllm.napcatbot.pojo.bilibili.model.VideoInfo;
import com.esdllm.napcatbot.pojo.bilibili.model.data.LiveRoom;
import com.esdllm.napcatbot.pojo.bilibili.model.data.video.Staff;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class BilibiliAnalysisPlugin extends BotPlugin {
    @Resource
    private CardInfo cardInfo;
    @Resource
    private BilibiliClient bilibiliClient;
    @Resource
    private Dynamic dynamic;

    @Override
    public int onAnyMessage(Bot bot, AnyMessageEvent event) {
        String message = event.getMessage();
        message = message.replace("\\","");
        message = message.replace("&#44;","");
        String[] strArr = message.split("\"");
        String url = null;
        for (String s : strArr) {
            int i = s.indexOf("http");
            int j = s.indexOf("www");
            if ((j>i||j ==-1)&&i!=-1) {
                s = s.substring(i);
            }else if (j!=-1){
                s = s.substring(j);
                s = "https://"+s;
            }
            s = s.trim();
            // 使用正则表达式查找空白字符及其后面的所有字符
            Pattern pattern = Pattern.compile("[\\s一-龥]|[\u3000-〿\uFF00-\uFFEF‐-‟\u3000-〿]");
            Matcher matcher = pattern.matcher(s);
            if (matcher.find()) {
                s = s.substring(0, matcher.start()); // 截取空白字符前的部分
            }
            s = s.trim();
            if (s.startsWith("https://b23")||s.startsWith("http://b23")||s.startsWith("https://www.b23")) {
                url = s;
                break;
            }
            if (s.startsWith("https://live.bilibili.com")||s.startsWith("http://live.bilibili.com")) {
                return onLiveUrl(bot,event,s);
            }
            if (s.startsWith("https://www.bilibili.com/video")||s.startsWith("http://www.bilibili.com/video")){
                return onVideoUrl(bot,event,s);
            }
            if (s.startsWith("https://www.bilibili.com/opus")){
                return onDynamicUrl(bot,event,s);
            }

        }
        for (String s : strArr){
            int i = s.indexOf("BV");
            if (i!=-1) {
                String bvid = s.substring(i,i+12);
                try {
                    VideoInfo info = bilibiliClient.getVideoInfo(bvid);
                    return sendVideoMsg(bot, event, info);
                } catch (IOException e) {
                    log.error(e.getMessage());
                    return MESSAGE_IGNORE;
                }
            }
        }
        if (url== null) {
            return MESSAGE_IGNORE;
        }
        ShortChain shortChain = new ShortChain(url);
        if (shortChain.getShotChainInfo().getType() == 0){
            LiveRoom liveRoom = shortChain.getLiveRoom();
            return sendLiveMsg(bot, event, liveRoom);
        }else if (shortChain.getShotChainInfo().getType() == 1){
            VideoInfo info = shortChain.getVideoInfo();
            return sendVideoMsg(bot, event, info);
        }else if (shortChain.getShotChainInfo().getType() == 2){
            BilibiliDynamicResp.Data.Card  card = shortChain.getDynamicCard();
            return sendDynamicMsg(bot,event,card);
        }

        return MESSAGE_IGNORE;
    }

    private int onDynamicUrl(Bot bot, AnyMessageEvent event, String url) {
        String[] urlArr = url.split("/");
        int indexOf = urlArr[urlArr.length - 1].indexOf("?");
        if (indexOf<=0){
            indexOf =urlArr[urlArr.length-1].length();
        }
        String dynamicIdStr = urlArr[urlArr.length-1].substring(0, indexOf);
        BilibiliDynamicResp.Data.Card  card;
        try {
            card = dynamic.getDynamicDetail(dynamicIdStr);
        } catch (IOException e) {
            log.error(e.getMessage());
            return MESSAGE_IGNORE;
        }
        return sendDynamicMsg(bot,event,card);
    }

    private int onVideoUrl(Bot bot, AnyMessageEvent event, String url) {
        String[] urlArr = url.split("/");
        int indexOf = urlArr[urlArr.length - 1].indexOf("?");
        if (indexOf<=0){
            indexOf =urlArr[urlArr.length-1].length();
        }
        String videoIdStr = urlArr[urlArr.length-1].substring(0, indexOf);
        VideoInfo info;
        if (videoIdStr.startsWith("BV")){
            try {
                info= bilibiliClient.getVideoInfo(videoIdStr);
            } catch (IOException e) {
                log.error(e.getMessage());
                return MESSAGE_IGNORE;
            }
        }else {
            Long aid = Long.parseLong(videoIdStr.substring(2));
            try {
                info= bilibiliClient.getVideoInfo(aid);
            } catch (IOException e) {
                log.error(e.getMessage());
                return MESSAGE_IGNORE;
            }
        }
        return sendVideoMsg(bot, event, info);
    }

    private int sendVideoMsg(Bot bot, AnyMessageEvent event, VideoInfo info) {
        String msg = MsgUtils.builder().img(info.getPic()).text(
                "av"+info.getAid()+"\n"+
                        info.getBvid()+"\n"+
                        "标题："+info.getTitle()+"\n"+
                        "简介："+info.getDesc()+"\n"+
                        "上传时间："+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(info.getPubdate()*1000)+"\n"+
                        "播放量："+info.getStat().getView()+
                        "，弹幕："+info.getStat().getDanmaku()+"\n评论："+info.getStat().getReply() +
                        "，收藏："+info.getStat().getFavorite() +
                        "\n点赞："+info.getStat().getLike()+"，投币："+info.getStat().getCoin()+
                        "\n分享："+info.getStat().getShare()+
                        "\nup主："+info.getOwner().getName()+"\n"+"up主uid："+info.getOwner().getMid()+"\n"+
                        getStaff(info.getStaff())+"\nhttps://www.bilibili.com/video/"+info.getBvid()
        ).build();
        bot.sendMsg(event,msg,false);
        return MESSAGE_IGNORE;
    }

    public String getStaff(List<Staff> staff){
        if (staff==null||staff.isEmpty()){
            return "";
        }
        String str = "合作up主：";
        for (Staff s : staff){
            str = s.getName()+",";
        }
        str = str.substring(0,str.length()-1);
        return str;
    }

    public int onLiveUrl(Bot bot, AnyMessageEvent event, String url){
        String[] urlArr = url.split("/");
        int indexOf = urlArr[urlArr.length - 1].indexOf("?");
        if (indexOf<=0){
            indexOf =urlArr[urlArr.length-1].length();
        }
        String roomIdStr = urlArr[urlArr.length-1].substring(0, indexOf);
        Long roomId = Long.parseLong(roomIdStr);
        LiveRoom liveRoom = new Live().getLiveRoom(roomId);
        if (liveRoom==null){
            return MESSAGE_IGNORE;
        }
        return sendLiveMsg(bot, event, liveRoom);
    }

    private int sendLiveMsg(Bot bot, AnyMessageEvent event, LiveRoom liveRoom) {
        String msg = MsgUtils.builder()
                .text(
                        "房间号："+liveRoom.getRoom_id()+"\n"+
                                "标题："+liveRoom.getTitle()+"\n"+
                                "up主："+cardInfo.getUserName(liveRoom.getUid())+"\n"+
                                "up主uid："+liveRoom.getUid()+"\n"+
                                "观看人数："+liveRoom.getOnline()+"\n"+
                                "直播分区："+liveRoom.getArea_name()+"\n"+
                                "开播状态: "+(liveRoom.getLive_status()==1?"正在直播":liveRoom.getLive_status()==0?"未开播":"轮播中")+"\n"+
                                "https://live.bilibili.com/"+liveRoom.getRoom_id()+"\n\n"
                ).img(liveRoom.getUser_cover()).build();
        bot.sendMsg(event,msg,false);
        return MESSAGE_IGNORE;
    }

    private int sendDynamicMsg(Bot bot, AnyMessageEvent event, BilibiliDynamicResp.Data.Card  dynamic) {
        String msg = MsgUtils.builder()
                .text("动态id："+dynamic.getDesc().getDynamic_id()+"\n"+
                        "发布时间："+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dynamic.getDesc().getTimestamp()*1000)+"\n"+
                        "up主："+dynamic.getDesc().getUser_profile().getInfo().getUname()+"\n"+
                        "up主uid："+dynamic.getDesc().getUser_profile().getInfo().getUid()+"\n"+
                        "点赞数："+dynamic.getDesc().getLike()+"\n"+
                        "评论数："+dynamic.getDesc().getComment()+"\n"+
                        "转发数："+dynamic.getDesc().getRepost()+"\n"+
                        "标题（如果是专栏动态）："+JsonToObject(dynamic.getCard()).get("title")+"\n"+
                        "https://www.bilibili.com/opus/"+dynamic.getDesc().getDynamic_id())
                .build();
        bot.sendMsg(event,msg,false);
        return MESSAGE_IGNORE;
    }
    public Map<String,Object> JsonToObject(String json)  {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            log.error("json转Object失败 错误消息{}",e.getMessage());
            return null;
        }
    }
}
