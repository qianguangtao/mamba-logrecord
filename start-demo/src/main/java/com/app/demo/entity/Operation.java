package com.app.demo.entity;

import com.app.core.mybatis.BaseEntity;
import com.app.logrecord.enums.LogOperate;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/12/5 11:11
 * @description: 操作记录-po
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@TableName("operation")
public class Operation extends BaseEntity {

    @ApiModelProperty(name = "业务id", notes = "各个业务表的id")
    private Long businessId;

    @ApiModelProperty(name = "操作类型", notes = "1-申请;2-审核;3-新增;4-更新;5-删除")
    private LogOperate type;

    @ApiModelProperty(name = "描述", notes = "描述")
    private String description;

    @ApiModelProperty(name = "操作前类全路径", notes = "操作前类全路径 string")
    private String classBefore;

    @ApiModelProperty(name = "操作后类全路径", notes = "操作后类全路径 string")
    private String classAfter;

    @ApiModelProperty(name = "操作前数据json", notes = "保存操作前的json string")
    private String jsonBefore;

    @ApiModelProperty(name = "操作后数据json", notes = "保存操作后的json string")
    private String jsonAfter;

    @ApiModelProperty(name = "操作人姓名")
    private String operatorName;
}
