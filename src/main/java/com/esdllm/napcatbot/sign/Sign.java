package com.esdllm.napcatbot.sign;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.esdllm.napcatbot.mapper.SignInRecordsMapper;
import com.esdllm.napcatbot.pojo.SignInRecordsResp;
import com.esdllm.napcatbot.pojo.database.SignInRecords;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@Slf4j
@Component
public class Sign {
    @Resource
    private SignInRecordsMapper signInRecordsMapper;

    private SignInRecordsResp buildResp(SignInRecords signInRecords){
        SignInRecordsResp resp = new SignInRecordsResp();
        resp.setRetCode(0);
        resp.setAddEmpirical(0.00);
        resp.setRetMsg(" 签到成功");
        resp.setDesc("");
        resp.setSignInRecords(signInRecords);
        return resp;
    }

    private SignInRecordsResp isToday(SignInRecords signInRecords) {
        Date updateTime = signInRecords.getUpdateTime();
        Date currenDate = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String todayDate = sdf.format(currenDate);
        String updateDate = sdf.format(updateTime);
        if (updateDate.equals(todayDate)&&signInRecords.getEmpirical()!=0) {
            SignInRecordsResp resp = buildResp(signInRecords);
            resp.setRetCode(1);
            resp.setRetMsg(" 签到失败");
            resp.setDesc(" 今日已签到");
            return resp;
        } else {
            return getSignInRecordsResp(signInRecords);
        }
    }

    public SignInRecordsResp onAnySignIn(AnyMessageEvent event) {
        LambdaQueryWrapper<SignInRecords> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SignInRecords::getQqUid, event.getUserId());
        if (!Objects.isNull(event.getGroupId())) {
            queryWrapper.eq(SignInRecords::getGroupId, event.getGroupId());
        }else {
            queryWrapper.isNull(SignInRecords::getGroupId);
        }
        SignInRecords signInRecordsOne = signInRecordsMapper.selectOne(queryWrapper);

        if (signInRecordsOne != null) {
            return isToday(signInRecordsOne);
        }else {
            signInRecordsOne = new SignInRecords();
            signInRecordsOne.setQqUid(event.getUserId());
            signInRecordsOne.setGroupId(event.getGroupId());

            signInRecordsMapper.insert(signInRecordsOne);
        }
        return getSignInRecordsResp(signInRecordsOne);
    }

    private SignInRecordsResp getSignInRecordsResp(SignInRecords signInRecords) {
        Double addEmpirical = signStatus(signInRecords);
        if(addEmpirical>0){
            SignInRecordsResp resp = buildResp(signInRecords);
            resp.setAddEmpirical(addEmpirical);
            return resp;
        }
        SignInRecordsResp resp = buildResp(signInRecords);
        resp.setRetCode(1);
        resp.setRetMsg(" 签到失败");
        resp.setDesc(" 系统内部错误");
        resp.setSignInRecords(null);
        log.error("系统错误，签到失败,addEmpirical = {}", addEmpirical);
        return resp;
    }

    Double signStatus(SignInRecords signInRecords){
        double random = Math.random()*2;
        if (signInRecords.getEmpirical()==null){
            signInRecords.setEmpirical(0.0);
        }
        double empirical = signInRecords.getEmpirical()+random;
        BigDecimal two = new BigDecimal(empirical);
        signInRecords.setEmpirical(two.setScale(2, RoundingMode.HALF_UP).doubleValue());
        signInRecords.setUpdateTime(null);
        int i = signInRecordsMapper.updateById(signInRecords);
        if(i>0){
            return new BigDecimal(random).setScale(2, RoundingMode.HALF_UP).doubleValue();
        }else {
            throw new RuntimeException(" 签到失败");
        }
    }

    public Double getEmpirical(Long userId, Long groupId) {
        LambdaQueryWrapper<SignInRecords> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SignInRecords::getQqUid,userId);
        if(groupId==null){
            queryWrapper.isNull(SignInRecords::getGroupId);
        }else {
            queryWrapper.eq(SignInRecords::getGroupId,groupId);
        }
        SignInRecords signInRecords = signInRecordsMapper.selectOne(queryWrapper);
        if(signInRecords!=null){
            return signInRecords.getEmpirical();
        }else {
            return 0.0;
        }
    }
    public Integer getUid(Long userId,Long groupId){
        LambdaQueryWrapper<SignInRecords> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SignInRecords::getQqUid,userId);
        if (Objects.isNull(groupId)){
            queryWrapper.isNull(SignInRecords::getGroupId);
        }else {
            queryWrapper.eq(SignInRecords::getGroupId,groupId);
        }
        SignInRecords signInRecords = signInRecordsMapper.selectOne(queryWrapper);
        if (signInRecords == null){
            SignInRecords addSignInRecords = new SignInRecords();
            addSignInRecords.setQqUid(userId);
            addSignInRecords.setGroupId(groupId);
            signInRecordsMapper.insert(addSignInRecords);
            return addSignInRecords.getSid();
        }else {
            return signInRecords.getSid();
        }
    }
}
