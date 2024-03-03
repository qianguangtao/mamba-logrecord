package com.app.logrecord.annotation;

import com.app.logrecord.translator.Translator;
import com.app.logrecord.translator.TranslatorTemplate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/12/25 13:40
 * @description: 操作记录字段，作用在实体类属性
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogRecordField {
    /** 被标注的字段的中文名 */
    String value() default "";

    /** 空值描述 */
    String nullDesc() default "空";

    /** 修改描述翻译（基本数据类型） */
    Class<? extends Translator> translator() default Translator.None.class;

    /** 自定义字段比较模板 */
    Class<? extends TranslatorTemplate> translatorTemplate() default TranslatorTemplate.None.class;
}
