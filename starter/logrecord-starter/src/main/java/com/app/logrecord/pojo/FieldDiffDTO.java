package com.app.logrecord.pojo;

import com.app.logrecord.enums.EditType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/12/26 11:35
 * @description: 字段比较DTO
 */
@Data
@Accessors(chain = true)
public class FieldDiffDTO implements Serializable {
    /** 字段中文名 */
    private String name;
    /** 字段英文名 */
    private String fieldName;
    /** 字段修改类型 */
    private EditType editType;
    /** 字段旧值 */
    private Object oldValue;
    /** 字段新值 */
    private Object newValue;
    /** 展示的字段旧值（翻译后的值） */
    private Object oldValueShow;
    /** 展示的字段新值（翻译后的值） */
    private Object newValueShow;
}
