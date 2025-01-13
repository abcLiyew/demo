package com.esdllm.napcatbot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.esdllm.napcatbot.pojo.database.PushInfo;
import org.springframework.stereotype.Repository;

/**
* @author LiYehe
* @description 针对表【push_info(记录推送信息表)】的数据库操作Mapper
* @createDate 2025-01-05 14:49:20
* @Entity com.esdllm.napcatbot.pojo.database.PushInfo
*/
@Repository
public interface PushInfoMapper extends BaseMapper<PushInfo> {
}
