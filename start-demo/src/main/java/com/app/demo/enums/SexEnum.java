package com.app.demo.enums;

import com.app.core.mvc.serialization.EnumDefinition;

/**
 * @author qiangt
 */
public enum SexEnum implements EnumDefinition {
    /**
     * 用户类型
     */
    Male("0", "女性"),
    Female("1", "男性"),
    Unknown("2", "未知"),
    ;
    private final String code;
    private final String comment;

    SexEnum(final String code, final String comment) {
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
