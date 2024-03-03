package com.app.core.mvc.converter;

import com.app.core.mvc.serialization.EnumDefinition;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Objects;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/10/17 16:39
 * @description: 枚举序列化器，同时返回枚举的code和comment
 */
@Slf4j
public class EnumSerializer extends JsonSerializer<Enum<? extends EnumDefinition>> implements ContextualSerializer {

    public static final String DICT_VALUE_NAME_PATTERN = "%sLabel";

    private BeanProperty beanProperty;

    public EnumSerializer(final BeanProperty beanProperty) {
        this.beanProperty = beanProperty;
    }

    public EnumSerializer() {
        super();
    }

    @Override
    public void serialize(Enum<? extends EnumDefinition> value, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        if (Objects.isNull(value)) {
            gen.writeObject(null);
            return;
        }
        if (!this.beanProperty.getType().isEnumType()) {
            log.error("当前序列化对象不是枚举: {}:{}", beanProperty.getType().getRawClass().getSimpleName(), beanProperty.getFullName().getSimpleName());
            gen.writeObject(null);
            return;
        }
        if (!EnumDefinition.class.isAssignableFrom(this.beanProperty.getType().getRawClass())) {
            log.error("当前序列化对象未实现EnumComment: {}:{}", beanProperty.getType().getRawClass().getSimpleName(), beanProperty.getFullName().getSimpleName());
            gen.writeObject(null);
            return;
        }
        final EnumDefinition enumComment = (EnumDefinition) value;
        gen.writeObject(enumComment.getCode());
        // json序列化新增字段"{属性名}+Label"
        gen.writeFieldName(this.generateDictValueName(gen.getOutputContext().getCurrentName()));
        gen.writeObject(enumComment.getComment());
    }

    private String generateDictValueName(final String currentFiledName) {
        return String.format(DICT_VALUE_NAME_PATTERN, currentFiledName);
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        this.beanProperty = beanProperty;
        return this;
    }
}
