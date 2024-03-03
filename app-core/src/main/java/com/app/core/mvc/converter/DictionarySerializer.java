package com.app.core.mvc.converter;

import com.app.core.mvc.serialization.Dictionary;
import com.app.core.mvc.serialization.DictionaryTranslator;
import com.app.kit.SpringKit;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Objects;

/**
 * 字典序列化器
 * @author qiangt
 * @since 2023-05-02
 */
@Slf4j
@NoArgsConstructor
public class DictionarySerializer extends JsonSerializer<String> implements ContextualSerializer {

    public static final String DICT_VALUE_NAME_PATTERN = "%sLabel";

    private BeanProperty beanProperty;
    private Dictionary dictionary;
    private String dictCode;
    private DictionaryTranslator dictTranslator;

    public DictionarySerializer(final Dictionary dictionary) {
        this.dictionary = dictionary;
        if (Objects.nonNull(dictionary)) {
            this.dictCode = dictionary.value();
        }
    }


    public void setDesensitization(final Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public void serialize(final String value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        if (StringUtils.isEmpty(value)) {
            gen.writeObject(null);
            return;
        }
        gen.writeObject(value);
        gen.writeFieldName(this.generateDictValueName(gen.getOutputContext().getCurrentName()));
        gen.writeObject(this.obtainDictValue(this.dictCode, value));
    }

    private String generateDictValueName(final String currentFiledName) {
        return String.format(DICT_VALUE_NAME_PATTERN, currentFiledName);
    }

    /**
     * 通过数据字典类型和value获取name
     * @param dictCode 字典编码
     * @param itemKey  字典key
     * @return 字典key对应的字典值
     */
    private String obtainDictValue(final String dictCode, final String itemKey) {
        if (StringUtils.isEmpty(dictCode)) {
            return null;
        }
        if (StringUtils.isEmpty(itemKey)) {
            return null;
        }
        try {
            return this.getDictTranslator().translate(dictCode, itemKey);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return itemKey;
        }
    }

    private DictionaryTranslator getDictTranslator() {
        if (Objects.isNull(this.dictTranslator)) {
            this.dictTranslator = SpringKit.getBean(DictionaryTranslator.class);
        }
        return this.dictTranslator;
    }

    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider prov, final BeanProperty beanProperty) throws JsonMappingException {
        final Dictionary dict = beanProperty.getAnnotation(Dictionary.class);
        if (Objects.nonNull(dict)) {
            return new DictionarySerializer(dict);
        }
        // return prov.findNullValueSerializer(null);
        this.dictCode = dict.value();
        this.dictionary = dict;
        return this;
    }

}
