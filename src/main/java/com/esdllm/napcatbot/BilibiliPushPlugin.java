package com.esdllm.napcatbot;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.esdllm.bilibiliApi.CardInfo;
import com.esdllm.bilibiliApi.Live;
import com.esdllm.napcatbot.mapper.AdminMapper;
import com.esdllm.napcatbot.mapper.PushInfoMapper;
import com.esdllm.napcatbot.pojo.database.Admin;
import com.esdllm.napcatbot.pojo.database.PushInfo;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.enums.AtEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Thread.sleep;


@Slf4j
@Component
public class BilibiliPushPlugin extends BotPlugin {

    @Resource
    private PushInfoMapper pushInfoMapper;

    @Resource
    private AdminMapper adminMapper;

    @Resource
    private Live liveRoom;
    @Resource
    private CardInfo cardInfo;


    @Override
    @MessageHandlerFilter(at = AtEnum.BOTH)
    public int onAnyMessage(Bot bot, AnyMessageEvent event) {
        String message = event.getMessage();
        message = message.replaceAll("\\[CQ:[^]]*]", "");
        message = message.trim();
        if (message.startsWith("添加订阅")) {
            //鉴权
            if (isAdmin(bot, event)){
                return MESSAGE_IGNORE;
            }
            //解析房间号
            String roomIdStr = message.substring(4).trim();
            if (roomIdStr.isEmpty()) {
                bot.sendMsg(event, "房间号不能为空", false);
                return MESSAGE_IGNORE;
            }
            Long roomId;
            try {
                roomId = Long.parseLong(roomIdStr);
            } catch (NumberFormatException e) {
                throw new RuntimeException(e);
            }
            //判断是否已经订阅过
            PushInfo pushInfoAdd = new PushInfo();
            LambdaQueryWrapper<PushInfo> queryWrapper = new LambdaQueryWrapper<>();
            pushInfoAdd.setRoomId(roomId);
            queryWrapper.eq(PushInfo::getRoomId, roomId);
            if (!Objects.isNull(event.getGroupId())) {
                pushInfoAdd.setGroupId(event.getGroupId());
                queryWrapper.eq(PushInfo::getGroupId, event.getGroupId());
            } else {
                pushInfoAdd.setQqUid(event.getUserId());
                queryWrapper.eq(PushInfo::getQqUid, event.getUserId());
            }
            PushInfo pushInfo = pushInfoMapper.selectOne(queryWrapper);
            if (pushInfo != null) {
                bot.sendMsg(event, "你已经订阅过这个房间了", false);
                return MESSAGE_IGNORE;
            }
            int rows = pushInfoMapper.insert(pushInfoAdd);
            if (rows > 0) {
                bot.sendMsg(event,
                        MsgUtils.builder()
                                .at(event.getUserId())
                                .text("开播提醒 " +
                                        cardInfo.getUserName(liveRoom.getUid(roomId)) +
                                        "添加成功").build(), false);
            } else {
                bot.sendMsg(event,
                        MsgUtils.builder()
                                .at(event.getUserId())
                                .text(
                                        "添加失败").build(), false);
            }
            return MESSAGE_IGNORE;
        }

        if (message.startsWith("取消订阅")) {
            if (isAdmin(bot,event)){
                return MESSAGE_BLOCK;
            }
            String roomIdStr = message.substring(4).trim();
            if (roomIdStr.isEmpty()) {
                bot.sendMsg(event, "房间号不能为空", false);
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
            if (!Objects.isNull(event.getGroupId())) {
                pushInfoDel.setGroupId(event.getGroupId());
                queryWrapper.eq(PushInfo::getGroupId, event.getGroupId());
            } else {
                pushInfoDel.setQqUid(event.getUserId());
                queryWrapper.eq(PushInfo::getQqUid, event.getUserId());
            }
            PushInfo pushInfo = pushInfoMapper.selectOne(queryWrapper);
            if (pushInfo == null) {
                bot.sendMsg(event, "你没有订阅过这个房间", false);
                return MESSAGE_IGNORE;
            }
            int rows = pushInfoMapper.delete(queryWrapper);
            if (rows > 0) {
                bot.sendMsg(event,
                        MsgUtils.builder()
                                .at(event.getUserId())
                                .text(cardInfo.getUserName(liveRoom.getUid(roomId)) +
                                        "取消开播提醒成功").build(), false);
            } else {
                bot.sendMsg(event,
                        MsgUtils.builder()
                                .at(event.getUserId())
                                .text("取消开播提醒失败").build(), false);
            }
        }
        if (message.startsWith("开播@我")) {
            if (Objects.isNull(event.getGroupId())) {
                bot.sendMsg(event, "请在群聊中使用", false);
                return MESSAGE_IGNORE;
            }
            message = message.substring(4).trim();
            Long roomId = null;
            if (!message.isEmpty()) {
                try {
                    roomId = Long.parseLong(message);
                } catch (NumberFormatException e) {
                    bot.sendMsg(event, MsgUtils.builder().at(event.getUserId()).text("房间号格式错误").build(), false);
                    return MESSAGE_IGNORE;
                }
            }
            return liveAtMe(bot, event, roomId);
        }
        if (message.startsWith("取消开播@我")) {
            if (Objects.isNull(event.getGroupId())) {
                bot.sendMsg(event, "请在群聊中使用", false);
                return MESSAGE_IGNORE;
            }
            message = message.substring(6).trim();
            Long roomId = null;
            if (!message.isEmpty()) {
                try {
                    roomId = Long.parseLong(message);
                }catch (NumberFormatException e) {
                    bot.sendMsg(event, MsgUtils.builder().at(event.getUserId()).text("房间号格式错误").build(), false);
                    return MESSAGE_IGNORE;
                }
            }
            return liveAtMeCancel(bot, event, roomId);
        }
        if (message.startsWith("开播@全体成员")){
            //鉴权
            if (isAdmin(bot, event)) return MESSAGE_BLOCK;
            if (Objects.isNull(event.getGroupId())) {
                bot.sendMsg(event, "请在群聊中使用", false);
            }
            message = message.substring(7).trim();
            Long roomId = null;
            if (!message.isEmpty()) {
                try {
                    roomId = Long.parseLong(message);
                }catch (NumberFormatException e) {
                    bot.sendMsg(event, MsgUtils.builder().at(event.getUserId()).text("房间号格式错误").build(), false);
                    return MESSAGE_IGNORE;
                }
            }
            return liveAtAll(bot, event, roomId);
        }
        if (message.startsWith("取消开播@全体成员")){
            //鉴权
            if (isAdmin(bot, event)) return MESSAGE_BLOCK;
            if (Objects.isNull(event.getGroupId())) {
                bot.sendMsg(event, "请在群聊中使用", false);
            }
            message = message.substring(9).trim();
            Long roomId = null;
            if (!message.isEmpty()) {
                try {
                    roomId = Long.parseLong(message);
                }catch (NumberFormatException e) {
                    bot.sendMsg(event, MsgUtils.builder().at(event.getUserId()).text("房间号格式错误").build(), false);
                    return MESSAGE_IGNORE;
                }
            }
            return liveAtAllCancel(bot, event, roomId);
        }
        return MESSAGE_IGNORE;
    }

    private boolean isAdmin(Bot bot, AnyMessageEvent event) {
        LambdaQueryWrapper<Admin> adminLambdaQueryWrapper = new LambdaQueryWrapper<>();
        adminLambdaQueryWrapper.eq(Admin::getQqUid, event.getUserId());
        List<Admin> adminList = adminMapper.selectList(adminLambdaQueryWrapper);
        Admin admin = null;
        for (Admin a : adminList) {
            if (Objects.isNull(a.getGroupId())){
                admin = a;
                break;
            }
            if (a.getGroupId().equals(event.getGroupId())){
                admin = a;
                break;
            }
        }
        if (admin == null && !Objects.isNull(event.getGroupId()) && !(event.getSender().getRole().equals("owner")) && !(event.getSender().getRole().equals("admin"))) {
            bot.sendMsg(event,
                    MsgUtils.builder()
                            .at(event.getUserId())
                            .text(" 只有管理员才可以这么做").build(), false);
            return true;
        }
        return false;
    }

    private int liveAtAllCancel(Bot bot, AnyMessageEvent event, Long roomId) {
        LambdaQueryWrapper<PushInfo> queryWrapper = new LambdaQueryWrapper<>();
        if (Objects.isNull(event.getGroupId())){
            queryWrapper.isNull(PushInfo::getGroupId);
            queryWrapper.eq(PushInfo::getQqUid,event.getUserId());
        }else {
            queryWrapper.eq(PushInfo::getGroupId,event.getGroupId());
        }
        List<PushInfo> pushInfoList = pushInfoMapper.selectList(queryWrapper);
        if (pushInfoList.isEmpty()){
            bot.sendMsg(event,"该群没有订阅任何直播间",false);
            return MESSAGE_IGNORE;
        }
        if (pushInfoList.size()==1) {
            if (roomId != null && !roomId.equals(pushInfoList.get(0).getRoomId())) {
                bot.sendMsg(event, MsgUtils.builder().at(event.getUserId())
                      .text(" 没有订阅该直播间").build(), false);
                return MESSAGE_IGNORE;
            }
            if (pushInfoList.get(0).getAtAll() == 0) {
                bot.sendMsg(event,MsgUtils.builder().at(event.getUserId())
                     .text(" 没有开播@全体成员").build(), false);
                return MESSAGE_IGNORE;
            }
            pushInfoList.get(0).setAtAll(0);
            pushInfoMapper.updateById(pushInfoList.get(0));
            bot.sendMsg(event,MsgUtils.builder().at(event.getUserId())
                 .text(" 取消开播@全体成员成功").build(), false);
            return MESSAGE_IGNORE;
        }
        if (roomId == null){
            bot.sendMsg(event,MsgUtils.builder().at(event.getUserId()).text("请输入房间号").build(),false);
            return MESSAGE_IGNORE;
        }
        for (PushInfo pushInfo : pushInfoList) {
            if (roomId.equals(pushInfo.getRoomId())) {
                if (pushInfo.getAtAll() == 0) {
                    bot.sendMsg(event,MsgUtils.builder().at(event.getUserId())
                         .text(" 没有开播@全体成员").build(), false);
                    return MESSAGE_IGNORE;
                }
                pushInfo.setAtAll(0);
                pushInfoMapper.updateById(pushInfo);
                String sendMsg = MsgUtils.builder().at(event.getUserId()).
                        text(new CardInfo().getUserName(liveRoom.getUid(pushInfo.getRoomId()))+
                                " 取消开播@全体成员成功").build();
                bot.sendMsg(event,sendMsg,false);
            }
        }
        bot.sendMsg(event,MsgUtils.builder().at(event.getUserId()).text(" 没有订阅该直播间").build(),false);

        return MESSAGE_IGNORE;
    }

    private int liveAtAll(Bot bot, AnyMessageEvent event, Long roomId) {
        LambdaQueryWrapper<PushInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PushInfo::getGroupId,event.getGroupId());
        List<PushInfo> pushInfoList = pushInfoMapper.selectList(queryWrapper);
        if (pushInfoList.isEmpty()){
            bot.sendMsg(event,"该群没有订阅任何直播间",false);
            return MESSAGE_IGNORE;
        }
        if (pushInfoList.size()==1) {
            if (roomId!= null &&!roomId.equals(pushInfoList.get(0).getRoomId())) {
                bot.sendMsg(event, MsgUtils.builder().at(event.getUserId())
                      .text(" 没有订阅该直播间").build(), false);
                return MESSAGE_IGNORE;
            }
            if (pushInfoList.get(0).getAtAll() == 1) {
                bot.sendMsg(event,MsgUtils.builder().at(event.getUserId())
                      .text(" 已经开播@全体成员了").build(), false);
                return MESSAGE_IGNORE;
            }
            pushInfoList.get(0).setAtAll(1);
            pushInfoMapper.updateById(pushInfoList.get(0));
            bot.sendMsg(event,MsgUtils.builder().at(event.getUserId())
                 .text(" 添加开播@全体成员成功").build(), false);
            return MESSAGE_IGNORE;
        }
        if (roomId == null){
            bot.sendMsg(event,MsgUtils.builder().at(event.getUserId()).text(" 请输入房间号").build(),false);
            return MESSAGE_IGNORE;
        }
        for (PushInfo pushInfo : pushInfoList) {
            if (roomId.equals(pushInfo.getRoomId())) {
                if (pushInfo.getAtAll() == 1) {
                    bot.sendMsg(event,MsgUtils.builder().at(event.getUserId())
                         .text(" 已经开播@全体成员了").build(), false);
                    return MESSAGE_IGNORE;
                }
                pushInfo.setAtAll(1);
                pushInfoMapper.updateById(pushInfo);
                String sendMsg = MsgUtils.builder().at(event.getUserId()).
                        text(new CardInfo().getUserName(liveRoom.getUid(pushInfo.getRoomId()))+
                                " 添加开播@全体成员成功").build();
                bot.sendMsg(event,sendMsg,false);
            }
        }
        bot.sendMsg(event,MsgUtils.builder().at(event.getUserId()).text(" 你没有订阅该直播间").build(),false);

        return MESSAGE_IGNORE;
    }

