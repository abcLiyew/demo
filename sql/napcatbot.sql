-- napcatbot.admin definition

CREATE TABLE `admin` (
                         `admin_id` int NOT NULL AUTO_INCREMENT COMMENT '管理员id',
                         `qq_uid` bigint NOT NULL COMMENT '管理员的QQ号',
                         `group_id` bigint DEFAULT NULL COMMENT '群号',
                         `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                         `is_delete` tinyint DEFAULT '0' COMMENT '逻辑删除',
                         PRIMARY KEY (`admin_id`)
) ENGINE=InnoDB CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- napcatbot.push_info definition

CREATE TABLE `push_info` (
                             `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
                             `room_id` bigint NOT NULL COMMENT 'b站房间号',
                             `group_id` bigint DEFAULT NULL COMMENT '群号',
                             `qq_uid` bigint DEFAULT NULL COMMENT 'qq号',
                             `at_all` tinyint NOT NULL DEFAULT '0' COMMENT '是否At全体成员，0-否，1-是',
                             `at_list` text COMMENT '需要at的成员，如给at_ll为1，则该列失效，否则其中存放qq号，用逗号分隔',
                             `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             `is_delete` tinyint DEFAULT '0' COMMENT '逻辑删除',
                             `live_status` tinyint NOT NULL DEFAULT '0' COMMENT '开播状态',
                             `live_time` datetime DEFAULT NULL COMMENT '开播时间',
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='记录推送信息表';

-- napcatbot.sign_in_records definition

CREATE TABLE `sign_in_records` (
                                   `sid` int NOT NULL AUTO_INCREMENT COMMENT '签到表id',
                                   `qq_uid` bigint NOT NULL COMMENT 'qq号',
                                   `group_id` bigint DEFAULT NULL COMMENT '群号',
                                   `empirical` double DEFAULT '0' COMMENT '签到的经验值',
                                   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                   `is_delete` tinyint DEFAULT '0' COMMENT '逻辑删除',
                                   PRIMARY KEY (`sid`)
) ENGINE=InnoDB  CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='签到信息';