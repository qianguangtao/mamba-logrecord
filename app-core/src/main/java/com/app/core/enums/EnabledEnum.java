package com.app.core.enums;

import com.app.core.mvc.serialization.EnumDefinition;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/10/16 15:29
 * @description: 启用|禁用枚举
 */
public enum EnabledEnum implements EnumDefinition {
    /**
     * 启用
     */
    Enabled("1", "启用"),
    /**
     * 禁用
     */
    Disabled("0", "禁用");
    private final String code;
    private final String comment;

    EnabledEnum(final String code, final String comment) {
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
