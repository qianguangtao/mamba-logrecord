package com.app.core.mvc.converter;

import cn.hutool.core.util.StrUtil;
import com.app.core.mvc.serialization.EnumDefinition;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Objects;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/10/17 16:39
 * @description: 枚举序反列化器，前端传的string转Enum<? extends EnumDefinition>
 */
public class EnumDeserializer extends JsonDeserializer<Enum<? extends EnumDefinition>> implements ContextualDeserializer {

    private BeanProperty beanProperty;

    public EnumDeserializer(BeanProperty beanProperty) {
        this.beanProperty = beanProperty;
    }

    public EnumDeserializer() {
        super();
    }

    @Override
    public Enum<? extends EnumDefinition> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String value = jsonParser.getValueAsString();
        if (StrUtil.isNotEmpty(value)) {
            Class<?> enumClass = this.beanProperty.getType().getRawClass();
            try {
                Field field = enumClass.getDeclaredField(EnumDefinition.DB_FIELD);
                field.setAccessible(true);
                for (Object enumConstant : enumClass.getEnumConstants()) {
                    Object fieldValue = field.get(enumConstant);
                    if (Objects.equals(fieldValue.toString(), value)) {
                        return (Enum<? extends EnumDefinition>) enumConstant;
                    }
                }
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return null;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {
        this.beanProperty = beanProperty;
        return this;
    }
}
