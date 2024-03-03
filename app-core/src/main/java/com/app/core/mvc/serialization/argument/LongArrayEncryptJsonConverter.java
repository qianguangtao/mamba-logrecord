package com.app.core.mvc.serialization.argument;

import com.app.core.mvc.converter.primary.LongArrayEncryptJsonComponent;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 加密字段序列化和反序列化
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@JacksonAnnotationsInside
@JsonSerialize(converter = LongArrayEncryptJsonComponent.Serializer.class)
@JsonDeserialize(converter = LongArrayEncryptJsonComponent.Deserializer.class)
public @interface LongArrayEncryptJsonConverter {
}
