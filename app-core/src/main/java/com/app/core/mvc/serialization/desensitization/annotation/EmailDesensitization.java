package com.app.core.mvc.serialization.desensitization.annotation;

import com.app.core.mvc.serialization.desensitization.strategy.EmailDesensitizationStrategy;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@JacksonAnnotationsInside
@Desensitization(clazz = EmailDesensitizationStrategy.class)
public @interface EmailDesensitization {
}
