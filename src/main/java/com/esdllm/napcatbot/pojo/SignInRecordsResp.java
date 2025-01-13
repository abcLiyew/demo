package com.esdllm.napcatbot.pojo;

import com.esdllm.napcatbot.pojo.database.SignInRecords;
import lombok.Data;

@Data
public class SignInRecordsResp {
    private int retCode;
    private String retMsg;
    private Double addEmpirical;
    private SignInRecords signInRecords;
    private String desc;
}
