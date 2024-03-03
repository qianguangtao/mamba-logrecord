package com.app.logrecord.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/12/25 13:44
 * @description: 操作记录实体类注解，用在带有@LogRecord注解的方法入参，作为操作记录比较的oldObject
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogRecordModel {
    /** 方法参数名 */
    String value() default "";
}
