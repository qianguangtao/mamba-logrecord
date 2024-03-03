package com.app.dictionary.entity;

import com.app.core.mybatis.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/10/16 15:29
 * @description: 字典entity
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "用户表")
@TableName(value = "dict", autoResultMap = true)
public class Dict extends BaseEntity {

    @ApiModelProperty(value = "字典名称")
    private String dictName;

    @ApiModelProperty(value = "字典编码")
    private String dictCode;

    @ApiModelProperty(value = "描述")
    private String description;

}