    private int liveAtMeCancel(Bot bot, AnyMessageEvent event, Long roomId) {
        LambdaQueryWrapper<PushInfo> queryWrapper = new LambdaQueryWrapper<>();
        if (Objects.isNull(event.getGroupId())){
            queryWrapper.isNull(PushInfo::getGroupId);
            queryWrapper.eq(PushInfo::getQqUid,event.getUserId());
        }else {
            queryWrapper.eq(PushInfo::getGroupId,event.getGroupId());
        }
        List<PushInfo> pushInfoList = pushInfoMapper.selectList(queryWrapper);
        if (pushInfoList.isEmpty()){
            bot.sendMsg(event,"该群没有订阅任何直播间",false);
            return MESSAGE_IGNORE;
        }
        if (pushInfoList.size()==1) {
            if (roomId != null && !roomId.equals(pushInfoList.get(0).getRoomId())) {
                bot.sendMsg(event, MsgUtils.builder().at(event.getUserId())
                        .text(" 你没有订阅该直播间").build(), false);
                return MESSAGE_IGNORE;
            }
            List<String> atList = new ArrayList<>();
            Collections.addAll(atList, pushInfoList.get(0).getAtList().split(","));
            String removeStr = event.getUserId().toString();
            for (String s : atList) {
                if (s.equals(removeStr)){
                    atList.remove(s);
                    pushInfoList.get(0).setAtList(String.join(",",atList));
                    pushInfoMapper.updateById(pushInfoList.get(0));
                    bot.sendMsg(event,MsgUtils.builder().at(event.getUserId()).text(" 取消开播@你成功").build(),false);
                    return MESSAGE_IGNORE;
                }
            }
            bot.sendMsg(event, MsgUtils.builder().at(event.getUserId())
                    .text(" 你没有订阅该直播间").build(), false);
            return MESSAGE_IGNORE;
        }
        if (roomId == null){
            bot.sendMsg(event,MsgUtils.builder().at(event.getUserId()).text(" 请输入房间号").build(),false);
            return MESSAGE_IGNORE;
        }
        for (PushInfo pushInfo : pushInfoList) {
            if (roomId.equals(pushInfo.getRoomId())){
                List<String> atList = new ArrayList<>();
                Collections.addAll(atList, pushInfoList.get(0).getAtList().split(","));
                String removeStr = event.getUserId().toString();
                for (String s : atList){
                    if (s.equals(removeStr)){
                        atList.remove(s);
                        break;
                    }
                }
                pushInfo.setAtList(String.join(",",atList));
                pushInfoMapper.updateById(pushInfo);
                String sendMsg = MsgUtils.builder().at(event.getUserId()).
                        text(new CardInfo().getUserName(liveRoom.getUid(pushInfo.getRoomId()))+
                                " 取消开播@你添加成功").build();
                bot.sendMsg(event,sendMsg,false);
                return MESSAGE_IGNORE;
            }
        }
        bot.sendMsg(event,MsgUtils.builder().at(event.getUserId()).text(" 你没有订阅该直播间").build(),false);
        return MESSAGE_IGNORE;
    }


