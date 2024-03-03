package com.app.core.mvc.serialization.desensitization.annotation;

import com.app.core.mvc.converter.DesensitizationSerializer;
import com.app.core.mvc.serialization.desensitization.strategy.DefaultDesensitizationStrategy;
import com.app.core.mvc.serialization.desensitization.strategy.DesensitizationStrategy;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.*;

/**
 * 脱敏注解
 * @author qiangt
 * @since 2022-12-06
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@JacksonAnnotationsInside
@JsonSerialize(using = DesensitizationSerializer.class)
public @interface Desensitization {

    /** 脱敏策略 */
    Class<? extends DesensitizationStrategy> clazz() default DefaultDesensitizationStrategy.class;

}
