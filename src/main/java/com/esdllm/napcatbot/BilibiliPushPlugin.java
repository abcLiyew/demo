package com.esdllm.napcatbot;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.esdllm.bilibiliApi.CardInfo;
import com.esdllm.bilibiliApi.LiveRoom;
import com.esdllm.napcatbot.mapper.AdminMapper;
import com.esdllm.napcatbot.mapper.PushInfoMapper;
import com.esdllm.napcatbot.pojo.database.Admin;
import com.esdllm.napcatbot.pojo.database.PushInfo;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static java.lang.Thread.sleep;


@Component
public class BilibiliPushPlugin extends BotPlugin {

    @Resource
    private PushInfoMapper pushInfoMapper;

    @Resource
    private AdminMapper adminMapper;

    @Resource
    private LiveRoom liveRoom;
    @Resource
    private CardInfo cardInfo;


    @Override
    public int onAnyMessage(Bot bot, AnyMessageEvent event) {
        if (event.getMessage().startsWith("添加订阅")) {
            LambdaQueryWrapper<Admin> adminLambdaQueryWrapper = new LambdaQueryWrapper<>();
            adminLambdaQueryWrapper.eq(Admin::getQqUid, event.getUserId());
            if (!Objects.isNull(event.getGroupId())){
                adminLambdaQueryWrapper.eq(Admin::getGroupId, event.getGroupId());
            }
            Admin admin = adminMapper.selectOne(adminLambdaQueryWrapper);
            if (admin==null){
                bot.sendMsg(event,
                        MsgUtils.builder()
                                .at(event.getUserId())
                                .text(" 你没有权限使用该功能").build(), false);
                return MESSAGE_BLOCK;
            }
            String msg = event.getMessage();
            String roomIdStr = msg.substring(4).trim();
            if (roomIdStr.isEmpty()) {
                return MESSAGE_IGNORE;
            }
            Long roomId;
            try {
                roomId = Long.parseLong(roomIdStr);
            } catch (NumberFormatException e) {
                throw new RuntimeException(e);
            }
            PushInfo pushInfoAdd = new PushInfo();
            pushInfoAdd.setRoomId(roomId);
            LambdaQueryWrapper<PushInfo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(PushInfo::getRoomId, roomId);
            if (!Objects.isNull(event.getGroupId())){
                pushInfoAdd.setGroupId(event.getGroupId());
                queryWrapper.eq(PushInfo::getGroupId, 0L);
            }else {
                pushInfoAdd.setQqUid(event.getUserId());
                queryWrapper.eq(PushInfo::getQqUid, event.getUserId());
            }
            PushInfo pushInfo = pushInfoMapper.selectOne(queryWrapper);
            if (pushInfo!=null){
                bot.sendPrivateMsg(event.getUserId(), "你已经订阅过这个房间了", false);
                return MESSAGE_IGNORE;
            }
            int rows = pushInfoMapper.insert(pushInfoAdd);
            if (rows>0) {
                bot.sendPrivateMsg(event.getUserId(),
                        MsgUtils.builder()
                                .at(event.getUserId())
                                .text("开播提醒 " +
                                        cardInfo.getUserName(liveRoom.getUid(roomId)) +
                                        "添加成功").build(), false);
            }else {
                bot.sendPrivateMsg(event.getUserId(),
                        MsgUtils.builder()
                                .at(event.getUserId())
                                .text(
                                        "添加失败").build(), false);
            }
            return MESSAGE_IGNORE;
        }

        if (event.getMessage().startsWith("取消订阅")){
            LambdaQueryWrapper<Admin> adminLambdaQueryWrapper = new LambdaQueryWrapper<>();
            adminLambdaQueryWrapper.eq(Admin::getQqUid, event.getUserId());
            if (!Objects.isNull(event.getGroupId())){
                adminLambdaQueryWrapper.eq(Admin::getGroupId, event.getGroupId());
            }
            Admin admin = adminMapper.selectOne(adminLambdaQueryWrapper);
            if (admin==null){
                bot.sendMsg(event,
                        MsgUtils.builder()
                                .at(event.getUserId())
                                .text(" 你没有权限使用该功能").build(), false);
                return MESSAGE_BLOCK;
            }
            String msg = event.getMessage();
            String roomIdStr = msg.substring(4).trim();
            if (roomIdStr.isEmpty()) {
                return MESSAGE_IGNORE;
            }
            Long roomId;
            try {
                roomId = Long.parseLong(roomIdStr);
            } catch (NumberFormatException e) {
                throw new RuntimeException(e);
            }
            PushInfo pushInfoDel = new PushInfo();
            pushInfoDel.setRoomId(roomId);
            LambdaQueryWrapper<PushInfo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(PushInfo::getRoomId, roomId);
            if (!Objects.isNull(event.getGroupId())){
                pushInfoDel.setGroupId(event.getGroupId());
                queryWrapper.eq(PushInfo::getGroupId, 0L);
            }else {
                pushInfoDel.setQqUid(event.getUserId());
                queryWrapper.eq(PushInfo::getQqUid, event.getUserId());
            }
            PushInfo pushInfo = pushInfoMapper.selectOne(queryWrapper);
            if (pushInfo==null){
                bot.sendPrivateMsg(event.getUserId(), "你没有订阅过这个房间", false);
                return MESSAGE_IGNORE;
            }
            int rows = pushInfoMapper.delete(queryWrapper);
            if (rows>0) {
                bot.sendPrivateMsg(event.getUserId(),
                        MsgUtils.builder()
                                .at(event.getUserId())
                                .text(cardInfo.getUserName(liveRoom.getUid(roomId)) +
                                        "取消开播提醒成功").build(), false);
            }else {
                bot.sendPrivateMsg(event.getUserId(),
                        MsgUtils.builder()
                                .at(event.getUserId())
                                .text("取消开播提醒失败").build(), false);
            }
        }
        return MESSAGE_IGNORE;
    }


