package com.app.core.mvc.converter;

import com.app.core.mvc.serialization.desensitization.annotation.Desensitization;
import com.app.core.mvc.serialization.desensitization.strategy.DesensitizationStrategy;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 脱敏字段序列化器
 * @author qiangt
 * @since 2022-12-06
 */
public class DesensitizationSerializer extends JsonSerializer<String> implements ContextualSerializer {

    /**
     * 策略对象缓存，避免每次反射生成对象造成的开销
     */
    Map<String, DesensitizationStrategy> strategyCache = new ConcurrentHashMap<>(16);

    private Desensitization desensitization;

    @Override
    public void serialize(final String value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        if (StringUtils.isBlank(value)) {
            gen.writeString(value);
            return;
        }
        if (this.desensitization != null) {
            final Class<? extends DesensitizationStrategy> desensitizationStrategy = this.desensitization.clazz();
            gen.writeString(this.doDesensitization(value, desensitizationStrategy));
        } else {
            gen.writeString(value);
        }
    }

    // public DesensitizationSerializer(Desensitization desensitization) {
    //     this.desensitization = desensitization;
    // }

    private String doDesensitization(final String source, final Class<? extends DesensitizationStrategy> clazz) {
        // final DesensitizationProperties desensitizationProperties = SpringKit.getBean(DesensitizationProperties.class);
        // // 如果配置文件中设置了禁用脱敏，则不做任何操作
        // if (!desensitizationProperties.isEnabled()) {
        //     return source;
        // }
        return this.obtainStrategy(clazz).doDesensitization(source);
    }

    private DesensitizationStrategy obtainStrategy(final Class<? extends DesensitizationStrategy> clazz) {
        Objects.requireNonNull(clazz, "脱敏类不能为空");
        return this.strategyCache.computeIfAbsent(clazz.getName(), k -> BeanUtils.instantiateClass(clazz));
    }

    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider prov, final BeanProperty property) {
        final Desensitization annotation = property.getAnnotation(Desensitization.class);
        if (annotation != null) {
            final DesensitizationSerializer desensitizationSerializer = new DesensitizationSerializer();
            desensitizationSerializer.setDesensitization(annotation);
            return desensitizationSerializer;
        }
        return this;
    }

    public void setDesensitization(final Desensitization desensitization) {
        this.desensitization = desensitization;
    }

}