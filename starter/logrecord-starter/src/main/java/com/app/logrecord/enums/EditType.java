package com.app.logrecord.enums;


import com.app.core.mvc.serialization.EnumDefinition;

/**
 * @author qiangt
 */
public enum EditType implements EnumDefinition {
    /**
     * 1-新增（null变为有值）
     */
    SAVE("1", "新增"),
    /**
     * 2-编辑（值改变）
     */
    UPDATE("2", "编辑"),
    /**
     * 3-删除（有值变没值）
     */
    DELETE("3", "删除");

    private final String code;
    private final String comment;

    EditType(final String code, final String comment) {
        this.code = code;
        this.comment = comment;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getComment() {
        return this.comment;
    }

}
