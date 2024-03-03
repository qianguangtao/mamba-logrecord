package com.app.logrecord.pojo;

import com.app.logrecord.enums.LogOperate;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/12/26 11:34
 * @description: 对象比较结果DTO
 */
@Data
public class ObjectDiffDTO implements Serializable {
    /** 操作业务员id */
    @ApiModelProperty(name = "businessId", notes = "操作业务员id")
    private String businessId;
    /** 操作描述 */
    @ApiModelProperty(name = "description", notes = "操作描述")
    private String description;
    /** 操作人姓名 */
    @ApiModelProperty(name = "operatorName", notes = "操作人姓名")
    private String operatorName;
    /** 操作类型 */
    @ApiModelProperty(name = "logOperate", notes = "操作类型")
    private LogOperate logOperate;
    /** 原类全路径 */
    @ApiModelProperty(name = "oldClassName", notes = "原类全路径")
    private String oldClassName;
    /** 新类全路径 */
    @ApiModelProperty(name = "newClassName", notes = "新类全路径")
    private String newClassName;
    /** 操作前数据json */
    @ApiModelProperty(name = "jsonBefore", notes = "操作前数据json")
    private String jsonBefore;
    /** 操作后数据json */
    @ApiModelProperty(name = "jsonAfter", notes = "操作后数据json")
    private String jsonAfter;
    /** 字段变动list */
    @ApiModelProperty(name = "fieldDiffDTOList", notes = "字段变动list")
    private List<FieldDiffDTO> fieldDiffDTOList;
}
