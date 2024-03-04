package com.app.logrecord.enums;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/3/4 11:01
 * @description: TODO
 */
public enum FieldStrategy {
    /**
     * 默认：
     * 新增-操作前后有变化就入库
     * 更新-前端为null，数据库有值，操作不记录入库
     */
    DEFAULT,
    /**
     * 更新时前端为null，数据库有值，操作记录入库
     */
    UPDATE
}
