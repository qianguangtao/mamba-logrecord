package com.app.logrecord.enums;

import cn.hutool.core.util.StrUtil;
import com.app.core.mvc.serialization.EnumDefinition;

import java.util.Objects;

/**
 * @author qiangt
 */
public enum LogOperate implements EnumDefinition {
    /**
     * 1-申请
     */
    APPLY(Type.APPLY, "申请"),
    /**
     * 2-审批
     */
    APPROVE(Type.APPROVE, "审批"),
    /**
     * 3-新增
     */
    SAVE(Type.SAVE, "新增"),
    /**
     * 4-更新
     */
    UPDATE(Type.UPDATE, "更新"),
    /**
     * 5-删除
     */
    DELETE(Type.DELETE, "删除"),
    /**
     * 9-位置
     */
    UNKNOWN("9", "位置");

    private final String code;
    private final String comment;

    LogOperate(final String code, final String comment) {
        this.code = code;
        this.comment = comment;
    }

    public static LogOperate getLogOperateByCode(String code) {
        if (StrUtil.isEmpty(code)) {
            return LogOperate.UNKNOWN;
        }
        for (LogOperate logOperate : LogOperate.values()) {
            if (Objects.equals(code, logOperate.getCode())) {
                return logOperate;
            }
        }
        return LogOperate.UNKNOWN;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getComment() {
        return this.comment;
    }

    public interface Type {
        /**
         * 1-申请
         */
        String APPLY = "1";
        /**
         * 2-审批
         */
        String APPROVE = "2";
        /**
         * 3-新增
         */
        String SAVE = "3";
        /**
         * 4-更新
         */
        String UPDATE = "4";
        /**
         * 5-物理删除
         */
        String DELETE = "5";

    }

}
