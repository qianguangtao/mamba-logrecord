package com.app.logrecord.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/12/25 13:44
 * @description: 操作记录方法注解，作用在service接口
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogRecord {

    /** 必填，业务主键spel表达式 */
    String key() default "";

    /** 必填，描述文本 */
    String desc() default "";

    /** 必填，操作类型，使用枚举LogOperate.Type.APPLY */
    String operateType() default "";

    /** 修改时候必填，获取数据库记录的spel表达式。 */
    String method() default "";

    /** 必填，mapperClass返回的entity，新增的时候，用来反射newInstance出空对象进行下一步比较 */
    Class entityClass() default Object.class;
}
