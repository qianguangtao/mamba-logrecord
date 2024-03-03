package com.app.core.mvc.serialization.desensitization.annotation;

import com.app.core.mvc.serialization.desensitization.strategy.ChineseNameDesensitizationStrategy;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@JacksonAnnotationsInside
@Desensitization(clazz = ChineseNameDesensitizationStrategy.class)
public @interface ChineseNameDesensitization {
}
