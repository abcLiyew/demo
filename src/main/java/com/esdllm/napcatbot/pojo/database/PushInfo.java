package com.esdllm.napcatbot.pojo.database;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 记录推送信息表
 * @TableName push_info
 */
@TableName(value ="push_info")
@Data
public class PushInfo implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * b站uid
     */
    private Long roomId;

    /**
     * 群号
     */
    private Long groupId;

    /**
     * qq号
     */
    private Long qqUid;

    /**
     * 是否At全体成员，0-否，1-是
     */
    private Integer atAll;

    /**
     * 需要at的成员，如给at_ll为1，则该列失效，否则其中存放qq号，用逗号分隔
     */
    private String atList;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 逻辑删除
     */
    @TableField
    private Integer isDelete;

    /**
     * 直播间状态，0-未开播，1-直播中，2-轮播中
     */
    private Integer liveStatus;
    /**
     * 开播时间
     */
    private Date liveTime;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = -1156369419169561581L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        PushInfo other = (PushInfo) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getRoomId() == null ? other.getRoomId() == null : this.getRoomId().equals(other.getRoomId()))
            && (this.getGroupId() == null ? other.getGroupId() == null : this.getGroupId().equals(other.getGroupId()))
            && (this.getQqUid() == null ? other.getQqUid() == null : this.getQqUid().equals(other.getQqUid()))
            && (this.getAtAll() == null ? other.getAtAll() == null : this.getAtAll().equals(other.getAtAll()))
            && (this.getAtList() == null ? other.getAtList() == null : this.getAtList().equals(other.getAtList()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getIsDelete() == null ? other.getIsDelete() == null : this.getIsDelete().equals(other.getIsDelete()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getRoomId() == null) ? 0 : getRoomId().hashCode());
        result = prime * result + ((getGroupId() == null) ? 0 : getGroupId().hashCode());
        result = prime * result + ((getQqUid() == null) ? 0 : getQqUid().hashCode());
        result = prime * result + ((getAtAll() == null) ? 0 : getAtAll().hashCode());
        result = prime * result + ((getAtList() == null) ? 0 : getAtList().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getIsDelete() == null) ? 0 : getIsDelete().hashCode());
        return result;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [" +
                "Hash = " + hashCode() +
                ", id=" + id +
                ", bilibiliUid=" + roomId +
                ", groupId=" + groupId +
                ", qqUid=" + qqUid +
                ", atAll=" + atAll +
                ", atList=" + atList +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", isDelete=" + isDelete +
                ", serialVersionUID=" + serialVersionUID +
                "]";
    }
}