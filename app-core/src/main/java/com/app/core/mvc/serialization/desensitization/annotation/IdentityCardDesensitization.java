package com.app.core.mvc.serialization.desensitization.annotation;

import com.app.core.mvc.serialization.desensitization.strategy.IdentityCardDesensitizationStrategy;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@JacksonAnnotationsInside
@Desensitization(clazz = IdentityCardDesensitizationStrategy.class)
public @interface IdentityCardDesensitization {
}
