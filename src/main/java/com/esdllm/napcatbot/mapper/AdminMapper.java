package com.esdllm.napcatbot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.esdllm.napcatbot.pojo.database.Admin;
import org.springframework.stereotype.Repository;

/**
* @author LiYehe
* @description 针对表【admin】的数据库操作Mapper
* @createDate 2025-01-05 15:47:52
* @Entity com.esdllm.napcatbot.pojo.database.Admin
*/
@Repository
public interface AdminMapper extends BaseMapper<Admin> {

}
