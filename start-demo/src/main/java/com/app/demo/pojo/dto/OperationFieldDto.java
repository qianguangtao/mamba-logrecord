package com.app.demo.pojo.dto;

import com.app.logrecord.enums.EditType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/12/5 11:11
 * @description: 操作字段-dto
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode()
public class OperationFieldDto {

    @NotBlank(message = "操作记录id不能为空")
    @ApiModelProperty(name = "operationId", notes = "操作记录id")
    private String operationId;

    @ApiModelProperty(name = "fieldName", notes = "字段名（英文）")
    private String fieldName;

    @ApiModelProperty(name = "fieldNameShow", notes = "字段名（中文）")
    private String fieldNameShow;

    @ApiModelProperty(name = "changeType", notes = "修改类型;1-新增（null变为有值）；2-编辑（值改变）；3-删除（有值变没值）")
    private EditType changeType;

    @ApiModelProperty(name = "fieldBefore", notes = "字段修改前值")
    private String fieldBefore;

    @ApiModelProperty(name = "fieldBeforeShow", notes = "字段修改前值（字典，枚举等转换后）")
    private String fieldBeforeShow;

    @ApiModelProperty(name = "fieldAfter", notes = "字段修改后值")
    private String fieldAfter;

    @ApiModelProperty(name = "fieldAfterShow", notes = "字段修改后值（字典，枚举等转换后）")
    private String fieldAfterShow;
}
