package com.app.core.mybatis;

import com.app.core.validate.Delete;
import com.app.core.validate.QueryOne;
import com.app.core.validate.Update;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.util.Date;

/**
 * 基础实体，所有的实体都要继承此实体，一些特别的除外
 * @author qiangt
 * @since 2019-12-31
 */
//@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(groups = {QueryOne.class, Update.class, Delete.class}, message = "主键不能为空")
    @Positive
    @ApiModelProperty(value = "主键", example = "1")
    @TableId(value = "id", type = IdType.AUTO)
    protected Long id;

    @JsonIgnore
    @ApiModelProperty(value = "是否可用", example = "YES", position = 1, hidden = true)
    @TableField(value = "deleted")
    protected Boolean deleted;

    @ApiModelProperty(value = "更新日期", position = 2, hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "GMT+8")
    @NotNull(groups = {Update.class, Delete.class}, message = "更新时间不能为空")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    protected Date updateTime;

    @ApiModelProperty(value = "更新人", position = 3, hidden = true)
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    protected Long updateBy;

    @ApiModelProperty(value = "创建日期", position = 4, hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    protected Date createTime;

    @ApiModelProperty(value = "创建人", position = 5, hidden = true)
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    protected Long createBy;

}