    private int LivePush(Bot bot) throws IOException {
        List<PushInfo> pushInfosList = pushInfoMapper.selectList(null);
        //构建消息
        String sendMsg = null;
        for (PushInfo pushInfo : pushInfosList) {
            if (liveRoom.getLiveStatus(pushInfo.getRoomId())!=pushInfo.getLiveStatus()) {
                if (pushInfo.getLiveStatus()==0) {
                    Date liveTime = null;
                    SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        liveTime=formatter.parse(liveRoom.getLiveTime(pushInfo.getRoomId()));
                    } catch (ParseException e) {
                        log.info("解析开播时间失败");
                    }
                    pushInfo.setLiveTime(liveTime);
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
                    Date now = new Date();
                    long between = now.getTime() - pushInfo.getLiveTime().getTime();
                    long hour = (between / (60 * 60 * 1000));
                    long minute = ((between / (60 * 1000)) % 60);
                    long second = ((between / 1000) % 60);
                    String time = hour==0?(minute==0?second+"秒":minute+"分"+second+"秒"):(hour+"小时"+minute+"分"+second+"秒");
                    sendMsg = MsgUtils.builder()
                           .text(cardInfo.getUserName(liveRoom.getUid(pushInfo.getRoomId()))+" 下播了"+
                                   "\n直播时长："+time)
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
            int i=0;

            try {
                i = LivePush(bot);
            } catch (IOException e) {
                log.info(e.getMessage());
            }

            if (i != 0) {
                break;
            }
            sleep(1000);
        }
    }
    private int liveAtMe(Bot bot,AnyMessageEvent event,Long roomId){
        LambdaQueryWrapper<PushInfo> queryWrapper = new LambdaQueryWrapper<>();
        if (Objects.isNull(event.getGroupId())){
            queryWrapper.eq(PushInfo::getGroupId,event.getUserId());
            queryWrapper.isNull(PushInfo::getGroupId);
        }else {
            queryWrapper.eq(PushInfo::getGroupId,event.getGroupId());
        }
        List<PushInfo> pushInfoList = pushInfoMapper.selectList(queryWrapper);
        if (pushInfoList.isEmpty()){
            bot.sendMsg(event," 该群没有订阅任何直播间",false);
            return MESSAGE_IGNORE;
        }
        if (pushInfoList.size()==1){
            if (roomId !=null&& !roomId.equals(pushInfoList.get(0).getRoomId())){
                bot.sendMsg(event,MsgUtils.builder().at(event.getUserId())
                        .text("你没有订阅该直播间").build(),false);
                return MESSAGE_IGNORE;
            }
            if (pushInfoList.get(0).getAtAll()==1){
                String sendMsg = MsgUtils.builder().at(event.getUserId()).
                        text(" 开播提醒已经@全体成员了！").build();
                bot.sendMsg(event,sendMsg,false);
                return MESSAGE_IGNORE;
            }
            PushInfo pushInfo = pushInfoList.get(0);
            String atList = pushInfo.getAtList();
            if (atList==null){
                atList = "";
            }
            if (atList.contains(event.getUserId().toString())){
                String sendMsg = MsgUtils.builder().at(event.getUserId()).
                        text(" 现在已经开播@你了！").build();
                bot.sendMsg(event,sendMsg,false);
                return MESSAGE_IGNORE;
            }
            atList = atList + ","+event.getUserId();
            pushInfo.setAtList(atList);
            pushInfoMapper.updateById(pushInfo);
            String sendMsg = MsgUtils.builder().at(event.getUserId()).
                    text(new CardInfo().getUserName(liveRoom.getUid(pushInfo.getRoomId()))+
                            " 开播@你添加成功").build();
            bot.sendMsg(event,sendMsg,false);
            return MESSAGE_IGNORE;
        }
        if (roomId == null){
            bot.sendMsg(event,MsgUtils.builder().at(event.getUserId()).text(" 该群订阅了多个直播间，请指定房间号").build(),false);
            return MESSAGE_IGNORE;
        }
        for (PushInfo pushInfo : pushInfoList) {
            if (roomId.equals(pushInfo.getRoomId())){
                if (pushInfo.getAtAll()==1){
                    String sendMsg = MsgUtils.builder().at(event.getUserId()).
                            text(" 开播提醒已经@全体成员了！").build();
                    bot.sendMsg(event,sendMsg,false);
                    return MESSAGE_IGNORE;
                }
                if (pushInfo.getAtList().contains(event.getUserId().toString())){
                    String sendMsg = MsgUtils.builder().at(event.getUserId()).
                            text(" 现在已经开播@你了！").build();
                    bot.sendMsg(event,sendMsg,false);
                    return MESSAGE_IGNORE;
                }
                pushInfo.setAtList(pushInfo.getAtList()+","+event.getUserId());
                pushInfoMapper.updateById(pushInfo);
                String sendMsg = MsgUtils.builder().at(event.getUserId()).
                        text(new CardInfo().getUserName(liveRoom.getUid(pushInfo.getRoomId()))+
                                " 开播@你添加成功").build();
                bot.sendMsg(event,sendMsg,false);
                return MESSAGE_IGNORE;
            }
        }
        bot.sendMsg(event,MsgUtils.builder().at(event.getUserId()).text(" 没有订阅该直播间").build(),false);
        return MESSAGE_IGNORE;
    }
}
