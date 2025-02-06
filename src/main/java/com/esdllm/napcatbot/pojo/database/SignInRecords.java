package com.esdllm.napcatbot.pojo.database;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 签到信息
 * @TableName sign_in_records
 */
@TableName(value ="sign_in_records")
@Data
public class SignInRecords implements Serializable {
    /**
     * 签到表id
     */
    @TableId(type = IdType.AUTO)
    private Integer sid;

    /**
     * qq号
     */
    private Long qqUid;

    /**
     * 群号
     */
    private Long groupId;

    /**
     * 签到的经验值
     */
    private Double empirical;

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
    @TableLogic
    private Integer isDelete;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = -1908324245514730809L;

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
        SignInRecords other = (SignInRecords) that;
        return (this.getSid() == null ? other.getSid() == null : this.getSid().equals(other.getSid()))
            && (this.getQqUid() == null ? other.getQqUid() == null : this.getQqUid().equals(other.getQqUid()))
            && (this.getGroupId() == null ? other.getGroupId() == null : this.getGroupId().equals(other.getGroupId()))
            && (this.getEmpirical() == null ? other.getEmpirical() == null : this.getEmpirical().equals(other.getEmpirical()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getIsDelete() == null ? other.getIsDelete() == null : this.getIsDelete().equals(other.getIsDelete()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getSid() == null) ? 0 : getSid().hashCode());
        result = prime * result + ((getQqUid() == null) ? 0 : getQqUid().hashCode());
        result = prime * result + ((getGroupId() == null) ? 0 : getGroupId().hashCode());
        result = prime * result + ((getEmpirical() == null) ? 0 : getEmpirical().hashCode());
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
                ", sid=" + sid +
                ", qqUid=" + qqUid +
                ", groupId=" + groupId +
                ", empirical=" + empirical +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", isDelete=" + isDelete +
                ", serialVersionUID=" + serialVersionUID +
                "]";
    }
}