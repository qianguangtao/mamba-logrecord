package com.app.core.mvc.converter.primary;

import com.app.core.encrypt.processor.argument.PrimaryEncryptProcessor;
import com.app.kit.SpringKit;
import com.fasterxml.jackson.databind.util.StdConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

@Slf4j
public class LongEncryptJsonComponent {

    private static PrimaryEncryptProcessor primaryEncryptProcessor;

    private static PrimaryEncryptProcessor getPrimaryEncryptHandler() {
        if (Objects.isNull(primaryEncryptProcessor)) {
            primaryEncryptProcessor = SpringKit.getBean(PrimaryEncryptProcessor.class);
        }
        return primaryEncryptProcessor;
    }

    public static class Serializer extends StdConverter<Long, String> {
        @Override
        public String convert(final Long value) {
            if (Objects.isNull(value)) {
                return null;
            }
            if (log.isDebugEnabled()) {
                log.debug("加密:{}", value);
            }
            return (String) getPrimaryEncryptHandler().encode(value);
        }
    }

    public static class Deserializer extends StdConverter<String, Long> {
        @Override
        public Long convert(final String value) {
            if (StringUtils.isBlank(value)) {
                return null;
            }
            if (log.isDebugEnabled()) {
                log.debug("解密:{}", value);
            }
            return (Long) getPrimaryEncryptHandler().decode(value);
        }
    }

}
