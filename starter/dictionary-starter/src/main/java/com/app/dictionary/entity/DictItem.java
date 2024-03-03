package com.app.dictionary.entity;

import com.app.core.enums.EnabledEnum;
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
 * @description: 字典项entity
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "字典条目表")
@TableName(value = "dict_item", autoResultMap = true)
public class DictItem extends BaseEntity {

    @ApiModelProperty(value = "字典code")
    private String dictCode;

    @ApiModelProperty(value = "字典key")
    private String itemKey;

    @ApiModelProperty(value = "字典value")
    private String itemValue;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "排序")
    private Integer sortOrder;

    @ApiModelProperty(value = "状态")
    private EnabledEnum enabled;

}
