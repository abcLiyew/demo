package com.esdllm.napcatbot.pojo.bilibili;

import lombok.Data;

import java.util.List;

@Data
public class BilibiliDynamicResp {
    private int code;
    private String message;
    private int ttl;
    private String msg;
    private Data data;

    @lombok.Data
    public  class Data {
        private Card card;
        private Long result;
        private Long _gt_;

        @lombok.Data
        public  class Card {
            private Object activity_infos;
            private String card;
            private Desc desc;
            private Object display;
            private String extend_json;

            @lombok.Data
            public  class Desc{
                private Long uid;
                private Integer type;
                private Long rid;
                private Long acl;
                private Long view;
                private Long repost;
                private Integer comment;
                private Long like;
                private Integer is_liked;
                private Long dynamic_id;
                private Long timestamp;
                private Integer pre_dy_id;
                private Integer orig_dy_id;
                private Integer orig_type;
                private UserProfile user_profile;
                private Integer spec_type;
                private Integer uid_type;
                private Integer stype;
                private Integer r_type;
                private Integer inner_id;
                private Integer status;
                private String dynamic_id_str;
                private String pre_dy_id_str;
                private String orig_dy_id_str;
                private String rid_str;
                private Object origin;
                private String bvid;
                private Object previous;

                @lombok.Data
                public  class UserProfile{
                    private Info info;
                    private Object card;
                    private Object vip;
                    private Object official_verify;
                    private Object pendant;
                    private String rank;
                    private String sign;
                    private Object level_info;

                    @lombok.Data
                    public  class Info{
                        private Long uid;
                        private String uname;
                        private String face;
                    }
                }

            }
        }
    }
}
