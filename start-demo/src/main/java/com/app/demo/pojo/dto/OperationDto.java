package com.app.demo.pojo.dto;

import com.app.logrecord.enums.LogOperate;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/12/5 11:11
 * @description: 操作记录-dto
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode()
public class OperationDto {

    @NotBlank(message = "业务id不能为空")
    @ApiModelProperty(name = "业务id", notes = "各个业务表的id")
    private String businessId;

    @NotNull(message = "操作类型不能为空")
    @ApiModelProperty(name = "操作类型", notes = "1-申请;2-审核;3-新增;4-更新;5-物理删除")
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

    @NotBlank(message = "操作人姓名不能为空")
    @ApiModelProperty(name = "操作人姓名", notes = "")
    private String operatorName;
}
