package com.app.demo.entity;

import com.app.core.mybatis.BaseEntity;
import com.app.logrecord.enums.EditType;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/12/5 11:11
 * @description: 操作字段-po
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@TableName("operation_field")
public class OperationField extends BaseEntity {

    @ApiModelProperty(name = "操作记录id")
    private Long operationId;

    @ApiModelProperty(name = "字段名（英文）")
    private String fieldName;

    @ApiModelProperty(name = "字段名（中文）")
    private String fieldNameShow;

    @ApiModelProperty(name = "修改类型", notes = "1-新增（null变为有值）；2-编辑（值改变）；3-删除（有值变没值）")
    private EditType changeType;

    @ApiModelProperty(name = "字段修改前值")
    private String fieldBefore;

    @ApiModelProperty(name = "字段修改前值（字典，枚举等转换后）")
    private String fieldBeforeShow;

    @ApiModelProperty(name = "字段修改后值")
    private String fieldAfter;

    @ApiModelProperty(name = "字段修改后值（字典，枚举等转换后）")
    private String fieldAfterShow;
}