    private int LivePush(Bot bot) throws IOException {
        List<PushInfo> pushInfosList = pushInfoMapper.selectList(null);
        //构建消息
        String sendMsg = null;
        for (PushInfo pushInfo : pushInfosList) {
            if (liveRoom.getLiveStatus(pushInfo.getRoomId())!=pushInfo.getLiveStatus()) {
                if (pushInfo.getLiveStatus()==0) {
                    if (pushInfo.getAtAll() == 1){
                    sendMsg = MsgUtils.builder().atAll()
                            .text(cardInfo.getUserName(liveRoom.getUid(pushInfo.getRoomId()))+" 开播了"+
                                    "\n标题："+liveRoom.getLiveTitle(pushInfo.getRoomId())+"\n"+
                                    "分区："+liveRoom.getLiveArea(pushInfo.getRoomId())+"\n"+
                                    "地址："+liveRoom.getLiveUrl(pushInfo.getRoomId())+"\n"+
                                    "[CQ:image,file="+liveRoom.getImageUrl(pushInfo.getRoomId())+"]")
                            .build();
                    }else if (pushInfo.getAtList()!=null){
                        String[] atList = pushInfo.getAtList().split(",");
                        Long[] atListLong = new Long[atList.length];
                        for (int i = 0; i < atList.length; i++) {
                            atListLong[i] = Long.parseLong(atList[i]);
                        }
                        MsgUtils builder = MsgUtils.builder();
                        for (Long l : atListLong) {
                            builder = builder.at(l);
                        }
                        sendMsg = builder.text(
                                cardInfo.getUserName(liveRoom.getUid(pushInfo.getRoomId()))+" 开播了"+
                                        "\n标题："+liveRoom.getLiveTitle(pushInfo.getRoomId())+"\n"+
                                        "分区："+liveRoom.getLiveArea(pushInfo.getRoomId())+"\n"+
                                        "地址："+liveRoom.getLiveUrl(pushInfo.getRoomId())+"\n"+
                                        "[CQ:image,file="+liveRoom.getImageUrl(pushInfo.getRoomId())+"]")
                                .build();
                    } else{
                        sendMsg = MsgUtils.builder()
                                .text(cardInfo.getUserName(liveRoom.getUid(pushInfo.getRoomId()))+" 开播了"+
                                        "\n标题："+liveRoom.getLiveTitle(pushInfo.getRoomId())+"\n"+
                                        "分区："+liveRoom.getLiveArea(pushInfo.getRoomId())+"\n"+
                                        "地址："+liveRoom.getLiveUrl(pushInfo.getRoomId())+"\n"+
                                        "[CQ:image,file="+liveRoom.getImageUrl(pushInfo.getRoomId())+"]")
                                .build();
                    }
                    pushInfo.setLiveStatus(1);
                }else {
                    sendMsg = MsgUtils.builder()
                           .text(cardInfo.getUserName(liveRoom.getUid(pushInfo.getRoomId()))+" 下播了")
                           .build();
                    pushInfo.setLiveStatus(0);
                }
                pushInfo.setUpdateTime(null);
                pushInfoMapper.updateById(pushInfo);
                //发送消息
                if (Objects.isNull(pushInfo.getGroupId())){
                    bot.sendPrivateMsg(pushInfo.getQqUid(), sendMsg, false);
                }else {
                    bot.sendGroupMsg(pushInfo.getGroupId(), sendMsg, false);
                }
            }

        }
        return 0;
    }
    public void onTimer(Bot bot) throws IOException, InterruptedException {
        while (true) {
            int i;

            i = LivePush(bot);

            if (i != 0) {
                break;
            }
            sleep(10000);
        }
    }

}
