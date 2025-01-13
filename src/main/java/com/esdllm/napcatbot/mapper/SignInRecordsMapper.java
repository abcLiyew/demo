package com.esdllm.napcatbot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.esdllm.napcatbot.pojo.database.SignInRecords;
import org.springframework.stereotype.Repository;

/**
* @author LiYehe
* @description 针对表【sign_in_records(签到信息)】的数据库操作Mapper
* @createDate 2025-01-05 14:49:36
* @Entity com.esdllm.napcatbot.pojo.database.SignInRecords
*/
@Repository
public interface SignInRecordsMapper extends BaseMapper<SignInRecords> {
}
